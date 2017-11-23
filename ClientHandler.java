import java.net.*;
import java.io.*;
/**
A thread that handles a single client.
@author Barel Levy
*/
public class ClientHandler extends Thread{
  protected Socket socket; // the client's socket
  private DataBase db; // instance of DataBase which is a singelton

  /**
  A constructor for the ClientHandler class
  @param socket an already open socket with the client
  */
  public ClientHandler(Socket socket){
    this.socket = socket;
    this.db = DataBase.getInstance();
  }

  /**
  Log a user to the server
  @param name the username
  @param pass the user password
  @return whether the login was successful or not
  */
  private int logUser(String name, String pass){
    // test if user is in the data base
    if (!this.db.userIn(name)){
      System.out.println("No user named " + name);
      return Protocol.PASS;
    }
    // test if the password is correct
    String onServer = this.db.getPassword(name);
    int reply = pass.equals(onServer) ? Protocol.OK : Protocol.PASS;
    // print and reply accordingly
    if (reply == Protocol.OK){
      System.out.println(name + " logged in with password " + pass);
    }else{
      System.out.println(name + " tried to login with wrong  password " + pass);
    }
    return reply;
  }

  /**
  Sign a user to the database
  @param name the username
  @param pass the user password
  @return whether the signin was successful or not
  */
  private int signUser(String name, String pass){
    if (this.db.userIn(name)){
      System.out.println("Name " + name + " in already in");
      return Protocol.NAME;
    }
    this.db.setPassword(name, pass);
    System.out.println("Sighned " + name + " with password " + pass);
    return Protocol.OK;
  }

  /**
  Change user password in the database
  @param name the username
  @param pass the user password
  @return whether the change was successful or not
  */
  private int changePassword(String name, String pass){
    if (!this.db.userIn(name)){
      System.out.println("No user named " + name);
      return Protocol.NAME;
    }
    this.db.setPassword(name, pass);
    System.out.println(name + " changed password to " + pass);
    return Protocol.OK;
  }

  /**
  Main method for the thread
  */
  public void run(){
    // try to open the comunication with the client
    try{
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      // read request
      int reply = Protocol.OK;
      String[] parts = Protocol.parseUserRequest(in.readLine());
      // act according to user request
      switch (Integer.parseInt(parts[0])){
        case Protocol.LOG:{
                               reply = logUser(parts[1], parts[2]);
                               break;}
        case Protocol.SIGN: {
                                reply = signUser(parts[1], parts[2]);
                                break;}
        case Protocol.CHANGE:{
                                  reply = changePassword(parts[1], parts[2]);
                                  break;}
      }
      out.println(reply);
      DataBase.letGo(); // let database know we do not need it
      // close socket when done
      this.socket.close();
    } catch(SocketException e){
      System.err.println("Done");
    } catch (Throwable e){
      System.err.println("Error on handler");
    }
  }
}
