package businessLogic;

import java.io.Serializable;

public class ServerData implements Serializable {
    private String serverIp;
    private int serverPort;
    private int nClients;
    private int serverNumber;

    public ServerData() {
    }

    public ServerData(String serverIp, int serverPort, int nClients, int serverNumber) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.nClients = nClients;
        this.serverNumber = serverNumber;
    }

    public String getServerIp() {
        return serverIp;
    }

    public int getServerPort() {
        return serverPort;
    }

    public int getnClients() {
        return nClients;
    }

    public int getServerNumber() {
        return serverNumber;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void setnClients(int nClients) {
        this.nClients = nClients;
    }

    public void setServerNumber(int serverNumber) {
        this.serverNumber = serverNumber;
    }
}
