import java.io.*;
import java.net.*;
import java.util.Map.Entry;
import java.util.concurrent.*;

public class ChatServer implements Runnable
{
	ConcurrentHashMap<String,ObjectOutputStream> whosIn = new ConcurrentHashMap<String,ObjectOutputStream>();
	ConcurrentHashMap<String,String> clients = new ConcurrentHashMap<String,String>();
	private ServerSocket ss;
	
	public static void main(String[] args) throws Exception
	{
		new ChatServer();
	}
	
	@SuppressWarnings("unchecked")
	public ChatServer() 
	{
		try
		{
			System.out.println("William Shaw");
			ss = new ServerSocket(3333);
			String myAddress = InetAddress.getLocalHost().getHostAddress();
			System.out.println("Chat Server address is: " + myAddress + " and on port 3333");
			new Thread(this).start();
		} 
		catch (IOException e) 
		{
			return;
		}
		
		try
		{
			FileInputStream fis = new FileInputStream("clients.ser");
			try
			{
				ObjectInputStream ois = new ObjectInputStream(fis);
				clients = (ConcurrentHashMap<String, String>) ois.readObject();
				ois.close();
			}
			catch(Exception e)
			{}
		}
		
		catch(FileNotFoundException fnfe)
		{
			System.out.println("clients.ser was not found, so an empty collection will be used.");
		}
	}
	
	public void run()
	{
		Socket s = null;
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		String firstMessage = null, chatName = null, enteredPassword = null, clientAddress = null;
		
		
		
		try 
		{
			s = ss.accept(); // wait for client to call.
			ois = new ObjectInputStream(s.getInputStream());
			firstMessage = (String) ois.readObject();
			oos = new ObjectOutputStream(s.getOutputStream());
		} 
		catch (Exception e) 
		{
			if(s != null)
			{
				try
				{
					clientAddress = s.getInetAddress().getHostAddress();
					s.close();
				}
				catch(Exception ioe)
				{}
			}
			System.out.println("Connect/join exception from " + clientAddress);
			return;
		} 
		finally
		{
			new Thread(this).start();
		}
		
		int spaceOffset = firstMessage.indexOf(" ");
		
		if(spaceOffset < 0)
		{
			try 
			{
				oos.writeObject("Invalid Format");
				System.out.println("recieved string does not contain a space.");
				s.close();
			} catch (IOException e) 
			{}
			return;
		}

		chatName = firstMessage.substring(0,spaceOffset);
		chatName = chatName.toUpperCase();
		enteredPassword = firstMessage.substring(spaceOffset+1);
		if(enteredPassword.contains(" "))
		{
			try 
			{
				oos.writeObject("Invalid Format");
				System.out.println("recieved password contains a space.");
				s.close();
			} catch (IOException e) 
			{}
			return;
		}
		
		try
		{
			if(!(clients.containsKey(chatName))) //Do we already have this user?
			{
				clients.put(chatName, enteredPassword);// if not add them
				whosIn.put(chatName, oos);
				oos.writeObject("Welcome " + chatName);
				sendToAll("Welcome to " + chatName + " who has just joined!");
			}
			else // if yes, check password
			{
				String storedPassword = clients.get(chatName);
				if(!enteredPassword.equals(storedPassword)) //is their password correct?
				{
					oos.writeObject("Password: " + enteredPassword + ", is invalid.");// if not then give error message and close
					System.out.println("recieved password does not matched one stored in system.");
					s.close();
					return;
				}
				else // if yes, see if they are still online
				{
					if(!(whosIn.contains(chatName))) // are they online?
					{ // if not then add the user
						clients.put(chatName, enteredPassword);
						whosIn.put(chatName, oos);
						oos.writeObject("Welcome " + chatName);
						sendToAll("Welcome to " + chatName + " who has just joined!");
					}
					else // if yes, the close out previous session and open new session
					{
						ObjectOutputStream previousOOS = whosIn.get(chatName);
						previousOOS.writeObject("Session terminated due to join from another location.");
						previousOOS.close();
						whosIn.replace(chatName, oos);
						clients.put(chatName, enteredPassword); // add users to lists
						whosIn.put(chatName, oos);
						oos.writeObject("Welcome " + chatName);
					}
				}
			}
		}
		catch(Exception e)
		{
			return;
		}
		
		savePasswords();
		
		try{
			while(true)
			{
				Object messageFromClient = ois.readObject();
				System.out.println("Received '" + messageFromClient + "' from " + chatName);
				if(messageFromClient instanceof String)
				{
					sendToAll(chatName + " says: " + messageFromClient);
				}
				else
				{
					sendToAll(messageFromClient);
				}
			}
		}
		catch(Exception e)
		{
			System.out.println(chatName + " is leaving the chat room.");
			whosIn.remove(chatName);
			sendToAll("Goodbye to " + chatName + " who has just left the chat room.");
			return;
		}
	}
	
	private synchronized void sendToAll(Object message)
	{
		ObjectOutputStream[] oosArray = whosIn.values().toArray(new ObjectOutputStream[0]);
		for(ObjectOutputStream clientOOS : oosArray)
		{
			try 
			{
				clientOOS.writeObject(message);
			} 
			catch (IOException e) 
			{}
		}
	}
	
	private synchronized void savePasswords()
	{
		try 
		{
			FileOutputStream fos = new FileOutputStream("clients.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(clients);
			oos.close();
		}
		catch(Exception e)
		{
			System.out.println("clients.ser cannot be saved: " + e);
		}
	}
	
}

