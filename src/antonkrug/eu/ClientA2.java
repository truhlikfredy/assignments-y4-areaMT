package antonkrug.eu;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * Simple GUI client
 * 
 * @author Anton Krug
 * @date 01.11.2016
 * @version 1
 */
public class ClientA2 extends JFrame {
  
  private static final long             serialVersionUID = 6743513674592292079L;
  private              Socket           connection       = null;
  private              BufferedReader   consumer         = null; 
  private              PrintWriter      producer         = null;
  private              JTextField       inputRadius;
  private              JTextField       inputAccount;
  private              JTextArea        logTextArea;


  public static void main(String[] args) {
    new ClientA2();
  }
  
  
  public ClientA2() {   
    setupGui();
  }
  
  
  public void setupGui() {
    logTextArea  = new JTextArea();
    logTextArea.append(Messages.getString("CLIENT_WILL_CONNECT") + "\n");

    
    inputRadius  = new JTextField();
    inputRadius.setHorizontalAlignment(JTextField.RIGHT);
    inputRadius.setPreferredSize(new Dimension(150, 17));
    inputRadius.setText("1.0");
    inputRadius.addActionListener(new Listener());
    
    inputAccount = new JTextField();
    inputAccount.setHorizontalAlignment(JTextField.RIGHT);
    inputAccount.setPreferredSize(new Dimension(150, 17));
    inputAccount.setText("1001");
    inputAccount.addActionListener(new Listener());
    
    //both fields can be handled by the same action listener
    
    JPanel inputsWest = new JPanel(new BorderLayout());
    inputsWest.add(new JLabel("Enter radius"), BorderLayout.WEST);
    inputsWest.add(inputRadius, BorderLayout.CENTER);

    JPanel inputsEast = new JPanel(new BorderLayout());
    inputsEast.add(new JLabel("Account number"), BorderLayout.WEST);
    inputsEast.add(inputAccount, BorderLayout.CENTER);
    
    JPanel inputsCombined = new JPanel(new BorderLayout());
    inputsCombined.add(inputsWest, BorderLayout.WEST);
    inputsCombined.add(inputsEast, BorderLayout.EAST);
    
    JPanel logoWithInputs = new JPanel(new BorderLayout());
    logoWithInputs.add(new JLabel(new ImageIcon(getClass().getResource("/resources/logo.png"))),BorderLayout.NORTH);
    logoWithInputs.add(inputsCombined);
    
    
    setLayout(new BorderLayout());
    add(logoWithInputs, BorderLayout.NORTH);
    add(new JScrollPane(logTextArea), BorderLayout.CENTER);

    setTitle("Area calculator client");
    setSize(600, 700);
    
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);      
  }

  
  private void authenticateAccount() {
    Integer account = 0;

    //will validate first if the account is not text
    try {
      account = Integer.parseInt(inputAccount.getText().trim());
    }
    catch (NumberFormatException ex) {
      logTextArea.append(Messages.getString("ERROR_NUMBER") + "\n");
    }
    
    producer.println(account);
    
    String ret = "Failed";
    
    try {
      ret = consumer.readLine();
    } catch (IOException ex) {
      logTextArea.append(ex.toString() + '\n');
    }
    
    logTextArea.append(ret + "\n");
    
    // If we are not Welcome then don't even try connecting to it because in
    // meantime server is closing the connection anyway
    if (!ret.startsWith("Welcome")) {
      connection = null;
    }    
  }
  
  
  public void connect(int port) {
    try {
      connection = new Socket("localhost", port);

      //setup consumer and producer streams
      consumer = new BufferedReader(new InputStreamReader( connection.getInputStream()));
      producer = new PrintWriter(connection.getOutputStream(), true);
            
      //authenticate the account first
      authenticateAccount();
      
    } catch (IOException ex) {
      logTextArea.append(Messages.getString("CLIENT_CANT_CONNECT")+" "+ port+"\n");
      logTextArea.append(ex.toString() + '\n');
    }    
  }

  
  private class Listener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      
      double radius = 0;

      // only connect when needed
      if (connection == null) {
        connect(Config.getInstance().getInteger("socket_server_port"));
      }
      
      //will validate first if the radius text field doesn't contain non-numerical characters
      try {
        radius = Double.parseDouble(inputRadius.getText().trim());
      }
      catch (NumberFormatException ex) {
        logTextArea.append(Messages.getString("ERROR_NUMBER") + "\n");
      }
      
      try {
        if (connection != null) {
          // Send the radius to the server
          producer.println(radius);

          // Get result from the server
          final double area = Double.parseDouble(consumer.readLine());

          // Display to the text area
          logTextArea.append("Radius is " + radius + "\n");
          logTextArea.append("Sending request to server IP " + connection.getInetAddress().getHostAddress() + "\n");
          logTextArea.append("Area received from the server is " + area + '\n');
        }
      } catch (IOException ex) {
        logTextArea.append("Got problem. Is still server running? \n " + ex.toString() + '\n'
            + "Press enter again to try reconnect\n");
        connection = null;
      }
    }
  }
  
  
}
