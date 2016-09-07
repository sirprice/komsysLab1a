import java.util.Random;

/**
 * Created by o_0 on 2016-09-07.
 */
public class GuessGame {
    private int secretNumber = 0;
    public GuessGame() {
        this.secretNumber = new Random().nextInt(20) + 1 ;
    }

    public String guess(int number) {
        if (number == this.secretNumber) {
            return "Correct";
        }
        if (number > secretNumber) {
            return "Hi";
        }
        return "low";
    }
}
