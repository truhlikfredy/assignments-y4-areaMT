package antonkrug.eu;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
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
class ServerHandler extends Thread {

  //multiply by 1000, round to whole number and then divide 1000 to get 3 decimal places.
  //Keep as floating number so it will not get truncated. Set 0 to disable rounding
  private static final double          DECIMAL_PLACES = 1000;
  private static final boolean         DEBUG          = false;
  private              Socket          client         = null;
  private              ServerListenner server         = null;


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
  
  
  private void calculatePi(DataInputStream consume, DataOutputStream produce) {
   
    while (true) {
      try {
        double input  = consume.readDouble();
        double result = Math.PI * Math.pow(input, 2);
        
        //round to defined decimal places
        if (DECIMAL_PLACES > 0) {
          result = Math.round(result * DECIMAL_PLACES) / DECIMAL_PLACES;
        }
        
        server.messageFromThread(this, Messages.getString("HANDLER_RADIUS") + " " + input
            + " " + Messages.getString("HANDLER_RESULT") + result);
        
        produce.writeDouble(result);
      }
      catch (IOException e) {
        server.messageFromThread(this, Messages.getString("ERROR_REQUEST") );
        if (DEBUG) e.printStackTrace();
      }
    }
      
  }
  

  public void run() {
    DataInputStream  consume; 
    DataOutputStream produce;
    
    try {
      consume =  new DataInputStream(client.getInputStream());
      produce =  new DataOutputStream(client.getOutputStream());

      calculatePi(consume, produce);

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
