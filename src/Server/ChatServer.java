package Server;

import java.io.*;
import java.net.*; //for Socket and ServerSocket
import java.util.*;


public class ChatServer {

private static final int PORT =1234;
private static Set<ClientHandler> clientHandlers = new HashSet<>();

public static void main(String[] args) {
	System.out.println("Chat server started");
	try(ServerSocket serverSocket = new ServerSocket(PORT)) {
		while(true) {
			Socket socket = serverSocket.accept();
			System.out.println("New client connected");
			
			ClientHandler clientHandler = new ClientHandler(socket);
			clientHandlers.add(clientHandler);
			new Thread(clientHandler).start();
			
		}
		
	} catch(IOException e) {
		e.printStackTrace();
	}
}
static class ClientHandler implements Runnable{
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	
	public ClientHandler(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			
			out.println("Welcome Enter name:");
			String name = in.readLine();
			ChatServer.broadcastMessage(name + "joined the chat", this);
			
			String message;
			while ((message = in.readLine()) != null) {
				System.out.println(name + ":" + message);
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
