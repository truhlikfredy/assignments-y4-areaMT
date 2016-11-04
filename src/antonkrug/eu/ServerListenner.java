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
public class ServerListenner {

  // if set true will output on console more verbose information
  private static final boolean           DEBUG         = false;

  // this way I can track each socket with their associated handler (thread)
  private HashMap<Socket, ServerHandler> clientSockets = null;
  private JTextArea                      textArea      = null;
  private boolean                        keepRunning   = true;
  private DatabaseHandler                db            = null;

  
  /**
   * Setting empty list of clients and access to config file.
   */
  public ServerListenner(JTextArea textArea) {
    this.clientSockets = new HashMap<>();
    this.textArea      = textArea;
    this.db            = new DatabaseHandler();
    
    final Pair<Boolean, String> ret = db.connect();
    if (ret.getFirst()==false) {
      guiMessage(ret.getSecond());
      keepRunning = false;
    }
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
    
    guiMessage(Messages.getString("CLIENT_CONNECTED") + handler.getHostAddress() );
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
  
  
  public synchronized Pair<Boolean, String> logIn(int account) {
    final String ret = db.getAccountName(account);
    if (ret == null) {
      return new Pair<Boolean, String>(false, Messages.getString("ERROR_NOT_ALLOWED"));
    }
    else {
      return new Pair<Boolean, String>(true, ret);
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
  
  
  /**
   * Only server listener can update this logger
   * @param msg
   */
  private void guiMessage(String msg) {
    textArea.append("Server---" + msg + "\n");
  }
  
  /**
   * Search external strings and print out the value to the gui directly
   * 
   * @param key
   */
  private void guiMessageFromExternal(String key) {
    guiMessage(Messages.getString(key));
  }
  
  
  /**
   * Threads workers can access the GUI logger
   * 
   * @param handler
   * @param msg
   */
  protected synchronized void messageFromThread(ServerHandler handler, String msg) {
    textArea.append(handler.toString() + "---" + msg + "\n");
  }


  public int listen(int portToListen) {
    ServerSocket server;
    Socket       client;

    try {
      server = new ServerSocket(portToListen);
    } catch (IOException e) {
      guiMessageFromExternal("SERVER_START_ERROR");
      if (DEBUG) e.printStackTrace();

      return 1;
    }

    guiMessage(Messages.getString("SERVER_STARTED") + new Date());
    
    while (keepRunning) {
      
      guiMessage(Messages.getString("SERVER_WAITING") + portToListen);
      
      try {
        client = server.accept();
        guiMessageFromExternal("CLIENT_CONNECTING");

        // Before getting Denial-Of-Service due the server overload and dropping
        // all connections, let's throttle down number of connections possible.
        if (clientsConnected() < Config.getInstance().getInteger("socket_max_clients")) {

          ServerHandler handler = new ServerHandler(this, client);
          clientConnected(client, handler);

          // sendIdentity();

          handler.start();

        }
        else {          
          //reached maximum of clients
          guiMessageFromExternal("SERVER_CAP");
          client.close();
        }

      } catch (Exception e) {
        guiMessageFromExternal("ERROR_ACCEPT");
        if (DEBUG) e.printStackTrace();
      }

    }
    
    guiMessageFromExternal("SERVER_CLOSING");
    
    try {
      server.close();
    } catch (IOException e) {
      guiMessageFromExternal("SERVER_STOP_ERROR");
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
