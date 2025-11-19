package server; //fake client to test server connection v1.1

import java.io.*; //input/output
import javax.net.ssl.SSLSocketFactory;

import javax.net.ssl.SSLSocket;

public class ChatServerTest {

	private static final int PORT = 1234;
	private static final String HOST = "localhost";
	public static void main(String[] args) {
		
		//trust server's SSL certificate as links java to correct SSL keystore & password
		System.setProperty("javax.net.ssl.trustStore", "server.keystore");
		System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
		
		System.out.println("Starting SSL client test for Level 2 on port: " + PORT); //print confirms certificate linked and test starting
		
		try {
			//Create SSL socket
            SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
            try (SSLSocket socket = (SSLSocket) ssf.createSocket(HOST, PORT)) {
            	System.out.println("Connected to SSL multi-chat server."); //confirm connection
            	
            	PrintWriter out = new PrintWriter(socket.getOutputStream(), true); //(configuring) write & broadcast messages to server
            	BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //(configuring) read byte messages from server
            	//read and send messages
            	String serverMsg = in.readLine(); //read initial server welcome message "Welcome Enter name:"
            	System.out.println("Server: " + serverMsg);
            	out.println("Tester"); //send test username to server
            	out.println("Hello from the Level 2 Test Client."); //send message to server
            	System.out.println("Message sent.");
            	
            	String receivedMsg;
            	while ((receivedMsg = in.readLine()) !=null) {
            		System.out.println("Received: " + receivedMsg);
            		break; //exit after response
            	}
            	
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("SSL test completed for Level 2!");
	}
}
/*Notes:
 * Tester needs to use same keystore > pointing truststore to same file.
 * Test Client for Level 2 also uses SSLSocketFactory instead of Socket to establish a connection.
 * It follows a simple flow of reading the welcome message sent by the server ("Welcome Enter name:"), sending a username back and then a message.
 * A few limitations: Version 1 doesn't handle multiple messages, continuous chatting and only reads one broadcast then exits.
 * Plan to upgrade and convert to JUnit test so it can automatically run rather than manually.
 * To Do: try stress testing via spawning multiple clients
 * */