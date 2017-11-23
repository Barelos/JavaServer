import java.io.*;
import java.net.*;

public class Client{
  public static void main(String[] args){
    // test usage
    if (args.length != 2){
      System.err.println("USAGE: java NewClient <ip> <port>");
      System.exit(1);
    }
    String hostName = args[0];
    int portNumber = Integer.parseInt(args[1]);

    try{
      Socket serverSocket = new Socket(hostName, portNumber);
      PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
      BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
      BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
      System.out.println("Server: " + in.readLine());
      out.println(stdIn.readLine());
      serverSocket.close();
    }catch (IOException e){
      System.out.println("Failed connectin to server");
    }
  }
}
