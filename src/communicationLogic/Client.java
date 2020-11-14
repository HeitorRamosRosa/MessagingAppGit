package communicationLogic;

import businessLogic.ServerData;
import businessLogic.ClientData;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private String serverIp;
    private int serverPort;
    private static int clientN = 0;
    private int clientNumber;
    private int threadNumber;
    boolean shutdown;
    ClientData clientData;
    ServerData serverData;
    Socket socket;
    ObjectOutputStream oOSTCP;
    ObjectInputStream oISTCP;

    public Client(String serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        threadNumber = 0;
    }

    public String getServerIp() {
        return serverIp;
    }

    public int getServerPort() {
        return serverPort;
    }

    public static int getClientN() {
        return clientN;
    }

    public int getClientNumber() {
        return clientNumber;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public static void setClientN(int clientN) {
        Client.clientN = clientN;
    }

    public void setClientNumber(int clientNumber) {
        this.clientNumber = clientNumber;
    }

    public int handleFirstConnection(){
        HandleFirstConnection hFC = new HandleFirstConnection();
        return 0;
    }

    class HandleFirstConnection extends Thread {
        @Override
        public void run() {

            try {
                System.out.println("Thread " + threadNumber + " is running.");
                threadNumber++;

                DatagramSocket dS = new DatagramSocket();
                ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
                ObjectOutputStream oOS = new ObjectOutputStream(bAOS);
                UDPCommunication c = new UDPCommunication("firstConnect",false, new ClientData("Maria","Amelia","Dinis","localhost",9008));
                c.setRequest("firstConnect");
                c.setAccepted(false);
                oOS.writeObject(c);

                byte[] bufDPOut = bAOS.toByteArray();

                InetAddress ip = InetAddress.getByName(serverIp);
                DatagramPacket dP = new DatagramPacket(bufDPOut, bufDPOut.length, ip, serverPort);
                dS.send(dP);
                System.out.println("UDPCommunication sent with resquest: " + c.getRequest());
                dP = new DatagramPacket(new byte[512], 512);
                dS.receive(dP);

                byte[] bufDP = dP.getData();
                ByteArrayInputStream bAIS = new ByteArrayInputStream(bufDP);
                ObjectInputStream oIS = new ObjectInputStream(bAIS);
                c = (UDPCommunication) oIS.readObject();

                if (c.isAccepted() == true) {
                    System.out.println("You've been accepted.");
                    clientData = c.getClient();
                    EstablishTCPConnection eTC = new EstablishTCPConnection();
                    eTC.run();
                } else {
                    System.out.println("You've been denied.");
                }

                dS.close();
            } catch (IOException | ClassNotFoundException e) {

                System.out.println("IO or Class not found exception.");
                e.printStackTrace();

            }
        }
    }


    class EstablishTCPConnection extends Thread {
        @Override
        public void run() {

            try {

                socket = new Socket(serverIp,serverPort);
                oOSTCP = new ObjectOutputStream(socket.getOutputStream());
                oISTCP = new ObjectInputStream(socket.getInputStream());
                TCPCommunication c = new TCPCommunication("firstTCPConnection",false, clientData,new ServerData());
                oOSTCP.writeObject(c); /*writeUnshared por causa da cache e a referência ser a mesma ou fazer oOS.reset()*/
                oOSTCP.flush();

                c= (TCPCommunication)oISTCP.readObject();

                if(c.isAccepted() == true){
                    System.out.println("TCP connection established.");
                    serverTCPConnection sTC = new serverTCPConnection();
                    sTC.start();
                }else{
                    System.out.println("TCP connection failed.");
                }


            } catch (IOException | ClassNotFoundException e) {
                System.out.println("IO Exception");
                e.printStackTrace();
            }

        }
    }

    public int establishTCPConnection(){
        EstablishTCPConnection eTC = new EstablishTCPConnection();
        eTC.run();
        return 0;
    }


    class serverTCPConnection extends Thread{
        @Override
        public void run() {
            System.out.println("TCP connection with server is being held by an independent thread.");

            try {

                do {

                    TCPCommunication c = (TCPCommunication)oISTCP.readObject();

                    if(c.getRequest().equals("ResponseToRequest1")){
                        System.out.println("Server responded to request1");
                    }else{
                        if(c.getRequest().equals("ResponseToRequest2")){
                            System.out.println("Server responded to request2");
                        }
                    }
                }while(shutdown == false);

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("IOException | ClassNotFoundException at serverTCPConnection");
                e.printStackTrace();
            }
        }
    }

    class SendRequest extends Thread{
        private String request;

        public SendRequest(String request) {
            this.request = request;
        }

        @Override
        public void run() {

            try {

                TCPCommunication c = new TCPCommunication(request,false, clientData,new ServerData());
                oOSTCP.writeObject(c); /*writeUnshared por causa da cache e a referência ser a mesma ou fazer oOS.reset()*/
                oOSTCP.flush();
                System.out.println("Wrote request.");

            } catch (IOException e) {
                System.out.println("IOException while sending request in SendRequest thread.");
                e.printStackTrace();
            }

        }

    }

    public void run() {
        System.out.println("Client " + clientNumber + " is running.");
        HandleFirstConnection hFC = new HandleFirstConnection();
        hFC.start();
        Scanner sc = new Scanner(System.in);
        String op = "buHU";
        do{
            op = sc.nextLine();
            System.out.print(clientData.getClientN() + " - insert command:");
            if(op.equals("request1")){
                System.out.println("Wrote request.");
                SendRequest sR = new SendRequest("requestOne");
                sR.start();

            }else{
                if(op.equals("request2")){
                    System.out.println("Wrote request.");
                    SendRequest sR = new SendRequest("requestTwo");
                    sR.start();

                }
            }
        }while(!op.equals("exit"));
        shutdown = true;
    }

    public static void main(String[] args) {
        Client c = new Client("localhost", 9008);
        c.run();
    }
}
