import java.util.Random;

/**
 * Created by o_0 on 2016-09-07.
 */
public class GuessGame {
    private int secretNumber = 0;
    private boolean flag = false;
    public GuessGame() {
        this.secretNumber = new Random().nextInt(20) + 1 ;
        System.out.println("Secret number is:" + secretNumber);
    }
    public boolean gameCompleted() {
        return flag;
    }
    public String guess(int number) {
        if (number == this.secretNumber) {
            flag = true;
            return "Correct";
        }
        if (number > secretNumber) {
            return "Hi";
        }
        return "low";
    }
}
