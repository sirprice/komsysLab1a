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
            socket.setSoTimeout(3000);
            while (running) {


                String msg = scanner.nextLine();

                byte[] requesetMsg = msg.getBytes();
                DatagramPacket request = new DatagramPacket(requesetMsg, requesetMsg.length, this.serverAddress, serverPort);
                socket.send(request);
                if (msg.equals("TERMINATE")) {
                    running = false;
                    break;
                }

                try {

                    byte[] responseMsg = new byte[1024];
                    DatagramPacket response = new DatagramPacket(responseMsg, responseMsg.length);
                    socket.receive(response);
                    String receivedMsg = new String(response.getData()).trim();
                    if (receivedMsg.equals("TERMINATE")) {
                        System.out.println("You have timed out from server and your connection to has bean terminated, do you want to reconnect? y/n");
                        if (!scanner.nextLine().toLowerCase().equals("y")) {
                            running = false;
                        }
                    } else {
                        System.out.println("Msg from: " + response.getAddress() + " Msg: " + receivedMsg);
                    }

                } catch (SocketTimeoutException e) {
                    System.out.println("Server not responding and may be offline, do you want to try again? y/n");
                    if (!scanner.nextLine().toLowerCase().equals("y")) {
                        running = false;
                    }
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
