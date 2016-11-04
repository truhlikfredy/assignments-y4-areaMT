package antonkrug.eu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Thread calculating area for each connection
 * 
 * 
 * @author Anton Krug
 * @date 01.11.2016
 * @version 1
 */
public class ServerHandler extends Thread {

  //multiply by 1000, round to whole number and then divide 1000 to get 3 decimal places.
  //Keep as floating number so it will not get truncated. Set 0 to disable rounding
  private static final double           DECIMAL_PLACES = 1000;
  private static final boolean          DEBUG          = false;
  private              Socket           client         = null;
  private              ServerListenner  server         = null;
  private              boolean          keepConnected  = true;
  private              BufferedReader   consumer       = null; 
  private              PrintWriter      producer       = null;


  public ServerHandler(ServerListenner server, Socket client) {
    this.client = client;
    this.server = server;    
  }

  
  public String getHostAddress() {
    return client.getInetAddress().getHostAddress();
  }
  
  
  public void drop() {
    try {
      client.close();
    } 
    catch (IOException e) {  
      server.messageFromThread(this, Messages.getString("ERROR_CLOSE") );
      if (DEBUG) e.printStackTrace();
    }
  }
  
  public double calculatePi(double radius) {
    // Use absolute value on radius, negative radius is just into opposite
    // direction (could more presentation feature), but the length of the radius
    // is the same. Negative value could be used to display the line on opposite
    // size, but still it would be the same radius as positive value.
    
    double result = Math.PI * Math.pow( Math.abs(radius), 2);
    
    //round to defined decimal places
    if (DECIMAL_PLACES > 0) {
      result = Math.round(result * DECIMAL_PLACES) / DECIMAL_PLACES;
    }
    
    return result; 
  }
  
  
  private void calculatePiRequests() {
   
    while (keepConnected) {
      try {
        double input  = Double.parseDouble(consumer.readLine());
        double result = calculatePi(input);
            
        server.messageFromThread(this,"Request from IP: " + client.getInetAddress().getHostAddress());
        server.messageFromThread(this, Messages.getString("HANDLER_RADIUS") + " " + input
            + " " + Messages.getString("HANDLER_RESULT") + result);
        
        producer.println(result);
      }
      catch (IOException e) {
        server.messageFromThread(this, Messages.getString("ERROR_REQUEST") );
        if (DEBUG) e.printStackTrace();
        keepConnected = false;
      }
    }      
  }
  
  
  private boolean validatedUser() {
    try {
      final Integer               account = Integer.parseInt(consumer.readLine());      
      final Pair<Boolean, String> result  = server.logIn(account);
      server.messageFromThread(this, "Authetication with account number:" + account);
      
      if (result.getFirst()) {
        server.messageFromThread(this, "Authetication correct:" + result.getSecond());        
        producer.println("Welcome "+result.getSecond());
        return true;        
      }
      else {
        server.messageFromThread(this, "Authetication failed!");        
        producer.println("Failed to authenticate");
        return false;
      }
           
    }
    catch (IOException e) {
      server.messageFromThread(this, Messages.getString("ERROR_VALIDATED") );
      if (DEBUG) e.printStackTrace();
      keepConnected = false;
      return false;
    } 
  }
  

  public void run() {
    try {
      consumer = new BufferedReader(new InputStreamReader(client.getInputStream()));
      producer = new PrintWriter(client.getOutputStream(), true);
      
      if (validatedUser()) {
        calculatePiRequests();
      }
      
      client.close();
    }
    catch (Exception e) {
      server.messageFromThread(this, Messages.getString("CLIENT_MSG_ERROR") );
      if (DEBUG) e.printStackTrace();
    }
    
    server.clientDisconnected(client);
    server.messageFromThread(this, Messages.getString("CLIENT_DISCONNECTING") + getHostAddress() );
    
  }


}
