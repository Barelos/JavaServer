import java.net.*;
import java.io.*;
/**
A thread that listens to clients requests
@author Barel Levy
*/
public class ListenThread extends Thread{
  private final int MAX_THREADS = 10;
  protected ServerSocket serverSocket;
  protected Socket socket;
  private static int activeThreads = 0;

  public static int getActive(){
    return activeThreads;
  }

  public static synchronized int upActive(){
    activeThreads++;
  }

  public static synchronized int downActive(){
    activeThreads--;
  }

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
      if (ListenThread.getActive() < MAX_THREADS){ // cap number of threads
        try{
            this.socket = serverSocket.accept();
            ListenThread.upActive();
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
}
