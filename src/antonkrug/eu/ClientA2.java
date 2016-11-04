package antonkrug.eu;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class ClientA2 extends JFrame {
  private static final long serialVersionUID = 6743513674592292079L;

  private JTextField       inputRadius;
  private JTextField       inputAccount;
  private JTextArea        logTextArea;
  private DataInputStream  consumer;
  private DataOutputStream producer;
  
  private Socket connection = null;


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
    setSize(700, 700);
    
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);      
  }
  
  
  public void connect(int port) {
    try {
      connection = new Socket("localhost", port);

      //setup streams
      consumer = new DataInputStream(connection.getInputStream());
      producer = new DataOutputStream(connection.getOutputStream());
      
      Integer account = 0;
      //will validate first if the account is not text
      try {
        account = Integer.parseInt(inputRadius.getText().trim());
      }
      catch (NumberFormatException ex) {
        logTextArea.append(Messages.getString("ERROR_NUMBER") + "\n");
      }
      
      producer.writeInt(account);
      producer.flush();
      
      @SuppressWarnings("deprecation")
      String ret=consumer.readLine();
      logTextArea.append(ret + "\n");
      
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
          producer.writeDouble(radius);
          producer.flush();

          // Get result from the server
          final double area = consumer.readDouble();

          // Display to the text area
          logTextArea.append("Radius is " + radius + "\n");
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
