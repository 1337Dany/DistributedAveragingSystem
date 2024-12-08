package ui;

public class View implements ConsoleView{
    @Override
    public void printMessage(String str) {
        System.out.println(str);
    }

    @Override
    public void printErrorMessage(String str) {
        System.err.println(str);
    }
}
