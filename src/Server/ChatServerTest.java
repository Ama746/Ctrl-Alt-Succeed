package Server; //Server
//Current version 1.1 = accepts single connection, reads one line of text, replies, exits connection
//Single-chat version not yet multi-chat test
import java.io.*;
import java.net.*;

public class ChatServerTest {//to do:run chatServer in separate thread

	private static final int PORT = 1234;
	//private Socket socket;
	//private static PrintWriter out;
	//private static BufferedReader in;

	public static void main(String[] args) { //currently a server socket test
		System.out.println("Starting serverSocket test on:" + PORT);
		try(ServerSocket serverSocket = new ServerSocket(PORT)) { //connection auto closes
			System.out.println("ChatServer running, currently no connections");
			try(Socket socket = serverSocket.accept()) {//testing client connection - waiting for client then accept conn & return socket obj(specific client conn)
				System.out.println("New client connected"); //confirms conn is real 
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));//read byte messages from client
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true); //write messages send to client - e.g. broadcast msgs from others
				String message = in.readLine(); //reads one line at a time from client > could also check for disconnection before message sent > what would output be??
				//if(message==null) > detect disconnection
				System.out.println("Client sent:" + message);
				out.println("This is the Server."); //read by client 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("ChatServerTest completed"); //only 1 client atm > loop accept() for multi > thread per client??
	}
}