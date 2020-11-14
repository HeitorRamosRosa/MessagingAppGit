package userInterface;

import communicationLogic.Server;

import java.util.Scanner;

public class ServerUI {
    private boolean over = false;
    private Server server;

    public ServerUI(Server server) {
        this.server = server;
    }

    public void run(){

        Scanner sc = new Scanner(System.in);
        String op;

        do{

            switch(server.handleFirstConnection()){

                case 0:

                    System.out.println("UDP connection was sucessfull.");
                    System.out.println("We'll to establish TCP connection.");

                    if(server.establishTCPConnection() == 0){
                        System.out.println("TCP Connection established sucessfully.");
                    }else{
                        System.out.println("TCP Connection failed.");
                    }

                    break;

                default:
                    over = true;
                    System.out.println("UDP connection failed.");
                    break;
            }

        }while(over != true);

        System.out.println("Server shutting down.");



    }

    public static void main(String[] args){
        Server s = new Server(9008);
        ServerUI sUI= new ServerUI(s);
        s.run();
        sUI.run();
    }
}
