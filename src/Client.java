import java.io.IOException;
import java.net.*;
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
        boolean running = true;
        try {

            socket = new DatagramSocket();
            socket.setSoTimeout(2000);
            while (running) {


                String msg = scanner.next();
                if (msg.equals("TERMINATE")){
                    running = false;
                }

                byte[] requesetMsg = msg.getBytes();
                DatagramPacket request = new DatagramPacket(requesetMsg, requesetMsg.length, this.serverAddress, serverPort);
                socket.send(request);

                try {

                    byte[] responseMsg = new byte[1024];
                    DatagramPacket response = new DatagramPacket(responseMsg, responseMsg.length);
                    socket.receive(response);
                    System.out.println("Msg from: " + response.getAddress() + " Msg: " + new String(response.getData()).trim());
                } catch (SocketTimeoutException e) {
                    System.out.println("Server not responding, try again");
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) socket.close();
        }

    }

}
