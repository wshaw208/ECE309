// William Shaw wtshaw
import java.io.*;
import java.net.Socket;

public class ChatClient {
	public static void main(String[] args)
	{
		String username, password, serverAddress;
		
		System.out.println("William Shaw"); // Lab3 requirement
		
		if(args.length < 3) // if not enough parameters, terminate program
		{
			System.out.println("Not enough parameters!");
			System.out.println("Please enter the following information on startup:");
			System.out.println("'username' 'password' 'server address'");
			return; // terminate the program
		}
		else if(args.length > 3) // if too many parameters, terminate program
		{
			System.out.println("Too many parameters!");
			System.out.println("Please enter the following information on startup:");
			System.out.println("'username' 'password' 'server address'");
			return; // terminate the program
		}
		else
		{
			int invalidParameter = 0;
			for(int i=0;i<3;i++)
			{
				if(args[i].contains(" ")) // check if parameter is a space
				{
					invalidParameter++;
				}
			}
			if(invalidParameter == 0) // if pass inspection, set values and connect
			{
				username = args[0];
				password = args[1];
				serverAddress = args[2];
				System.out.println("Connecting to the ChatRoom at " + serverAddress);
			}
			else // if any of the parameters is a space terminate program
			{
				System.out.println("One of the entered parameters contained a space.");
				System.out.println("Please enter parameters without spaces.");
				return; // terminate the program
			}
		}
		
		String newLine = System.lineSeparator();
		int serverPort = 3333;
		Socket s = null;
		try
		{
			s = new Socket(serverAddress, serverPort);
			System.out.println("Connected to the computer at " + serverAddress + " on port " + serverPort);
		}
		catch(Exception e)
		{
			System.out.println("The Chat Room Server is not responding."
					+ newLine + "Either the entered network address is incorrect,"
					+ newLine + "or the server computer is not up,"
					+ newLine + "or the ChatServer program is not started,"
					+ newLine + "or the ChatServer program is not accpeting connections at port 3333.");
			return; // terminate
		}
		
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		String serverReply = null;
		try
		{
			System.out.println("Sending join request for " + username);
			oos = new ObjectOutputStream(s.getOutputStream());
			oos.writeObject(username + " " + password); // command line parms 1&2
			ois = new ObjectInputStream(s.getInputStream());
			System.out.println("Waiting for server reply to join request.");
			serverReply = (String) ois.readObject();
			
			if(!serverReply.startsWith("Welcome")) return; // terminate
			
			System.out.println(serverReply);
			ChatClientGUI ccg = new ChatClientGUI(username, oos); // load GUI
			
			System.out.println("Entering Receiving Loop!!!");
			try
			{
				while(true) // Receiving Loop
				{
					serverReply = (String)ois.readObject(); // read incoming message
					System.out.println(serverReply); // print incoming message
					ccg.showIncomingMessage(serverReply); // send incoming message to GUI
				}
			}
			catch(Exception e)
			{
				System.out.println("Exiting due to lost connection to server.");
			}
		}
		catch(Exception e)
		{
			System.out.println("The remote computer has rejected our join protocol."
					+ newLine + "So the remote application we have connected to is likely not the Chat Room Server!"
					+ newLine + "Correct the network address and restart ChatClient.");
			return; // terminate
		}	
	}
}
// Use the command below to start the chat server
// java -cp C:\Users\wshaw\Documents\ECE309 ChatServer
