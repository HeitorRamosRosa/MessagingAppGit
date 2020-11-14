package businessLogic;

import java.io.Serializable;
import java.net.InetAddress;

public class ClientData implements Serializable {
    private String name;
    private String userName;
    private String password;
    private String serverIp;
    private int serverPort;
    private InetAddress clientIp;
    private int clientPort;
    private int clientN;

    public ClientData() {
    }

    public ClientData(String name, String userName, String password, String serverIp, int serverPort) {
        this.name = name;
        this.userName = userName;
        this.password = password;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        clientN = -1;
    }

    public String getName() {
        return name;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getServerIp() {
        return serverIp;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public InetAddress getClientIp() {
        return clientIp;
    }

    public int getClientPort() {
        return clientPort;
    }

    public void setClientIp(InetAddress clientIp) {
        this.clientIp = clientIp;
    }

    public void setClientPort(int clientPort) {
        this.clientPort = clientPort;
    }

    public int getClientN() {
        return clientN;
    }

    public void setClientN(int clientN) {
        this.clientN = clientN;
    }
}
