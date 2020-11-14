package communicationLogic;

import businessLogic.*;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.Vector;

public class Server {
    private Vector <String> possibleRequests;
    private Vector <ClientData> clientDataArray;
    private Vector <ServerTCPData> connetionsArray;
    private int serverPort;
    private int serverNumber;
    boolean shutdown = false;
    private DatagramSocket dS;
    private ServerSocket sS;
    private static int nTCPConnection = 0;
    private ServerData serverData;

    public Server( int serverPort) {
        this.serverPort = serverPort;
        possibleRequests = new Vector<>();
        clientDataArray = new Vector<>();
        connetionsArray =  new Vector<>();
        serverData = new ServerData();
        serverData.setServerPort(serverPort);
        serverData.setnClients(0);
        serverData.setServerNumber(serverNumber);
    }


    class HandleFirstConnection extends Thread{
        @Override
        public void run(){
            System.out.println("UDP connection manager is up and running.");
            try {

                boolean threadEnd = false;
                DatagramPacket dP;

                while(threadEnd != true){

                    dP = new DatagramPacket(new byte[512],512);
                    dS.receive(dP);

                    byte[] bufDP = dP.getData();
                    ByteArrayInputStream bAIS = new ByteArrayInputStream(bufDP);
                    ObjectInputStream oIS = new ObjectInputStream(bAIS);
                    UDPCommunication c = (UDPCommunication)oIS.readObject();
                    if(c.getRequest().equals("firstConnect")){
                        InetAddress clientIp = dP.getAddress();
                        int clientPort = dP.getPort();
                        System.out.println("Client trying to connect from: " +clientIp + " port: "+clientPort + ".");

                        ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
                        ObjectOutputStream oOS = new ObjectOutputStream(bAOS);

                        c.setAccepted(true);
                        ClientData temp = new ClientData();
                        temp.setClientIp(clientIp);
                        temp.setClientPort(clientPort);
                        temp.setClientN(nTCPConnection);
                        c.setClientData(temp);
                        oOS.writeUnshared(c);
                        byte[] bufDPout = bAOS.toByteArray();

                        dP = new DatagramPacket(bufDPout,bufDPout.length,clientIp,clientPort);
                        dS.send(dP);
                    }
                }
                dS.close();
            } catch (IOException | ClassNotFoundException e) {

                e.printStackTrace();

            }
        }
    }

    public int handleFirstConnection(){
        HandleFirstConnection hFC = new HandleFirstConnection();
        hFC.run();
        return 0;
    }

    class EstablishTCPConnection extends Thread {
        @Override
        public void run() {

            try {

                sS = new ServerSocket(serverPort);

                do{
                    System.out.println("Trying to establish TCP connection " + nTCPConnection + ".");
                    Socket s = sS.accept();
                    ObjectInputStream oIS = new ObjectInputStream(s.getInputStream());
                    ObjectOutputStream oOS = new ObjectOutputStream(s.getOutputStream());
                    TCPCommunication c = (TCPCommunication)oIS.readObject();

                    if(c.getRequest().equals("firstTCPConnection")){

                        ServerTCPData sTD = new ServerTCPData(s,nTCPConnection);
                        sTD.setoIS(oIS);
                        sTD.setoOS(oOS);
                        nTCPConnection++;
                        connetionsArray.add(sTD);
                        System.out.println("TCP connection with client was sucessfull.");
                        c.setAccepted(true);
                        clientTCPConnection cTC = new clientTCPConnection();
                        cTC.start();


                    }else{

                        System.out.println("TCP connection with client was redirected to another server.");
                        c.setAccepted(false);

                    }

                    oOS.writeUnshared(c);
                    oOS.flush();

                }while(shutdown == false);

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("A server was already runnning at this port.");
                e.printStackTrace();
            }
        }
    }

    class clientTCPConnection extends Thread{
        @Override
        public void run() {

            int n = nTCPConnection - 1;
            Socket s = connetionsArray.get(n).getSocket();
            System.out.println("TCP connection with client " + n +" is being held by an independent thread.");

            try {

                do{
                    TCPCommunication c = (TCPCommunication)connetionsArray.get(n).getoIS().readObject();
                    if(c.getRequest().equals("requestOne")){
                        System.out.println("Request one was received.");
                        SendRequest sR = new SendRequest("ResponseToRequest1",c.getClientData().getClientN());
                        sR.start();
                    }else{
                        if(c.getRequest().equals("requestTwo")){
                            System.out.println("Request two was received.");
                            SendRequest sR = new SendRequest("ResponseToRequest2",c.getClientData().getClientN());
                            sR.start();
                        }
                    }
                }while(shutdown == false);

            } catch (IOException | ClassNotFoundException e) {

                System.out.println("IO Exception within TCP connection " + n + ".");
                e.printStackTrace();

            }

        }
    }

