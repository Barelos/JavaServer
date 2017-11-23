import java.net.*;
import java.io.*;
/**
A thread that listens to clients requests
@author Barel Levy
*/
public class ListenThread extends Thread{
  protected ServerSocket serverSocket;
  protected Socket socket;

  /**
  Constructor
  @param serverSocket the socket that the server listen to
  */
  public ListenThread(ServerSocket serverSocket){
    this.serverSocket = serverSocket;
  }

  /**
  Main method of the thread
  */
  public void run(){
    boolean run = true;
    while(run){
      try{
          this.socket = serverSocket.accept();
          new ClientHandler(this.socket).start();
      } catch(SocketException e){
        /* this exception will get thrown when the server is waiting on
           accept but closed from the main thread */
        run = false;
        System.out.println("Server closed.");
      } catch (IOException e){
        System.err.println("Failed connecting to client");
      }
    }
  }
}
