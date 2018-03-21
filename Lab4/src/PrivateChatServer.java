import java.awt.List;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.*;

public class PrivateChatServer implements Runnable
{
	ConcurrentHashMap<String,ObjectOutputStream> whosIn = new ConcurrentHashMap<String,ObjectOutputStream>();
	ConcurrentHashMap<String,String> clients = new ConcurrentHashMap<String,String>();
	private ConcurrentHashMap<String, Vector<String>> savedMessages = new ConcurrentHashMap<String, Vector<String>>();
	private Vector<String> whosNotIn;
	private ServerSocket ss;
	
	public static void main(String[] args) throws Exception
	{
		new PrivateChatServer();
	}
	
	public PrivateChatServer() 
	{
		try
		{
			System.out.println("William Shaw");
			ss = new ServerSocket(5555);
			String myAddress = InetAddress.getLocalHost().getHostAddress();
			System.out.println("Chat Server address is: " + myAddress + " and on port 5555");
			new Thread(this).start();
		} 
		catch (IOException e) 
		{
			return;
		}
		
		try // Load passwords into server
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
		
		try // Load any saved messages into the server
		{
			FileInputStream fis = new FileInputStream("savedMessages.ser");
			try
			{
				ObjectInputStream ois = new ObjectInputStream(fis);
				savedMessages = (ConcurrentHashMap<String, Vector<String>>) ois.readObject();
				ois.close();
				System.out.println(savedMessages); // show saved messages
			}
			catch(Exception e)
			{}
		}
		catch(FileNotFoundException fnfe)
		{
			System.out.println("savedMessages.ser was not found, so an empty collection will be used.");
		}
		
		whosNotIn = new Vector(clients.keySet()); // set not in list to the clients list due to server initialization 
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
				whosNotIn.remove(chatName);
				oos.writeObject("Welcome " + chatName);
				sendToAll("Welcome to " + chatName + " who has just joined!");
				whosInChatRoom();
				whosNotInChatRoom();
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
						whosNotIn.remove(chatName);
						oos.writeObject("Welcome " + chatName);
						sendToAll("Welcome to " + chatName + " who has just joined!");
						whosInChatRoom();
						whosNotInChatRoom();
					}
					else // if yes, the close out previous session and open new session
					{
						ObjectOutputStream previousOOS = whosIn.get(chatName);
						previousOOS.writeObject("Session terminated due to join from another location.");
						previousOOS.close();
						whosIn.replace(chatName, oos);
						clients.put(chatName, enteredPassword); // add users to lists
						whosIn.put(chatName, oos);
						whosNotIn.remove(chatName);
						oos.writeObject("Welcome " + chatName);
						whosInChatRoom();
						whosNotInChatRoom();	
					}
				}
			}
		}
		catch(Exception e)
		{
			return;
		}
		
		savePasswords();
		
		Vector<String> savedMessageList = savedMessages.get(chatName);
		if(savedMessageList != null)
		{
			while(!savedMessageList.isEmpty())
			{
				String savedMessage = savedMessageList.remove(0);
				try
				{
					oos.writeObject(savedMessage);
				}
				catch(Exception e)
				{
					savedMessageList.add(0, savedMessage);
					break;
				}
			}
			saveSavedMessages();
		}
		
		try{
			while(true)
			{
				Object messageFromClient = ois.readObject();
				System.out.println("Received '" + messageFromClient + "' from " + chatName);
				if(messageFromClient instanceof String)
				{
					sendToAll(chatName + " says: " + messageFromClient);
				}
				else if(messageFromClient instanceof String[])
				{
					String[] recieverNames = (String[]) messageFromClient;
					if(recieverNames.length >=1)
					{
						if(whosNotIn.contains(recieverNames[1]))
						{
							saveMessage(recieverNames, chatName);
						}
						else
						{
							sendPrivate(recieverNames, chatName);
						}
					}
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
			whosInChatRoom();
			whosNotInChatRoom();
			sendToAll("Goodbye to " + chatName + " who has just left the chat room.");
			whosIn.remove(chatName);
			whosNotIn.add(chatName);
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
	
	private void whosInChatRoom()
	{
		System.out.println("Currently in the chat room:");
		sendToAll("Currently in the chat room:");
		String[] inNames = whosIn.keySet().toArray(new String[0]);
		Arrays.sort(inNames);
		for(String name : inNames)
		{
			System.out.println(name);
			sendToAll(name);
		}
		sendToAll(inNames);
	}
	
	private void whosNotInChatRoom()
	{
		System.out.println("Currently NOT in the chat room:");
		sendToAll("Currently NOT in the chat room:");
		String[] outNames = whosNotIn.toArray(new String[0]);
		Arrays.sort(outNames);
		for(String name : outNames)
		{
			System.out.println(name);
			sendToAll(name);
		}
		sendToAll(outNames);
	}
	
	private void sendPrivate(String[] recipients, String sender)
	{
		String message = recipients[0];
		Vector<String> intendedRecipients = new Vector<String>();
		for(int n=1; n<recipients.length;n++)
		{
			intendedRecipients.add(recipients[n]);
		}
		System.out.println("Received PRIVATE message '" + message
							+ "' from " + sender + " to " + intendedRecipients);
		
		Vector<String> successfulRecipients = new Vector<String>();
		for(int i=0; i<intendedRecipients.size();i++)
		{
			ObjectOutputStream temp = whosIn.get(intendedRecipients.get(i));
			try
			{
				temp.writeObject(message);
				successfulRecipients.add(intendedRecipients.get(i));
			}
			catch(IOException iot)
			{}
		}
		ObjectOutputStream sendBack = whosIn.get(sender);
		try
		{
			sendBack.writeObject(successfulRecipients);
		}
		catch(IOException ioe)
		{}
	}
	
	private void saveMessage(String[] recipients, String sender)
	{
		String message = recipients[0];
		Vector<String> intendedRecipients = new Vector<String>();
		for(int n=1; n<recipients.length;n++)
		{
			intendedRecipients.add(recipients[n]);
		}
		System.out.println("Received SAVE message '" + message
							+ "' from " + sender + " to " + intendedRecipients);
		
		for(int i=0; i<intendedRecipients.size();i++)
		{
			Vector<String> messageList = savedMessages.get(intendedRecipients.get(i));
			if(messageList == null)
			{
				messageList = new Vector<String>();
				savedMessages.put((String)intendedRecipients.get(i), messageList);
			}
			messageList.add(sender + " sent on " + new Date() + " " + message);
		}
		saveSavedMessages();
		
		ObjectOutputStream saverOOS = whosIn.get(sender);
		try
		{
			saverOOS.writeObject("Your Message " + message + " was saved for " + intendedRecipients);
		}
		catch(Exception e)
		{}
	}
	
	private synchronized void saveSavedMessages()
	{
		try 
		{
			FileOutputStream fos = new FileOutputStream("savedMessages.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(savedMessages);
			oos.close();
		}
		catch(Exception e)
		{
			System.out.println("savedMessages.ser cannot be saved: " + e);
		}
	}
}


