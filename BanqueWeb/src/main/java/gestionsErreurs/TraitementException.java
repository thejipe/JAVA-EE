package gestionsErreurs;

public class TraitementException extends Exception {

    private static final long serialVersionUID = 4049573491969878977L;

    public TraitementException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
