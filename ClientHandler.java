import java.net.*;
import java.io.*;

public class ClientHandler extends Thread{
  protected Socket socket;

  public ClientHandler(Socket socket){
    this.socket = socket;
  }

  public void run(){
    try{
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out.println("Hello?");
      String inputLine = in.readLine();
      System.out.println(inputLine);
      this.socket.close();
    } catch (Throwable e){
      System.err.println("Error on handler");
    }
  }
}
