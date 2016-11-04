package antonkrug.eu;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
  private              DataInputStream  consumer       = null; 
  private              DataOutputStream producer       = null;


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
        double input  = consumer.readDouble();
        double result = calculatePi(input);
            
        server.messageFromThread(this, Messages.getString("HANDLER_RADIUS") + " " + input
            + " " + Messages.getString("HANDLER_RESULT") + result);
        
        producer.writeDouble(result);
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
      final Integer               account = consumer.readInt();
      final Pair<Boolean, String> result  = server.logIn(account);
      
      if (result.getFirst()) {
        producer.writeUTF("Welcome "+result.getSecond());
        producer.flush();
      }
           
      return true;        
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
      consumer =  new DataInputStream(client.getInputStream());
      producer =  new DataOutputStream(client.getOutputStream());
      
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
