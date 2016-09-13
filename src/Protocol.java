import java.net.DatagramPacket;
import java.util.StringTokenizer;

/**
 * Created by cj on 07/09/16.
 */
public class Protocol {

    public static final String GUESS = "GUESS";
    public static final String TERMINATE = "TERMINATE";
    private static String delimiter = " ";

    public static boolean checkMsg(String tag, DatagramPacket packet){
        String remoteMsg = new String(packet.getData(),0,packet.getLength());
        System.out.println(remoteMsg);
        return remoteMsg.equals(tag);
    }

    public static boolean isTerminateCommand(DatagramPacket packet) {
        String input = new String(packet.getData()).trim();
        int i = input.indexOf(' ');
        String command = input.substring(0, i);

        //String rest = input.substring(i);

        return command.toUpperCase().equals(TERMINATE);
    }

    public static boolean isGuessCommand(DatagramPacket packet) {
        String input = new String(packet.getData()).trim();

        StringTokenizer tokenizer = new StringTokenizer(input,delimiter);
        if (!tokenizer.hasMoreTokens()){
            return false;
        }

        String sp = tokenizer.nextToken();
        System.out.println("Command is " + sp);

        return sp.toUpperCase().equals(GUESS);
    }

    public static Integer parseGuess(DatagramPacket packet){
        Integer number;
        String input = new String(packet.getData()).trim();


        String sp = null;
        StringTokenizer tokenizer = new StringTokenizer(input,delimiter);
        if (!tokenizer.hasMoreTokens()){
            return null;
        }else{
            sp = tokenizer.nextToken();
            if (!tokenizer.hasMoreTokens())
                return null;
            sp = tokenizer.nextToken();
        }

        System.out.println("Guess is " + sp);

        try {
            number = Integer.parseInt(sp);
        }catch (NumberFormatException e){
            e.printStackTrace();
            number = null;
        }
        return number;
    }
}