    class SendRequest extends Thread{
        private String request;
        private int clientNumber;
        public SendRequest(String request,int n) {
            this.request = request;
            this.clientNumber = n;
        }

        @Override
        public void run() {

            try {

                TCPCommunication c = new TCPCommunication(request,false, new ClientData(),new ServerData());
                connetionsArray.get(clientNumber).getoOS().writeObject(c); /*writeUnshared por causa da cache e a referência ser a mesma ou fazer oOS.reset()*/
                connetionsArray.get(clientNumber).getoOS().flush();

            } catch (IOException e) {
                System.out.println("IOException while sending request in SendRequest thread.");
                e.printStackTrace();
            }

        }

    }

    class MulticastReceiver extends Thread{
        @Override
        public void run() {
            System.out.println("Server is listening to multicast requests.");

            try {

                InetAddress group = InetAddress.getByName("225.4.5.6");
                MulticastSocket mS = new MulticastSocket(9007);
                mS.joinGroup(group);
                byte [] buf = new byte[512];
                DatagramPacket dP = new DatagramPacket(buf,buf.length);

                do{

                    mS.receive(dP);
                    byte[] bufDP = dP.getData();
                    ByteArrayInputStream bAIS = new ByteArrayInputStream(bufDP);
                    ObjectInputStream oIS = new ObjectInputStream(bAIS);
                    MulticastCommunication mC = (MulticastCommunication)oIS.readObject();
                    if(mC.getSendingServer() != serverNumber){
                        System.out.println("Multicast request from server " + mC.getSendingServer() +": " + mC.getMessage() +".");
                    }

                }while(shutdown == false);

                mS.close();

            } catch (IOException | ClassNotFoundException e) {

                System.out.println("IO or ClassNotFound Exception within multicast receiver thread.");
                e.printStackTrace();

            }

        }
    }

    class MulticastSender extends Thread{
        private String message;

        public MulticastSender(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public void run() {


            try {

                InetAddress group = InetAddress.getByName("225.4.5.6");
                MulticastSocket mS = new MulticastSocket();
                ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
                ObjectOutputStream oOS = new ObjectOutputStream(bAOS);
                MulticastCommunication m = new MulticastCommunication(serverNumber,message,serverData);
                oOS.writeUnshared(m);
                byte [] bufOut = bAOS.toByteArray();
                DatagramPacket dP = new DatagramPacket(bufOut,bufOut.length,group,9007);
                mS.send(dP);
                System.out.println("Multicast request sent.");
                mS.close();

            } catch (IOException e) {

                System.out.println("IO Exception within multicast sender thread.");
                e.printStackTrace();

            }

        }
    }

    public int establishTCPConnection(){
        EstablishTCPConnection eTC = new EstablishTCPConnection();
        eTC.run();
        return 0;
    }

    public void run(){
        HandleFirstConnection hFC = new HandleFirstConnection();
        EstablishTCPConnection eTC = new EstablishTCPConnection();
        MulticastReceiver mR = new MulticastReceiver();
        chooseAvailablePort();
        System.out.println("Server " + serverNumber +" is running.");
        hFC.start();
        eTC.start();
        mR.start();
        connectToOtherServers();
        Scanner sc = new Scanner(System.in);
        String op = "buHU";
        do{
            System.out.print("insert command:");
            op = sc.nextLine();
            if(op.equals("request1")){
                SendRequest sR = new SendRequest("ResponseToRequest1",0);
                sR.start();

            }else{
                if(op.equals("request2")){
                    SendRequest sR = new SendRequest("ResponseToRequest2",0);
                    sR.start();
                }else{
                    if(op.equals("multicast")){
                        MulticastSender mS = new MulticastSender("Hello brother(s) servers.");
                        mS.start();
                    }
                }
            }
        }while(!op.equals("exit"));
        shutdown = true;
    }

    public void chooseAvailablePort(){
        boolean done = false;

        do{

            try {

                dS = new DatagramSocket(serverPort);
                done = true;

            } catch (SocketException e) {
                serverPort++;
                serverNumber++;
            }

        }while(done != true);


    }

    public void connectToOtherServers(){
        if(serverNumber != 0){

            MulticastSender mS = new MulticastSender("serverConnect");
            mS.start();

        }
    }

    public static void main(String[] args){
        Server s = new Server(9008);
        s.run();
    }
}
