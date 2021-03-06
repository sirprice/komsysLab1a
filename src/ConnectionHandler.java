import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by magnus on 2016-09-09.
 */
public class ConnectionHandler {

    /**
     * @param address
     * @param port
     * @return
     */
    public static ConnectionState initialState(InetAddress address, int port) {
        StateNewConnection initialState = new StateNewConnection(address, port);
        return initialState;
    }

    /**
     *
     */
    private static class StateNewConnection extends ConnectionStateAbs {
        private static final int INITIAL_TIMEOUT = 1000;

        public StateNewConnection(InetAddress address, int port) {
            super(address, port, INITIAL_TIMEOUT);
            System.out.println("Entering StateNewConnection:");
        }

        @Override
        public void processIncoming(DatagramPacket request) {
            if (!Protocol.checkMsg("HELLO", request)) {
                System.out.println("Wrong Protocol tag");
                setResponse("ERROR", request.getAddress(), request.getPort());
                setState(null);
                return;
            }
            setResponse("OK", this.clientAddress, this.clientPort);
            setState(new StateHandshake(super.clientAddress, super.clientPort));
        }

        @Override
        public String toString() {
            return "StateNewConnection{}";
        }
    }

    /**
     *
     */
    private static class StateHandshake extends ConnectionStateAbs {
        public static final int HANDSHAKE_TIMEOUT = 9000;

        public StateHandshake(InetAddress address, int port) {
            super(address, port, HANDSHAKE_TIMEOUT);
            System.out.println("Entering StateHandshake:");
        }

        @Override
        public void processIncoming(DatagramPacket request) {
            if (hasTimedout()) {
                setState(null);
                return;
            }
            if ((super.clientAddress.equals(request.getAddress()) && super.clientPort == request.getPort()) == false) {
                setResponse("BUSY", request.getAddress(), request.getPort());
                setState(this);
                return;
            }
            resetTimeout();
            if (!Protocol.checkMsg("START", request)) {
                System.out.println("Wrong Protocol tag");
                setResponse("ERROR", request.getAddress(), request.getPort());
                setState(null);
            } else {
                setResponse("READY", request.getAddress(), request.getPort());
                setState(new InSession(super.clientAddress, super.clientPort));
            }
        }

        @Override
        public String toString() {
            return "StateHandshake{}";
        }
    }

    /**
     *
     */
    private static class InSession extends ConnectionStateAbs {
        public static final int SESSION_TIMEOUT = 15000;

        private GuessGame game = new GuessGame();

        public InSession(InetAddress address, int port) {
            super(address, port, SESSION_TIMEOUT);
            System.out.println("Entering InSession:");
        }

        @Override
        public void processIncoming(DatagramPacket request) {
            setState(this);
            if (hasTimedout()) {
                setResponse("TERMINATE", request.getAddress(), request.getPort());
                setState(null);
                return;
            }
            if (request == null || (super.clientAddress.equals(request.getAddress()) && super.clientPort == request.getPort()) == false) {
                setResponse("BUSY", request.getAddress(), request.getPort());
                return;
            }
            resetTimeout();
            if (Protocol.isGuessCommand(request)) {
                Integer guess = Protocol.parseGuess(request);
                // pars number
                if (guess == null) {
                    setResponse("not a valid guess", request.getAddress(), request.getPort());
                    return;
                }
                String result = game.guess(guess.intValue());
                if (game.gameCompleted()) {
                    result = result + "! Do you want to play again y/n";
                    setState(new PlayAgain(super.clientAddress, super.clientPort));
                }
                setResponse(result, request.getAddress(), request.getPort());
            }else {
                setResponse("not a valid guess command", request.getAddress(), request.getPort());
            }
        }

        @Override
        public String toString() {
            return "InSession{}";
        }
    }
    private static class PlayAgain extends ConnectionStateAbs {
        public static final int QUESTION_TIMEOUT = 10000;
        public PlayAgain(InetAddress address, int port) {
            super(address, port, QUESTION_TIMEOUT);
            System.out.println("Entering PlayAgain:");
        }

        @Override
        public void processIncoming(DatagramPacket request) {
            setState(this);
            if (request == null || (super.clientAddress.equals(request.getAddress()) && super.clientPort == request.getPort()) == false) {
                setResponse("BUSY", request.getAddress(), request.getPort());
                return;
            }
            if (!Protocol.checkMsg("y", request)) {
                System.out.println("Wrong Protocol tag");
                setResponse("TERMINATE", request.getAddress(), request.getPort());
                setState(null);
            } else {
                setResponse("New game", request.getAddress(), request.getPort());
                setState(new InSession(super.clientAddress, super.clientPort));
            }
        }

        @Override
        public String toString() {
            return "PlayAgain{}";
        }
    }


    /**
     *
     */
    private static class ShutDown extends ConnectionStateAbs {
        public static final int SHUTDOWN_TIMEOUT = 2000;

        public ShutDown(InetAddress address, int port) {
            super(address, port, SHUTDOWN_TIMEOUT);
            System.out.println("Entering PlayAgain:");
        }

        @Override
        public void processIncoming(DatagramPacket packet) {
            setResponse("TERMINATE",super.clientAddress,super.clientPort);
            setState(null);
        }

        @Override
        public String toString() {
            return "ShutDown{}";
        }
    }

    /**
     * @param msg
     * @param targetAddress
     * @param targetPort
     * @return
     */
    private static DatagramPacket createPacket(String msg, InetAddress targetAddress, int targetPort) {
        if (msg == null || targetAddress == null || targetPort <= 0) return null;
        byte[] responseMsg = msg.getBytes();
        return new DatagramPacket(responseMsg, responseMsg.length, targetAddress, targetPort);
    }


    /**
     *
     *
     *
     *
     */
    public static abstract class ConnectionStateAbs implements ConnectionState {
        protected InetAddress clientAddress;
        protected int clientPort;
        private ConnectionState state = null;
        private DatagramPacket response = null;
        private long timeoutTimer = 0;
        private long maxTimeout;

        public ConnectionStateAbs(InetAddress clientAddress, int clientPort, long maxTimeout) {
            this.clientPort = clientPort;
            this.clientAddress = clientAddress;
            this.timeoutTimer = System.currentTimeMillis();
            this.maxTimeout = maxTimeout;
        }

        @Override
        public boolean hasTimedout() {
            long diff = System.currentTimeMillis() - timeoutTimer;
            boolean flag = maxTimeout < (diff);
            return flag;
        }

        public void resetTimeout() {
            timeoutTimer = System.currentTimeMillis();
        }

        public void setResponse(String msg, InetAddress targetAddress, int targetPort) {
            this.response = createPacket(msg, targetAddress, targetPort);
        }

        public void setState(ConnectionState cs) {
            this.state = cs;
        }

        @Override
        public void processResult() {
        }

        @Override
        public void respond(DatagramSocket outSocket) throws IOException {
            if (this.response != null) {
                outSocket.send(this.response);
                this.response = null;
            }
        }

        @Override
        public void sendMsgToClient(DatagramSocket outSocket, String msg) throws IOException {
            outSocket.send(createPacket(msg, clientAddress, clientPort));
        }

        @Override
        public ConnectionState nextState() {
            return state;
        }
    }

}
