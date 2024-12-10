package ui;

/**
 * The View class implements the ConsoleView interface and provides
 * methods for printing messages and error messages to the console.
 */
public class View implements ConsoleView {
    /**
     * Prints a message to the console.
     *
     * @param str the message to print
     */
    @Override
    public void printMessage(String str) {
        System.out.println(str);
    }

    /**
     * Prints an error message to the console.
     *
     * @param str the error message to print
     */
    @Override
    public void printErrorMessage(String str) {
        System.err.println(str);
    }
}