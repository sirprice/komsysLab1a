import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Created by cj on 07/09/16.
 */
public class Server {
    private static final String TERMINATE = "TERMINATE";
    private int serverPort = 0;
    private static final int TIMEOUT = 1000;

    public Server(int port) {
        this.serverPort = port;

    }

    public void start() {
        DatagramSocket udpSocket = null;
        try {
            udpSocket = new DatagramSocket(this.serverPort);
            udpSocket.setSoTimeout(TIMEOUT);

            DatagramPacket request;
            boolean runLoop = true;
            ConnectionState clientState = null;

            while (runLoop) {
                byte[] buffer = new byte[1024];
                request = new DatagramPacket(buffer, buffer.length);

                try {
                    udpSocket.receive(request);

                    if (clientState == null) {
                        clientState = ConnectionHandler.initialState(request.getAddress(), request.getPort());
                    }
                    clientState.processIncoming(request);
                    clientState.processResult();
                    clientState.respond(udpSocket);
                    clientState = clientState.nextState();
                } catch (SocketTimeoutException e) {
                    if (clientState != null && clientState.hasTimedout()) {
                        clientState.sendMsgToClient(udpSocket, TERMINATE);
                        clientState = null;
                    }
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
