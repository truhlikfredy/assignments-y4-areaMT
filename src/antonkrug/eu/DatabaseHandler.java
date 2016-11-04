package antonkrug.eu;

import java.security.interfaces.RSAKey;
import java.sql.*;
import java.util.Properties;

/**
 * JDBC access to database
 * 
 * @author  Anton Krug
 * @date    01.10.2016
 * @version 1
 */
public class DatabaseHandler {
  
  // if set true will output on console more verbose information
  private static final boolean    DEBUG = false;
  private              Connection con   = null;

  public Pair<Boolean, String> connect() {
    Properties cfg = Config.getInstance().getProperties();

    // try to connect, for each error respond accordingly or return DB_CONNECTED
    // on success
    try {
      
      // use the config.properties file to set the database access credentials
      Class.forName(cfg.getProperty("driver"));
     
      //use the config.properties file for the configuration
      con = DriverManager.getConnection(
        cfg.getProperty("provider") +           // jdbc:mysql://
        cfg.getProperty("servername") + ":" +   // localhost
        cfg.getProperty("port") + "/" +         // 3306
        cfg.getProperty("database"),            // Assignment1
        cfg);                                   // provide username and password from configuration

    } catch (ClassNotFoundException e) {
      if (DEBUG) e.printStackTrace();
      return new Pair<Boolean, String>(false, Messages.getString("ERROR_CLASS"));

    } catch (SQLException e) {
      if (DEBUG) e.printStackTrace();
      return new Pair<Boolean, String>(false, Messages.getString("ERROR_CON"));
    }

    // everything went fine, return DB_CONNECTED
    if (DEBUG) System.out.println(Messages.getString("DB_CONNECTED") + ": " + con.toString());
    return new Pair<Boolean, String>(true, Messages.getString("DB_CONNECTED"));
  }
  
  
  public boolean isAccountPresent(int account) {
    int count = 0;
    
    try {
      PreparedStatement s = con.prepareStatement ("SELECT * FROM RegisteredApplicants WHERE AccountNum= ?");
      s.setInt(1, account);
      count = s.executeQuery().getFetchSize();
    } catch (SQLException e) {
      if (DEBUG) e.printStackTrace();
      return false;
    }
    
    //if query returns 1 row then we found the account
    if (count == 1) {
      return true;
    }
    else {
      return false;
    }
  }

  
  public String getAccountName(int account) {
    ResultSet rs;
    String firstName;
    String lastName;
    
    try {
      PreparedStatement s = con.prepareStatement ("SELECT * FROM RegisteredApplicants WHERE AccountNum= ?");
      s.setInt(1, account);   
      rs =s.executeQuery();      
      rs.first();
      firstName = rs.getString("FirstName");
      lastName  = rs.getString("LastName");
    } catch (SQLException e) {
      if (DEBUG) e.printStackTrace();
      return null;
    }
    
    return firstName + " " + lastName;
  }
  
  
}
