import java.net.*;
import java.io.*;
import java.util.Random;
import java.security.MessageDigest;
/**
A thread that handles a single client.
@author Barel Levy
*/
public class ClientHandler extends Thread{
  protected Socket socket; // the client's socket
  private DataBase db; // instance of DataBase which is a singelton
  // for the salt
  private final int SALT_LEN = 20; // len of salt
  private final int LOWER = 46;
  private final int UPPER = 126;

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
    String[] parts = onServer.split("-");
    String hased = hash(pass + parts[1]); // hash with salt
    int reply = hased.equals(parts[0]) ? Protocol.OK : Protocol.PASS;
    // print and reply accordingly
    if (reply == Protocol.OK){
      System.out.println(name + " logged in");
    }else{
      System.out.println(name + " tried to login with wrong password");
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
      System.out.println("Name " + name + " is already in");
      return Protocol.NAME;
    }
    String salt = createSalt();
    pass = hash(pass + salt) + "-" + salt;
    this.db.setPassword(name, pass);
    System.out.println("Sighned " + name);
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
    String salt = createSalt();
    pass = hash(pass + salt) + "-" + salt;
    this.db.setPassword(name, pass);
    System.out.println(name + " changed password");
    return Protocol.OK;
  }

  /**
  Make a random salt for hashing
  @return the salt in string form
  */
  private String createSalt(){
    Random rand = new Random(System.nanoTime());
    StringBuilder salt = new StringBuilder();
    int val;
    while (salt.length() < SALT_LEN){
      val = this.LOWER + (int) (rand.nextFloat() * (UPPER - LOWER + 1));
      salt.append((char) val);
    }
    String saltStr = salt.toString();
    return saltStr;
  }

  /**
  Hash a string using sha256
  @param base the string we would like to hash
  @return the string hased
  */
  private String hash(String base) {
    try{
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(base.getBytes("UTF-8"));
      StringBuffer hexString = new StringBuffer();

      for (int i = 0; i < hash.length; i++) {
        String hex = Integer.toHexString(0xff & hash[i]);
        if(hex.length() == 1) hexString.append('0');
        hexString.append(hex);
      }

      return hexString.toString();
    } catch(Exception ex){
       throw new RuntimeException(ex);
    }
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
      ListenThread.downActive(); // allow one more thread
    } catch(SocketException e){
      System.err.println("Done");
    } catch (Throwable e){
      try{
      this.socket.close();
      System.err.println("Error on handler");
    } catch (IOException ine){
        System.err.println("Wow this is F'd up");
      }
    }
  }
}
