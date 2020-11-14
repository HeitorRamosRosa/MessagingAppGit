package userInterface;

import communicationLogic.Client;
import communicationLogic.Server;

import java.util.Scanner;

public class ClientUI {
    private Boolean over = false;
    private Client client;

    public ClientUI(Client client) {
        this.client = client;
    }

    public void run(){

        Scanner sc = new Scanner(System.in);
        String op;

        do{

            switch(client.handleFirstConnection()){

                case 0:

                    System.out.println("UDP connection was sucessfull.");
                    System.out.println("We'll to establish TCP connection.");

                        if(client.establishTCPConnection() == 0){
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

        System.out.println("Client shutting down.");
    }

    public static void main(String[] args){
        Client c = new Client("localhost", 9008);
        ClientUI cUI= new ClientUI(c);
        c.run();
        cUI.run();
    }
}
