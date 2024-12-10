package domain;

/**
 * The SlaveCallback interface provides methods for handling messages and errors from the Slave.
 */
public interface SlaveCallback {
    /**
     * Handles a message from the Slave.
     *
     * @param string the message to handle
     */
    void slaveMessage(String string);

    /**
     * Handles an error message from the Slave.
     *
     * @param string the error message to handle
     */
    void slveErrorMessage(String string);
}