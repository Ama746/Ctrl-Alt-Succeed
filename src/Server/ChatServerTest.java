package Server; //fake client to test server connection v2 (Level 3)

import java.io.*; //input/output
import javax.net.ssl.SSLSocketFactory;

import javax.net.ssl.SSLSocket;

public class ChatServerTest {

	private static final int PORT = 1234;
	private static final String HOST = "localhost";
	public static void main(String[] args) {
		
		//trust server's SSL certificate as links java to correct SSL store & password
		System.setProperty("javax.net.ssl.trustStore", "resources/client.truststore");
		System.setProperty("javax.net.ssl.trustStorePassword", "changeit");

		System.out.println("Certificate confirmed in truststore.");
		System.out.println("Starting SSL client test for Level 3 on port: " + PORT); //print confirms certificate linked and test starting
		
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
            	out.println("Tester-Level3"); //send test username to server
            	out.println("Hello from the Level 3 Test Client."); //send message to server
            	System.out.println("Message sent.");
            	
				//spam large msgs
				System.out.println("Testing 100-character limiter control by sending long messages.");
				String longMsg = "xoxo".repeat(150);
				for (int i = 1; i <= 5; i++) { //send 5 long msgs
					out.println(longMsg);
				}
				
				//spam msgs quickly with no delay
				System.out.println("Testing rate-limiter controls by sending rapid messages.");
				for (int i = 1; i <=5; i++) { //send 5 quick msgs
					out.println("Quick message " + i);
				}

            	String receivedMsg;
            	while ((receivedMsg = in.readLine()) !=null) {
            		System.out.println("Server: " + receivedMsg);
					if (receivedMsg.contains("disconnected"))
            		break; //exit after response
            	}
            	
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Spam sent. Test completed for Level 3!");
	}
}
/*Notes:
 * Tester needs to use same keystore > pointing truststore to same file.
 * Included spam tests:
 * fast messages to trigger rate-limiter
 * long messages to trigger 100 character limiter
 * Other:
 * client.truststore contains trusted certificates
 * server.keystore contains server's private key
 * */