import java.net.*;
import java.io.*;

public class ListenThread extends Thread{
  protected ServerSocket serverSocket;
  protected Socket socket;

  public ListenThread(ServerSocket serverSocket){
    this.serverSocket = serverSocket;
  }

  public void run(){
    boolean run = true;
    while(run){
      try{
          this.socket = serverSocket.accept();
          new ClientHandler(this.socket).start();
      } catch(SocketException e){
        run = false;
        System.out.println("Server closed.");
      } catch (IOException e){
        System.err.println("Failed connecting to client");
      }
    }
  }
}
