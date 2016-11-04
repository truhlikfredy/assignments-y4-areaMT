package antonkrug.eu;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * The main method for the server, Starts the listener on a port defined in the
 * config file.
 * 
 * @author Anton Krug
 * @date 01.11.2016
 * @version 1
 */
public class MultiThreadedServerA2 extends JFrame {
  
  private static final long            serialVersionUID = 848073056894199184L;
  private              ServerListenner serverApp        = null;

  
  /**
   * Will create layout of the GUI and return the middle text area to write
   * 
   * @return
   */
  private JTextArea getTextArea() {
    JTextArea textArea = new JTextArea();
    setLayout(new BorderLayout());
    add(new JScrollPane(textArea), BorderLayout.CENTER);

    setTitle("Area Server (port:" + Config.getInstance().getInteger("socket_server_port") + ")");

    setSize(700, 300);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);

    return textArea;
  }
  
  
  /**
   * Before closing GUI let's drop all connections by hand
   */
  @Override
  public void dispose() {
    if (serverApp != null) {
      serverApp.disconnectAll();
    }
    super.dispose();
  }
  
  
  public void startUpTheApp() {
    serverApp = new ServerListenner(getTextArea());

    int ret = serverApp.listen(Config.getInstance().getInteger("socket_server_port"));
    System.exit(ret);    
  }


  public static void main(String args[]) {
    new MultiThreadedServerA2().startUpTheApp();
  }

  
}
