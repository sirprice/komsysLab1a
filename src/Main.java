import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Created by cj on 07/09/16.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        if (args[0].equals("server")){
            Server server = new Server(Integer.parseInt(args[1]));
            server.start();
        } else if (args[0].equals("client")){
            InetSocketAddress addr = new InetSocketAddress(args[1],Integer.parseInt(args[2]));
            Client client = new Client(addr.getAddress(),addr.getPort());
            client.start();
        }
    }
}
