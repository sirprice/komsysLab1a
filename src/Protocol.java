import java.net.DatagramPacket;

/**
 * Created by cj on 07/09/16.
 */
public class Protocol {

    public static final String GUESS = "GUESS";
    public static final String TERMINATE = "TERMINATE";

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
        System.out.println("isGuessCommand: input:" + input);
        int i = input.indexOf(' ');
        String command = input.substring(0, i);
        System.out.println("isGuessCommand: command:" + command);
        System.out.println("isGuessCommand: input2:" + input);

        //String rest = input.substring(i);

        return command.toUpperCase().equals(GUESS);
    }

    public static Integer parseGuess(DatagramPacket packet){
        Integer number;
        String input = new String(packet.getData()).trim();

        System.out.println("Input: "+input);
        int i = input.indexOf(' ');
        String command = input.substring(0, i);
        System.out.println("Command: "+command);


        i = input.indexOf(' ');
        String second = input.substring((input.indexOf((command)+command.length())+1), i);
        System.out.println("Second word: "+second);

        try {
            number = Integer.parseInt(second);
        }catch (NumberFormatException e){
            e.printStackTrace();
            number = null;
        }
        return number;
    }
}
