package servlets;

import gestionsErreurs.TraitementException;
import javaBeans.BOperations;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * This class represent
 */
@WebServlet(urlPatterns = {"/Compte/SaisieNoDeCompte", "/Compte/Operations"})
public class SOperations extends HttpServlet {

    /**
     *
     */
    enum MethodMode {
        SAISIE, CONSULTATION, FIN_TRAITEMENT
    }

    private final static String [] CLIENT_ATTRIBUTES =
            {"nom", "prenom", "solde", "op", "entier", "decimal", "result", "status", "listeOp",
                    "aInit", "mInit", "jInit", "aFinal", "mFinal", "jFinal", "erreur"};
    private BOperations bop;

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        MethodMode statut = MethodMode.valueOf((String) session.getAttribute("sesOPE"));
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
                var action = request.getParameter("action");
                try {
                    process_Operation(action, session, request, response);
                } catch (TraitementException e) {
                    process_Error(action, session, request, response, e);
                }
                break;
            case FIN_TRAITEMENT:
                // TODO : verif and finish fill the CLIENT_ATTRIBUTES array
                for(String attribute: CLIENT_ATTRIBUTES) {
                    session.removeAttribute(attribute);
                }
                session.setAttribute("noCompte", null);
                session.setAttribute("sesOPE", MethodMode.SAISIE.toString());
                doPost(request, response);
                break;
        }
    }

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.setAttribute("sesOPE", MethodMode.SAISIE.toString());
        getServletContext().getRequestDispatcher("/jsp/JSaisieNoDeCompte.jsp").forward(request, response);
    }

    /**
     * Search in database some account's informations about a specific noCompte and put them in a session to expose in user interface.
     * @param req Request containing all informations send by the user.
     * @throws TraitementException if the noCompte is incorrectly filled in or if the account does not exist.
     */
    private void consultation(HttpServletRequest req) throws TraitementException {
        var noCompte = req.getParameter("no_compt");
        HttpSession session = req.getSession();
        if (verifNoDeCompte(noCompte)) {
            bop.ouvrirConnexion();
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
     * @return {@HttpServletRequest} containing the result message of the succes of the transaction.
     * @throws TraitementException
     */
    private HttpServletRequest traitement(HttpServletRequest req) throws TraitementException {
        bop.ouvrirConnexion();
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
     * @throws TraitementException
     */
    private HttpServletRequest listeOperations(HttpServletRequest req) throws TraitementException {
        bop.ouvrirConnexion();
        var dateInf = String.format("%s-%s-%s", req.getParameter("aInit"), req.getParameter("mInit"), req.getParameter("jInit"));
        var dateSup = String.format("%s-%s-%s", req.getParameter("aFinal"), req.getParameter("mFinal"), req.getParameter("jFinal"));
        if(!verifDates(dateInf, dateSup)) {
            throw new TraitementException("31");
        }
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
     *
     * @param noCOmpte
     * @return
     * @throws TraitementException
     */
    private boolean verifNoDeCompte(String noCOmpte) throws TraitementException {
        if (noCOmpte.length() == 4){
            if (Pattern.matches("^[0-9]*$", noCOmpte)) return true;
            else throw new TraitementException("4");
        } else throw new TraitementException("5");
    }

    /**
     *
     * @param value
     * @return
     * @throws TraitementException
     */
    private boolean verifValeur(String value) throws TraitementException {
        if (!value.equals(".")){
            if (Pattern.matches("^\\d*[.]\\d{2}", value)) return true;
            else throw new TraitementException("25");
        }
        else throw new TraitementException("26");
    }

    /**
     *
     * @param action
     * @param session
     * @param req
     * @param res
     * @throws TraitementException
     * @throws ServletException
     * @throws IOException
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
     *
     * @param action
     * @param session
     * @param req
     * @param res
     * @param e
     * @throws ServletException
     * @throws IOException
     */
    private void process_Error(String action, HttpSession session, HttpServletRequest req, HttpServletResponse res, TraitementException e) throws ServletException, IOException {
        req.setAttribute("Erreur", e.getMessage());
        session.setAttribute("statut","error");
        switch (action) {
            case "traitement":
                getServletContext().getRequestDispatcher("/jsp/JOperations.jsp").forward(req, res);
                break;
            case "extraction":
                getServletContext().getRequestDispatcher("/jsp/JListeOpérations.jsp").forward(req, res);
                break;
            case "retour": break;
        }
    }

}
