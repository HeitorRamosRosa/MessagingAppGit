package communicationLogic;

import businessLogic.ClientData;
import businessLogic.ServerData;

import java.io.Serializable;

public class TCPCommunication implements Serializable {
    private String request;
    private boolean accepted;
    private ClientData clientData;
    private ServerData serverData;

    public TCPCommunication(String request, boolean accepted, ClientData clientData, ServerData serverData) {
        this.request = request;
        this.accepted = accepted;
        this.clientData = clientData;
        this.serverData = serverData;
    }

    public String getRequest() {
        return request;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public ClientData getClientData() {
        return clientData;
    }

    public ServerData getServerData() {
        return serverData;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public void setClientData(ClientData clientData) {
        this.clientData = clientData;
    }

    public void setServerData(ServerData serverData) {
        this.serverData = serverData;
    }
}
