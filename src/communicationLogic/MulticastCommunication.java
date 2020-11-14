package communicationLogic;

import businessLogic.ServerData;

import java.io.Serializable;

public class MulticastCommunication implements Serializable {
    private int sendingServer;
    private String message;
    private ServerData serverData;

    public MulticastCommunication(int sendingServer, String message, ServerData serverData) {
        this.sendingServer = sendingServer;
        this.message = message;
        this.serverData = serverData;
    }

    public int getSendingServer() {
        return sendingServer;
    }

    public String getMessage() {
        return message;
    }

    public ServerData getServerdata() {
        return serverData;
    }

    public void setSendingServer(int sendingServer) {
        this.sendingServer = sendingServer;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setServerdata(ServerData serverdata) {
        this.serverData = serverData;
    }
}
