import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by cj on 07/09/16.
 */
public class Server {
    private int serverPort = 0;

    public Server(int port) {
        this.serverPort = port;

    }

    public void start() {
        DatagramSocket udpSocket = null;
        try {
            udpSocket = new DatagramSocket(this.serverPort);

            DatagramPacket request;
            boolean runLoop = true;
            ConnectionState clientState = null;

            while (runLoop) {
                byte[] buffer = new byte[1024];
                request = new DatagramPacket(buffer, buffer.length);
                udpSocket.receive(request);
                if (clientState == null) {
                    clientState = ConectionHandler.newState(request.getAddress(), request.getPort());
                }
                clientState.processIncoming(request);
                clientState.processResult();
                clientState.respond(udpSocket);
                clientState = clientState.nextState();

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
