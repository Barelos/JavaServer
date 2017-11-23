import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Server{

  // main method
  public static void main(String[] args) throws IOException{
    // test for correct Usage
    if (args.length != 1){
      System.err.println("USAGE: java Server <port number>");
      System.exit(1);
    }
    // parse user input into port num and try to open a Socket
    int portNumber = Integer.parseInt(args[0]);

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
        }
      }
    } catch (Throwable e){
      System.err.println("Failed on port " + portNumber);
    }
  }
}
