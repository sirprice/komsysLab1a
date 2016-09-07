import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

/**
 * Created by cj on 07/09/16.
 */
public class Client {
    InetAddress serverAddress;
    int serverPort;
    public Client(InetAddress address, int port) {
        this.serverAddress = address;
        this.serverPort = port;
    }

    public void start() {
        DatagramSocket socket = null;
        Scanner scanner = new Scanner(System.in);
        try {
            socket = new DatagramSocket();
            String msg = scanner.next();
            byte[] requesetMsg = msg.getBytes();
            DatagramPacket request = new DatagramPacket(requesetMsg,requesetMsg.length,this.serverAddress,serverPort);
            socket.send(request);


            byte[] responseMsg = new byte[1024];
            DatagramPacket response = new DatagramPacket(responseMsg,responseMsg.length);
            socket.receive(response);
            System.out.println("Msg from: " + response.getAddress() + " Msg: " + new String(response.getData()));

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(socket != null) socket.close();
        }

    }

}
