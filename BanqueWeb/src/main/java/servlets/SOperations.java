package servlets;

import gestionsErreurs.TraitementException;
import javaBeans.BOperations;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * This class represent the servlet of the "BanqueWeb" application.
 */
@WebServlet(urlPatterns = {"/Compte/SaisieNoDeCompte", "/Compte/Operations"})
public class SOperations extends HttpServlet {

    /**
     * This enum is used to know the current step of the process in a POST operation
     */
    enum MethodMode {
        SAISIE, CONSULTATION, FIN_TRAITEMENT
    }

    private final static String [] CLIENT_ATTRIBUTES =
            {"nom", "prenom", "solde", "op", "entier", "decimal", "result", "status", "listeOp",
                    "aInit", "mInit", "jInit", "aFinal", "mFinal", "jFinal", "erreur"};
    private BOperations bop;
    private DataSource ds;

    /**
     * Used to init the servlet when it's start.
     * @throws ServletException propagation of the super method "init"
     */
    @Override
    public void init() throws ServletException {
        super.init();
        ds = (DataSource) getServletContext().getAttribute("dataSource");
    }

    /**
     * Method that it's call when a POST request is received.
     * @param request Request containing all informations send by the user.
     * @param response Response containing all informations send to the user.
     * @throws ServletException Propagate exception of the forward method of {@link RequestDispatcher}
     * @throws IOException Propagate exception of the forward method of {@link RequestDispatcher}
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        MethodMode statut;
        if (action != null && action.equals("finTraitement")){
            statut = MethodMode.FIN_TRAITEMENT;
        } else {
            statut = MethodMode.valueOf((String) session.getAttribute("sesOPE"));
        }
        switch (statut) {
            case SAISIE:
                try {
                    bop = new BOperations();
                    consultation(request);
                    session.setAttribute("sesOPE", MethodMode.CONSULTATION.toString());
                    session.setAttribute("statut","first_call");
                    getServletContext().getRequestDispatcher("/jsp/JOperations.jsp").forward(request, response);
                } catch (TraitementException e) {
                    session.setAttribute("sesOPE", MethodMode.SAISIE.toString());
                    request.setAttribute("Erreur", e.getMessage());
                    getServletContext().getRequestDispatcher("/jsp/JSaisieNoDeCompte.jsp").forward(request, response);
                }
                break;
            case CONSULTATION:

                try {
                    process_Operation(action, session, request, response);
                } catch (TraitementException e) {
                    process_Error(action, session, request, response, e);
                }
                break;
            case FIN_TRAITEMENT:
                for(String attribute: CLIENT_ATTRIBUTES) {
                    session.removeAttribute(attribute);
                }
                session.setAttribute("noCompte", null);
                session.setAttribute("sesOPE", MethodMode.SAISIE.toString());
                doGet(request, response);
                break;
        }
    }

    /**
     * Method that it's call when a GET request is received.
     * @param request Request containing all informations send by the user.
     * @param response Response containing all informations send to the user.
     * @throws ServletException Propagate exception of the forward method of {@link RequestDispatcher}
     * @throws IOException Propagate exception of the forward method of {@link RequestDispatcher}
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.setAttribute("sesOPE", MethodMode.SAISIE.toString());
        getServletContext().getRequestDispatcher("/jsp/JSaisieNoDeCompte.jsp").forward(request, response);
    }

    /**
     * Search in database some account's informations about a specific noCompte and put them in a session to expose in user interface.
     * @param req Request containing all informations send by the user.
     * @throws TraitementException If the noCompte is incorrectly filled in or if the account does not exist.
     */
    private void consultation(HttpServletRequest req) throws TraitementException {
        var noCompte = req.getParameter("no_compt");
        HttpSession session = req.getSession();
        if (verifNoDeCompte(noCompte)) {
            bop.ouvrirConnexion(ds);
            bop.setNoCompte(noCompte);
            bop.consulter();
            session.setAttribute("noCompte", noCompte);
            session.setAttribute("nom", bop.getNom());
            session.setAttribute("prenom", bop.getPrenom());
            session.setAttribute("solde", bop.getSolde());
            bop.fermerConnexion();
        }
    }

    /**
     * Update the bank account according to a transaction and a value entered by the user.
     * @param req Request containing all informations send by the user.
     * @return {@link HttpServletRequest} containing the result message of the succes of the transaction.
     * @throws TraitementException Propagate {@link TraitementException} of the {@link BOperations} methods
     */
    private HttpServletRequest traitement(HttpServletRequest req) throws TraitementException {
        bop.ouvrirConnexion(ds);
        bop.setOp(req.getParameter("op"));
        var value = req.getParameter("entier") + "." + req.getParameter("decimal");
        if (verifValeur(value)){
            bop.setValeur(value);
            bop.traiter();
            var result = "Operation -> " + bop.getAncienSolde() + " " + req.getParameter("op") + " " + value + "\n=> solde = " + bop.getNouveauSolde();
            req.setAttribute("result", result);
            req.getSession().setAttribute("solde", bop.getNouveauSolde());
            bop.fermerConnexion();
        }
        return req;
    }

