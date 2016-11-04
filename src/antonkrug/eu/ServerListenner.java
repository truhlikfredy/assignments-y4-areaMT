package antonkrug.eu;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JTextArea;

/**
 * Main listener thread, this will instanciate multiple threads for each
 * connection (socket)
 * 
 * @author Anton Krug
 * @date 01.11.2016
 * @version 1
 */
class ServerListenner {

  // if set true will output on console more verbose information
  private static final boolean           DEBUG         = false;

  // this way I can track each socket with their associated handler (thread)
  private HashMap<Socket, ServerHandler> clientSockets = null;
  private JTextArea                      textArea      = null;
  private boolean                        keepRunning   = true;

  
  /**
   * Setting empty list of clients and access to config file.
   */
  public ServerListenner(JTextArea textArea) {
    this.clientSockets = new HashMap<>();
    this.textArea      = textArea;
  }


  /**
   * Adds new client to the list (thread-safe). At the recent moment they don't
   * need to be thread safe, but it's future proofing in case these methods need
   * to be accessed from multiple threads.
   * 
   * @param client
   */
  public synchronized void clientConnected(Socket client, ServerHandler handler) {
    clientSockets.put(client, handler);
  }


  /**
   * If client is in the list, it will get removed (thread-safe) At the recent
   * moment this is only one used from the multiple threads.
   * 
   * @param client
   */
  public synchronized void clientDisconnected(Socket client) {
    if (clientSockets.containsKey(client)) {
      clientSockets.remove(client);
    }
  }
  
  
  /**
   * Will disconnect client from specific handler
   * @param handler
   */
  private void clientDrop(ServerHandler handler) {
    handler.drop();
  }


  /**
   * Will get number of connected clients (thread-safe) At the recent moment
   * they don't need to be thread safe, but it's future proofing in case these
   * methods need to be accessed from multiple threads.
   * 
   * @param client
   */
  public synchronized int clientsConnected() {
    return clientSockets.size();
  }
  
  
  public void disconnectAll() {
    //using tricks from http://www.java8.org/ to save lot of syntatical sugar
    
    clientSockets.values().stream().forEach(this::clientDrop);
    // this::clientDrop is same as following lamda expression:
    // (handlerThread) -> clientDrop(handlerThread)
    
    //server will finish the while loop
    keepRunning = false;  
  }
  
  
  private void guiMessage(String msg) {
    textArea.append(msg);
  }
  
  
  protected synchronized void messageFromThread(ServerHandler handler, String msg) {
    textArea.append(msg);
  }


  public int listen(int portToListen) {
    ServerSocket server;
    Socket       client;

    try {
      server = new ServerSocket(portToListen);
    } catch (IOException e) {
      guiMessage(Messages.getString("SERVER_START_ERROR"));
      if (DEBUG) e.printStackTrace();

      return 1;
    }

    while (keepRunning) {
      guiMessage("Started @ " + new Date() + " and waiting for a clients on port "
          + portToListen);
      
      try {
        client = server.accept();

        // Before getting Denial-Of-Service due the server overload and dropping
        // all connections, let's throttle down number of connections possible.
        if (clientsConnected() < Config.getInstance().getInteger("socket_max_clients")) {

          ServerHandler handler = new ServerHandler(this, client);
          clientConnected(client, handler);

          // sendIdentity();

          handler.start();

        }
        else {
          System.out.println("Server connection capacity reached.");
          client.close();
          continue;
        }

      } catch (Exception e) {
        System.out.println(Messages.getString("ERROR_ACCEPT"));
        if (DEBUG) e.printStackTrace();
      }

    }
    
    try {
      server.close();
    } catch (IOException e) {
      guiMessage(Messages.getString("SERVER_STOP_ERROR"));
      if (DEBUG) e.printStackTrace();
    }
    
    return 0;
  }
  /*
   * sock = (Socket) client_socks.get(0); try { out = new PrintWriter(
   * sock.getOutputStream(), true ); out.println("@WHITE"); out.flush(); }
   * 
   * catch(Exception e) { System.out.println(e); }
   */
}
