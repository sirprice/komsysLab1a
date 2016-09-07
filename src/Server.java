import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by cj on 07/09/16.
 */
public class Server {

    private enum SessionState {FREE, HANDSHAKE, INSESSION, SHUTDOWN}


    private int port;
    private SessionState state = SessionState.FREE;

    public Server(int port) {
        this.port = port;

    }

    public void start() {
            int clientPort;
            DatagramSocket udpSocket = null;
            InetAddress clientAddress, current;
        try {

            udpSocket = new DatagramSocket(this.port);
            byte[] buffer = new byte[1024];
            DatagramPacket request, response;
            boolean runLoop = true;
            while (runLoop) {

                request = new DatagramPacket(buffer,buffer.length);
                udpSocket.receive(request);
                current = request.getAddress();

                switch (state){

                    case FREE:{
                        if (!Protocol.checkMsg("HELLO",request)){
                            runLoop = false;
                            System.out.println("Wrong Protocol tag");
                            // implement error handling
                            break;
                        }

                        clientAddress = current;
                        clientPort = request.getPort();

                        byte[] responseMsg = "OK".getBytes();

                        response = new DatagramPacket(responseMsg,responseMsg.length, clientAddress,clientPort);
                        udpSocket.send(response);
                        break;
                    }
                    default:
                        runLoop = false;
                        System.out.println("Bad state:" + state.toString());
                        break;
                }
            }
        } catch (SocketException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (udpSocket != null)
                udpSocket.close();
        }
    }
}
