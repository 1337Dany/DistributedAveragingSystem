package ui;

/**
 * The ConsoleView interface provides methods for printing messages to the console.
 */
public interface ConsoleView {
    /**
     * Prints a message to the console.
     *
     * @param str the message to print
     */
    void printMessage(String str);

    /**
     * Prints an error message to the console.
     *
     * @param str the error message to print
     */
    void printErrorMessage(String str);
}