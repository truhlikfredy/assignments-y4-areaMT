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

  private static final boolean         DEBUG  = false;
  private              Socket          client = null;
  private              ServerListenner server = null;


  public ServerHandler(ServerListenner server, Socket client) {
    this.client = client;
    this.server = server;
    
    System.out.println("Client connection from " + getHostaddr());
  }

  
  private String getHostaddr() {
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
        server.messageFromThread(this, "Requested area for radius " + input
            + " resulting area is: " + result);      
        
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
      System.out.println(e);
    }
    
    System.out.println("Client (" + getHostaddr() + ") connection closed\n");
    server.clientDisconnected(client);
    
  }

//  // Create data input and output streams
//  DataInputStream inputFromClient = new DataInputStream(
//    socket.getInputStream());
//  DataOutputStream outputToClient = new DataOutputStream(
//    socket.getOutputStream());
//
//  while (true) {
//    // Receive radius from the client
//    double radius = inputFromClient.readDouble();
//
//    // Compute area
//    double area = radius * radius * Math.PI;
//
//    // Send area back to the client
//    outputToClient.writeDouble(area);
//
//    jta.append("Radius received from client: " + radius + '\n');
//    jta.append("Area found: " + area + '\n');
//  }
  
  


}
