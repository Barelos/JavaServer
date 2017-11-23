import java.util.*;
import java.io.*;
/**
A database of current users and passwords
Built as a singelton with a count of current active users
@author Barel Levy
*/
public class DataBase{
  private static DataBase instance;
  private final String delimiter = "\t";
  private Hashtable<String, String> dataBase;
  private static String file = "dataBase.txt";
  private int numUsers;
  private static int numHolders;

  /**
  Private constructor
  */
  private DataBase(){
    this.dataBase = new Hashtable<String, String>();
    readFile();
  }

  /**
  Make a hash table from a text file
  */
  private void readFile(){
    // open the file for reading
    try (BufferedReader lines = new BufferedReader(new FileReader(this.file))){
      String line;
      String[] parts;
      // read first line with the number of users
      parts =  lines.readLine().replace("\n", "").split("\t");
      this.numUsers = Integer.parseInt(parts[1]);
      // read line by line and add to database
      while((line = lines.readLine()) != null){
        parts = line.replace("\n", "").split(this.delimiter);
        this.dataBase.put(parts[0], parts[1]);
      }
    }catch (Throwable e){
      System.err.println("Couldn't open database file.");
      e.printStackTrace();
    }
  }

  /**
  Test if user is in the database
  @param name the username
  @return whether the user is in the database
  */
  public synchronized boolean userIn(String username){
    return dataBase.containsKey(username);
  }

  /**
  get password of user
  @param name the username
  @return the password
  */
  public synchronized String getPassword(String name){
    return dataBase.get(name);
  }

  /**
  Add or change the password of a user
  @param name the username
  @param pass user password
  */
  public synchronized void setPassword(String name, String pass){
    this.numUsers = this.userIn(name) ? this.numUsers : this.numUsers + 1;
    this.dataBase.put(name, pass);
  }

  /**
  Override to string method
  If Changed should also change readFile method
  */
  public String toString(){
    Enumeration<String> data = this.dataBase.keys();
    // add number of users
    String rep = "Elements:\t" + this.numUsers + "\n";
    String element;
    // print all useres and passwords
    while(data.hasMoreElements()){
      element = data.nextElement();
      rep += element + this.delimiter + this.dataBase.get(element) + "\n";
    }
    return rep;
  }

  /**
  When last user log out and server close save the database
  */
  public static void saveData(){
    try{
      File newFile = new File(file);
      FileWriter fileWriter = new FileWriter(newFile, false);
      fileWriter.write(instance.toString());
      fileWriter.close();
    } catch (IOException e){
      System.err.println("Failed to save File!");
    }
  }

  /**
  Decrement count of database holders
  If last one leave his copy(usually the Server) save and close
  */
  public static synchronized void letGo(){
    numHolders--;
    if (numHolders == 0){
      saveData();
    }
  }

  /**
  The singelton get instance function
  */
  public static synchronized DataBase getInstance(){
    if (instance == null){
      instance = new DataBase();
      numHolders = 0;
    }
    numHolders++;
    return instance;
  }
}
