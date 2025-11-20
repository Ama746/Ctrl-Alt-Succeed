package client;

import java.io.*;
import java.net.*;

public class ChatClient {
	private static final int PORT = 1234;

	public static void main(String[] args){
		try(Socket socket = new Socket("localhost", PORT); //creates a network connection to server via local and port 1234 - throw exception if unreachable
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));//read byte messages from server
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true); //write/send messages to server - e.g. broadcast msgs from others
				BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
						) { 
			System.out.println("Server connected. Type here:");
			
			//thread to listen for msgs sent from server
		Thread thread = new Thread() { 
			@Override
			public void run() {
			try {
				String serverMsg; //= in.readLine();// read one line from server //c
				while ((serverMsg = in.readLine()) != null) { //keep reading until server closes
					System.out.println("Message: " + serverMsg); //c 
					} //if (serverMsg == null) println("Server disconnected"); > 
				System.out.println("Server disconnected.");
				} catch (IOException e) {
					//System.out.println("Error from server"); //confirmed
					e.printStackTrace();
				}
			}
		};
		thread.start(); //starts listening on server along with msgs from client	
			
		//input from client on console and sends to server
		String userInput; //reads one line at a time from client > holding msgs typed
		while ((userInput = input.readLine()) != null) { //loop until no input from client
			out.println(userInput);//send to server over socket via out
			if (userInput.equals("exit")) { //possible null before exit > check
				System.out.println("Disconnecting...");
				break;
			}
			}
			
		} catch (IOException e)	{
		e.printStackTrace();
		}
	}
}
