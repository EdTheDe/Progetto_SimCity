package citylogic.core.validation;

/**
 * Eccezione checked personalizzata.
 * Viene lanciata in modo esplicito quando una regola di posizionamento degli edifici viene violata
 * (es. fondi insufficienti, mancanza di servizi).
 */
public class CostruzioneException extends Exception {
    public CostruzioneException(String message) {
        super(message); // Passa il messaggio d'errore alla superclasse Exception
    }
}