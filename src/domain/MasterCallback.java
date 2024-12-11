package domain;

/**
 * The MasterCallback interface provides a method for handling messages from the Master.
 */
public interface MasterCallback {
    /**
     * Handles a message from the Master.
     *
     * @param string the message to handle
     */
    void masterMessage(String string);
    /**
     * Handles an error message from the Master.
     *
     * @param string the message to handle
     */
    void masterErrorMessage(String string);
}