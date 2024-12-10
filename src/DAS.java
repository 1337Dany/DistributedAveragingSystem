import domain.DASPresenter;
import ui.View;

public class DAS {

    public DAS(int port, int number) {
        View view = new View();
        try {
            new DASPresenter(view, port, number);
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            view.printErrorMessage("Incorrect input");
        }
    }

    public static void main(String[] args) {
            new DAS(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    }
}