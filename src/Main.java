import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by cj on 07/09/16.
 */
public class Main {



    public static void main(String[] args) {
        System.out.println("Hello World!");

        if (args[0].equals("server")){
            Server server = new Server(Integer.parseInt(args[1]));


            while (true){






            }


        } else if (args[0].equals("client")){




        }
    }
}
