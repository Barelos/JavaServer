import java.net.*;
import java.io.*;
/**
A class to handle the comunication between server and clients
Redundent when comunication is simple but essential in the future
*/
public class Protocol {
  public static final int OK = 1;
  public static final int NAME = 2;
  public static final int PASS = 3;

  public static final int LOG = 1;
  public static final int SIGN = 2;
  public static final int CHANGE = 3;

  private static final String delimiter = "-";

  /**
  Parse username, password and request into server request format
  @param username the username
  @param password the password
  @param r a request type i.e LOG,SIGN or CHANGE
  @return a string in server request form
  */
  public static  String makeClientRequest(String username, String password, int r){
    String reply = Integer.toString(r) + delimiter;
    reply += username + delimiter + password;
    return reply;
  }

  /**
  Parse from request form into array of data
  @param request the client request
  @return an array of strings
  */
  public static String[] parseUserRequest(String request){
    String[] parts = request.split(delimiter);
    return parts;
  }
}
