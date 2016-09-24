import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by magnus on 2016-09-09.
 */
public interface ConnectionState {
    /**
     * Process a incoming packet
     * @param packet to be processed
     */
    void processIncoming(DatagramPacket packet);

    /**
     * Can be used if the results need to be handled
     */
    void processResult();

    /**
     * This will send the respnse for this state
     * @param outSocket the socket to use to send data
     * @throws IOException if there is a connection error
     */
    void respond(DatagramSocket outSocket) throws IOException;

    /**
     * Check if this state has timedout
     * @return
     */
    boolean hasTimedout();

    /**
     * Send a msg to the client direktly
     * @param outSocket socket to use
     * @param msg message to send
     * @throws IOException
     */
    void sendMsgToClient(DatagramSocket outSocket, String msg) throws IOException;

    /**
     * This will return the next state or the same depending on its rules
     * @return next/current state or null
     */
    ConnectionState nextState();
}
