package gestionsErreurs;

/**
 * Represent an error in a traitement of the application.
 */
public class TraitementException extends Exception {

    private static final long serialVersionUID = 4049573491969878977L;

    public TraitementException(String message) {
        super(message);
    }

    /**
     * Provide associated message that should be the message ID.
     * @return the message defined for the specified error ID
     */
    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
