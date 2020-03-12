import java.awt.*;
import java.applet.*;
import java.net.*;

import javax.swing.*;

import java.io.*;

public class MyClientWin extends Applet implements Runnable
{
	static boolean bConnected = false;
	
	static Socket mySocket = null;
    static PrintWriter out = null;
    static BufferedReader in = null;
	static String sMyId = null;
	static String sIP = null;
	
	static String fromServer = null;
    static String fromUser = null;

    static TextField textField;
    static TextArea textArea;
    
    static HDB3 hdb3;

    
    static String sConnection = "Not Connected to the chat server!";
	
	Thread thread;
   
	    
        
    public void init()
    {
        textField = new TextField("", 50);
        textArea = new TextArea("No Messages",15, 50);
        Button button = new Button("Connect");
        Button closebutton = new Button("Close");
        Button msgbutton = new Button("Send Message");
        //Button chkmsgbutton = new Button("Check Messages");
        
        hdb3 = new HDB3();
        
        add(textField);
        add(button);
        add(closebutton);
        add(msgbutton);
        //add(chkmsgbutton);
		add(textArea);
    }

    public void paint(Graphics g)
    {
        Font font = new Font("Arial", Font.PLAIN, 12);
        Font fontb = new Font("Arial", Font.BOLD, 14);
        
        g.setFont(fontb);
        
	    g.drawString(sConnection, 60, 330);
        
        /*try {
			fromServer = in.readLine();
		}catch (InterruptedIOException e) { }	
			
			if (fromServer) != null) {	
				textArea.setText(textArea.getText()+ "\n" + fromServer);	
			}
		*/
        
    }
	//***********************************************
	// trapping button actions
	//
    //***********************************************
    public boolean action(Event evt, Object arg) {
    	String sTemp = null;
		
		//******************************************
		// close button pressed
		//******************************************
		if (arg == "Close") {
			try {
				if (bConnected)
					mySocket.close();
			} catch (IOException e) {}
			
			System.exit(0);
		}
		
		//********************************************
		// connect button pressed
		//******************************************
		if (arg == "Connect" && !bConnected) {
					
			try {
				//
				// get server IP and name of client
				//
				sIP = JOptionPane.showInputDialog("Enter IP of chat server:");
				//
				// get client name used for communication to other people
				//
				sMyId = JOptionPane.showInputDialog("Enter your name:");
				
				//
				//get port number
				//
				int nPort = 4444; // default 
				nPort = Integer.parseInt(JOptionPane.showInputDialog("Enter port number:"));
				
				//
				// connect to the socket
				//
				mySocket = new Socket(sIP, nPort);
				
				// optional - setting socket timeout to 5 secs
				// this is not necessary because application
				// runs with multiple threads
				//
				//mySocket.setSoTimeout(5000);
								
				bConnected = true;
				//
				// define input and output streams for reading and
				// writing to the socket
				//
				out = new PrintWriter(mySocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
				out.println(sMyId);
				//
				// set screen messages
				//
				sConnection = "Connected to the chat server!";			
				
				//
				// define new thread
				//
				thread = new Thread(this);
				thread.start();	
				
			} catch (UnknownHostException e) {
				bConnected = false;
				sConnection = "Not Connected to the chat server!";
				JOptionPane.showMessageDialog(null,"Don't know about host: " + sIP);
			} catch (IOException e) {
				bConnected = false;
				sConnection = "Not Connected to the chat server!";
				JOptionPane.showMessageDialog(null,"Server is not running!"); }		
		}// end of connect button
		
		//*****************************************************	
		// Send Message button pressed
		//*****************************************************	
		if (arg == "Send Message") {
			if (textField.getText() != null){
				//
				// copy content of the message text into 
				// internal buffer for later processing
				// only one message can be stored into the
				// buffer
				//
				fromUser = textField.getText();
				textField.setText("");
				}
			else
				fromUser = null;
			
		}
		
		//
		// repaint the screen
		//           
        repaint();
		
        return true;
    }
    
    
	//************************************************
	// main
	//
	// main application method for the class
	// it will initialize whole environment
	//
	//************************************************
	public static void main(String args[]){
		String sTemp = null;
		//
		// define window and call standard methods
		//
		MyClientWin app = new MyClientWin();
		Frame frame = new Frame ("Nick & Hamza - Client Chatting Program");
		app.init();
		app.start();
		
		frame.add("Center",app);
		frame.resize(400,400);
		frame.show();
	
	}// end of main

	//***********************************
	// stop
	//***********************************
	public void stop() {
		thread.stop();
	}// end of stop

	//***********************************
	// run - thread method
	//***********************************
	public void run() {
		boolean bLoopForever = true;
		while (bLoopForever){
			//
			// call function to read/write from/to server
			//
			checkServer();
			try {
				//
				// put thread into some delay to 
				// give more cpu time to other processing
				//
				thread.sleep(10);
			} catch (InterruptedException e) {}
		}
	}// end of run
	//***********************************
	// checkServer - this is a main client algorithm
	//***********************************
	public static void checkServer(){
	
	String sTemp = null;
	boolean bLoop = true;
	String sFrameType = null;
 
	try {
		//
		// read from the server socket
		//
		if ((fromServer = in.readLine()) != null){
			
			//
			// simplified frame types: SEL, POL, ACK, NAC
			//
			
			//
			// determine what type of frame has been received
			// this is a simplified way of doing
			//
			sFrameType = fromServer.substring(0,3);
			
			//
			// received SELECT type of frame
			//
			if (sFrameType.equals("SEL")) {
				fromServer = fromServer.substring(4,fromServer.length());
				
				if(fromServer.contains("0") ||fromServer.contains("+") || fromServer.contains("-")){
					System.out.println("GOT MESSAGE FROM SERVER: " + fromServer);
					fromServer = hdb3.decodeHdb3(fromServer);
					System.out.println("DECODED MESSAGE FROM SERVER : " + fromServer);
				}
//				
				sTemp = textArea.getText();
				
				//
				// put message on screen		
				//
				textArea.setText(sTemp + "\n" + fromServer);
			}
		
			//
			// if received frame was POLLING
			// and data to be send 
			// return ACK with data
			// otherwise
			// return NACK frame
			//
			if (sFrameType.equals("POL")) {
				//
				// message in stack to be send to the server
				//
				if (fromUser != null){
					System.out.println("User is trying to say " + fromUser);
					String encodedText =  hdb3.convertHDB3(fromUser);
					out.println("ACK"+ sMyId + " says: " + encodedText);
					System.out.println("Encoded user message to be: " +encodedText);
					fromUser = null;
				}
				else
					out.println("NAC");
			}
		}// end of if anything from server
		//
		// trap exceptions while reading/writing from/to server
		//
		}catch (InterruptedIOException e) { }	
		 catch (IOException e) { }	

	}// end of checkserver

}// end of class MyClientWin
