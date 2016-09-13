import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by magnus on 2016-09-09.
 */
public class ConectionHandler {
    public static ConnectionState newState(InetAddress address, int port) {
        StateNewConn stateNewConn = new StateNewConn(address, port);
        return stateNewConn;
    }

    private static class StateNewConn extends ConnectionStateAbs {
        public StateNewConn(InetAddress address, int port) { super(address,port); }

        @Override
        public void processIncoming(DatagramPacket request) {
            if (!Protocol.checkMsg("HELLO", request)) {
                System.out.println("Wrong Protocol tag");
                setResponse("ERROR", request.getAddress(), request.getPort());
                setState(null);
                return;
            }
            setResponse("OK", this.address, this.port);
            setState(new HandsakeConn(super.address,super.port));
        }
    }

    private static class HandsakeConn extends ConnectionStateAbs {
        public HandsakeConn(InetAddress address, int port)  { super(address,port); }
        @Override
        public void processIncoming(DatagramPacket request) {
            if ( (super.address.equals(request.getAddress()) && super.port == request.getPort()) == false) {
                setResponse("BUSY", request.getAddress(), request.getPort());
                setState(this);
                return;
            }
            if (!Protocol.checkMsg("START", request)) {
                System.out.println("Wrong Protocol tag");
                setResponse("ERROR", request.getAddress(), request.getPort());
                setState(null);
            }else {
                setResponse("READY", request.getAddress(), request.getPort());
                setState(new InSession(super.address,super.port));
            }
        }
    }

    private static class InSession extends ConnectionStateAbs {
        private GuessGame game = new GuessGame();
        public InSession(InetAddress address, int port)  { super(address,port); }

        @Override
        public void processIncoming(DatagramPacket request) {
            setState(this);
            if (request == null || (super.address.equals(request.getAddress()) && super.port == request.getPort()) == false) {
                setResponse("BUSY", request.getAddress(), request.getPort());
            }
            if (Protocol.isGuessCommand(request)) {
                Integer guess = Protocol.parseGuess(request);
                // pars number
                if (guess == null ) {
                    setResponse("not a valid guess", request.getAddress(), request.getPort());
                    return;
                }
                String result = game.guess(guess.intValue());

                setResponse(result, request.getAddress(), request.getPort());
            }
        }
    }

    private static class ShutDown extends ConnectionStateAbs {
        public ShutDown(InetAddress address, int port)  { super(address,port); }

        @Override
        public void processIncoming(DatagramPacket packet) {
            setState(null);
        }
    }

    private static DatagramPacket createPacket(String msg,InetAddress targetAddress, int targetPort) {
        if (msg == null || targetAddress == null || targetPort <= 0) return null;
        byte[] responseMsg = msg.getBytes();
        return new DatagramPacket(responseMsg, responseMsg.length, targetAddress, targetPort);
    }

    private static abstract class ConnectionStateAbs implements ConnectionState {
        protected InetAddress address;
        protected int port;
        private ConnectionState state = null;
        private DatagramPacket response = null;
        public ConnectionStateAbs(InetAddress address, int port) {
            this.port = port;
            this.address = address;
        }

        public void setResponse(String msg, InetAddress targetAddress, int targetPort) {
            this.response = createPacket(msg, targetAddress, targetPort);
        }
        public void setState(ConnectionState cs) { this.state = cs;}

        @Override
        public void processResult() {}

        @Override
        public void respond(DatagramSocket outSocket) throws IOException {
            if (this.response != null) {
                outSocket.send(this.response);
                this.response = null;
            }
        }

        @Override
        public ConnectionState nextState() { return state; }
    }

}
