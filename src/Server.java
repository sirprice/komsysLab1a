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


    private int serverPort = 0;
    private SessionState state = SessionState.FREE;
    int clientPort = 0;
    DatagramSocket udpSocket = null;
    InetAddress clientAddress = null;

    public Server(int port) {
        this.serverPort = port;

    }

    private void sendMsgToClient(String msg) throws IOException {
        if (this.udpSocket == null || clientPort <= 0) return;
        byte[] responseMsg = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(responseMsg,responseMsg.length, clientAddress,clientPort);
        System.out.println("Sending "+ msg + "...");
        udpSocket.send(packet);
    }

    private void sendMsgTo(String msg,InetAddress targetAddress,int targetPort) throws IOException {
        if (this.udpSocket == null || targetAddress == null || targetPort <= 0) return;
        byte[] responseMsg = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(responseMsg,responseMsg.length, targetAddress,targetPort);
        System.out.println("Sending "+ msg + "...");
        udpSocket.send(packet);
    }

    public void start() {
        InetAddress currentAddr = null;
        int currentPort = 0;
        GuessGame game = null;
        try {

            udpSocket = new DatagramSocket(this.serverPort);
            byte[] buffer = new byte[1024];
            DatagramPacket request, response;
            boolean runLoop = true;
            while (runLoop) {

                request = new DatagramPacket(buffer,buffer.length);
                udpSocket.receive(request);
                currentAddr = request.getAddress();
                currentPort = request.getPort();
                System.out.println("Current state: " + state);
                System.out.println("Incoming msg: " + new String(buffer).trim());
                switch (state){

                    case FREE:{
                        if (!Protocol.checkMsg("HELLO",request)){
                            runLoop = false;
                            System.out.println("Wrong Protocol tag");
                            sendMsgTo("ERROR",currentAddr,currentPort);
                            // implement error handling
                            break;
                        }

                        clientAddress = currentAddr;
                        clientPort = currentPort;
                        sendMsgToClient("OK");
                        state = SessionState.HANDSHAKE;
                        break;
                    }
                    case HANDSHAKE:{
                        if (clientAddress == null || (clientAddress.equals(currentAddr) && clientPort == request.getPort()) == false) {
                            System.out.println("Bussy");
                            sendMsgTo("ERROR",currentAddr,currentPort);
                            // busy signal
                            break;
                        }
                        if (!Protocol.checkMsg("START",request)){
                            runLoop = false;
                            System.out.println("Wrong Protocol tag");
                            sendMsgToClient("ERROR");
                            // implement error handling
                            state = SessionState.FREE;
                            break;
                        }

                        // Do game code
                        game = new GuessGame();
                        sendMsgToClient("READY");
                        state = SessionState.INSESSION;
                        break;
                    }
                    case INSESSION:
                        if (game == null) {
                            sendMsgTo("ERROR",currentAddr,currentPort);
                            state = SessionState.FREE;
                            break;
                        }
                        if (clientAddress == null || (clientAddress.equals(currentAddr) && clientPort == request.getPort()) == false) {
                            System.out.println("Bussy");
                            sendMsgTo("ERROR",currentAddr,currentPort);
                            //sendMsgToClient("BUSY");
                            // busy signal
                            break;
                        }
                        // pars number
                        int guess = 4;
                        String result = game.guess(guess);
                        sendMsgToClient(result);
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
