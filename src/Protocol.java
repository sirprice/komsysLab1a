import java.net.DatagramPacket;

/**
 * Created by cj on 07/09/16.
 */
public class Protocol {

    public static boolean checkMsg(String tag, DatagramPacket packet){
        String remoteMsg = new String(packet.getData(),0,packet.getLength());
        System.out.println(remoteMsg);
        return remoteMsg.equals(tag);
    }
}
