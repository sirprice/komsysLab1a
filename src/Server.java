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


    private int port = 0;
    private SessionState state = SessionState.FREE;

    public Server(int port) {
        this.port = port;

    }

    public void start() {
            int clientPort = 0;
            DatagramSocket udpSocket = null;
            InetAddress clientAddress = null;
        InetAddress current = null;
        GuessGame game = null;
        try {

            udpSocket = new DatagramSocket(this.port);
            byte[] buffer = new byte[1024];
            DatagramPacket request, response;
            boolean runLoop = true;
            while (runLoop) {

                request = new DatagramPacket(buffer,buffer.length);
                udpSocket.receive(request);
                current = request.getAddress();
                System.out.println("Current state: " + state);
                System.out.println("Incoming msg: " + new String(buffer).trim());
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
                        System.out.println("Sending ok...");
                        udpSocket.send(response);
                        state = SessionState.HANDSHAKE;
                        break;
                    }
                    case HANDSHAKE:{
                        if (clientAddress == null || (clientAddress.equals(current) && clientPort == request.getPort()) == false) {
                            System.out.println("Bussy");
                            // busy signal
                            break;
                        }
                        if (!Protocol.checkMsg("START",request)){
                            runLoop = false;
                            System.out.println("Wrong Protocol tag");
                            // implement error handling
                            state = SessionState.FREE;
                            break;
                        }

                        // Do game code
                        byte[] responseMsg = "READY".getBytes();
                        response = new DatagramPacket(responseMsg,responseMsg.length, clientAddress,clientPort);
                        System.out.println("Sending READY...");
                        udpSocket.send(response);
                        state = SessionState.INSESSION;
                        break;
                    }
                    case INSESSION:
                        break;
                    case SHUTDOWN:
                        break;
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