    /**
     * Get all transactions between 2 dates set abose the request.
     * @param req Request containing all informations send by the user.
     * @return {@HttpServletRequest} containing the result message of the succes of the transaction.
     * @throws TraitementException If the set dates are incorrect, or if there is no operation during the specified interval
     */
    private HttpServletRequest listeOperations(HttpServletRequest req) throws TraitementException {
        var dateInf = String.format("%s-%s-%s", req.getParameter("aInit"), req.getParameter("mInit"), req.getParameter("jInit"));
        var dateSup = String.format("%s-%s-%s", req.getParameter("aFinal"), req.getParameter("mFinal"), req.getParameter("jFinal"));
        if(!verifDates(dateInf, dateSup)) {
            throw new TraitementException("31");
        }
        bop.ouvrirConnexion(ds);
        bop.setDateInf(dateInf);
        bop.setDateSup(dateSup);
        bop.listerParDates();
        ArrayList<String[]> result = bop.getOperationsParDates();
        if(result.isEmpty()) {
            throw new TraitementException("32");
        }
        req.setAttribute("listeOp", result);
        bop.fermerConnexion();
        return req;
    }

    /**
     * Used to check that "dateInf" is lower than "dateSup"
     * @param dateInf Upper bound of the time interval used to check operations history
     * @param dateSup Lower bound of the time interval used to check operations history
     * @return True if the dates are correct, false otherwise
     */
    private boolean verifDates(String dateInf, String dateSup) {
        var valuesInf = dateInf.split("-");
        var valuesSup = dateSup.split("-");
        for(int index = 0; index < valuesInf.length; index++) {
            if(Integer.parseInt(valuesInf[index]) > Integer.parseInt(valuesSup[index])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Used to check that "noCompte" is in a valid account number format.
     * @param noCOmpte The specified account number send by the user
     * @return True if the account number is in a correct format,
     * @throws TraitementException If the specified account number is in a wrong format
     */
    private boolean verifNoDeCompte(String noCOmpte) throws TraitementException {
        if (noCOmpte.length() == 4){
            if (Pattern.matches("^[0-9]*$", noCOmpte)) return true;
            else throw new TraitementException("4");
        } else throw new TraitementException("5");
    }

    /**
     * Used to check if the value specified by the user is correct.
     * @param value specified by the user and represent the amount that need to be added or substract to the current account balance
     * @return True if the value is correct, false otherwise
     * @throws TraitementException If the specified value is incorrect
     */
    private boolean verifValeur(String value) throws TraitementException {
        if (!value.equals(".")){
            if (Pattern.matches("^\\d*[.]\\d{2}", value)) return true;
            else throw new TraitementException("25");
        }
        else throw new TraitementException("26");
    }

    /**
     * Call the right method depending on the specified action.
     * @param action Specifies in which case we are and which JSP forward
     * @param session Provide the current {@link HttpSession} of the user that contains attributs
     * @param req Request containing all informations send by the user.
     * @param res Response containing all informations send to the user.
     * @throws TraitementException If there is an error during process or if the used parameters are incorrect
     * @throws ServletException Propagate exception of the forward method of {@link RequestDispatcher}
     * @throws IOException Propagate exception of the forward method of {@link RequestDispatcher}
     */
    private void process_Operation(String action, HttpSession session, HttpServletRequest req, HttpServletResponse res) throws TraitementException, ServletException, IOException {
        HttpServletRequest request;
        switch (action) {
            case "traitement":
                request = traitement(req);
                session.setAttribute("statut","succes_traitement");
                getServletContext().getRequestDispatcher("/jsp/JOperations.jsp").forward(request, res);
                break;
            case "extraction":
                request = listeOperations(req);
                getServletContext().getRequestDispatcher("/jsp/JListeOpérations.jsp").forward(request, res);
                break;
            case "retour":
                session.setAttribute("statut","liste_return");
                getServletContext().getRequestDispatcher("/jsp/JOperations.jsp").forward(req, res);
                break;
            default:
        }
    }

    /**
     * Used to set error attribut with the correct id of the throws {@link TraitementException}.
     * @param action Specifies in which case we are and if it's necessary to forward a specific JSP
     * @param session Provide the current {@link HttpSession} of the user that contains attributs
     * @param req Request containing all informations send by the user
     * @param res Response containing all informations send to the user
     * @param e Exception throw that containing the id of the error
     * @throws ServletException Propagate exception of the forward method of {@link RequestDispatcher}
     * @throws IOException Propagate exception of the forward method of {@link RequestDispatcher}
     */
    private void process_Error(String action, HttpSession session, HttpServletRequest req, HttpServletResponse res, TraitementException e) throws ServletException, IOException {
        req.setAttribute("Erreur", e.getMessage());
        session.setAttribute("statut","error");
        switch (action) {
            case "traitement":
            case "extraction":
                getServletContext().getRequestDispatcher("/jsp/JOperations.jsp").forward(req, res);
                break;
            case "retour": break;
        }
    }
}
