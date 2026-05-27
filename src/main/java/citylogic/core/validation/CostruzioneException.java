package citylogic.core.validation;


/**
 * Eccezione personalizzata lanciata quando una regola di costruzione non viene rispettata.
 */
public class CostruzioneException extends Exception {
    public CostruzioneException(String message) {
        super(message);
    }
}
