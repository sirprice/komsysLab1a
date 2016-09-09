import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by magnus on 2016-09-09.
 */
public interface ConnectionState {
    void processIncoming(DatagramPacket packet);
    void processResult();
    void respond(DatagramSocket outSocket) throws IOException;
    ConnectionState nextState();
}
