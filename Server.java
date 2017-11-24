import java.net.*;
import java.io.*;
import java.util.Scanner;
/**
This is the main class of the server and functions as main thread
@author Barel Levy
*/
public class Server{

  /**
  The main method of the server
  */
  public static void main(String[] args) throws IOException{
    // test for correct Usage
    if (args.length != 1){
      System.err.println("USAGE: java Server <port number>");
      System.exit(1);
    }
    // parse user input into port num and try to open a Socket
    int portNumber = Integer.parseInt(args[0]);
    DataBase db = DataBase.getInstance();
    try{
      ServerSocket serverSocket = new ServerSocket(portNumber);
      ListenThread listen = new ListenThread(serverSocket);
      listen.start();
      String userInput = "";
      Scanner sc = new Scanner(System.in);
      while(true){
        userInput = sc.nextLine();
        if (userInput.equals("EXIT")){
          serverSocket.close();
          break;
        } else if (userInput.equals("PRINT")){
          System.out.println(db);
        }
      }
    } catch (Throwable e){
      System.err.println("Failed on port " + portNumber);
    }
    DataBase.letGo();
  }
}
