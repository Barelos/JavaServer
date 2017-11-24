import java.io.*;
import java.net.*;
/**
A simple test client to test the server
@author Barel Levy
*/
public class Client{
  /**
  Main method
  */
  public static void main(String[] args){
    // test usage
    if (args.length != 2){
      System.err.println("USAGE: java NewClient <ip> <port>");
      System.exit(1);
    }
    // parse user input
    String hostName = args[0];
    int portNumber = Integer.parseInt(args[1]);
    // try establishing connection with server
    try{
      Socket serverSocket = new Socket(hostName, portNumber);
      PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
      BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
      BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
      // get server request from user
      out.println(stdIn.readLine());
      // read reply and print it
      String reply = in.readLine();
      System.out.println(reply);
      serverSocket.close();
    // if could not connect to server print error to user
    }catch(SocketException e){
      System.err.println("Somthing went wrong with server");
    }catch (IOException e){
      System.err.println("Failed connectin to server");
    }
  }
}
