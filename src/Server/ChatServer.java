package Server;

import java.io.*;
import java.net.*; //for Socket and ServerSocket
import java.util.*;
import javax.net.ssl.SSLServerSocket;//for replacing ServerSocket with SSL
import javax.net.ssl.SSLServerSocketFactory;

public class ChatServer {

private static final int PORT =1234;
//threas safe set holding connected client
private static Set<ClientHandler> clientHandlers = Collections.synchronizedSet(new HashSet<>()) ;

public static void main(String[] args) {
	
	//tells java which keystore and password for ssl
	System.setProperty("javax.net.ssl.keyStore", "resources/server.keystore");
	System.setProperty("javax.net.ssl.keyStorePassword", "changeit");
	
	System.out.println("Chat server started");
	
	//creating SSL server socket
	SSLServerSocketFactory ssf= (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
	
	try(SSLServerSocket serverSocket = (SSLServerSocket) ssf.createServerSocket(PORT)) {
		System.out.println("SSL chat server started on port " + PORT);
		
		//accepting client in loop
		while(true) {
			Socket socket = serverSocket.accept();//returns SSLsocket
			System.out.println("New SSL client connected " +socket.getRemoteSocketAddress());
			
			ClientHandler clientHandler = new ClientHandler(socket);
			clientHandlers.add(clientHandler);
			new Thread(clientHandler).start();
			
		}
		
	} catch(IOException e) {
		e.printStackTrace();
	}
}

//waits for and sends messages 
static class ClientHandler implements Runnable{
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;

	//tracks messages for level 3
	private long lastMessageTime = 0;
	private int spamWarnings = 0;
	
	public ClientHandler(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		try {
			//input and output 
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			
			out.println("Welcome Enter name:");
			String name = in.readLine();
			ChatServer.broadcastMessage(name + " joined the chat", this);
			String message;
			while ((message = in.readLine()) != null) {
			    /* System.out.println(name + ": " + message);
			    ChatServer.broadcastMessage(name + ": " + message, this); 
				*/

				//length limit- prevents resource exhaustion
				if(message.length() > 100){
					out.println("Message too long. Limit to 100 characters.");
					continue;
				}

				//rate limiting -preventing flooding 
				long now = System.currentTimeMillis();
				if(now - lastMessageTime < 300){
					spamWarnings ++;
					out.println("You are sending messages too quickly. Warning " + spamWarnings + "/5");

					if(spamWarnings >=5){
						out.println("You have been disconnected for spamming.");
						socket.close();
						return; //exit loop and disconnect
					}
					continue;
				}
				lastMessageTime = now;
				System.out.println(name + ": " + message);
				ChatServer.broadcastMessage(name + ": " + message, this);
			}

			
			} catch(IOException e) {
			System.out.println("client disconnected");
		} finally {
			ChatServer.removeClient(this);
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	void sendMessage(String message) {
		out.println(message);
	}
}
//broadcast message to all clients
 public static void broadcastMessage(String message, ClientHandler excludeUser) {
	 for(ClientHandler client: clientHandlers) {
		 if(client != excludeUser) {
			 client.sendMessage(message);
		 }
	 }
 }
public static void removeClient(ClientHandler clientHandler) {
	clientHandlers.remove(clientHandler);
	System.out.println("A user has disconnected");
}
	
}
