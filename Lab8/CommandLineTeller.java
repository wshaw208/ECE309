import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;

public class CommandLineTeller {

	public static void main(String[] args) throws Exception
	{
		// Server initialization
		TellerServer server = null;
		if(args.length == 0)
		{
			server = (TellerServer) Naming.lookup("TellerServices");
			System.out.println("The client has connected to the server at localhost.");
		}
		else
		{
			server = (TellerServer) Naming.lookup("rmi://" + args[0] + "/TellerServices");
			System.out.println("The client has connected to the server at " + args[0]);
		}
		
		if(server == null)
		{
			System.out.println("The client can't find the server!");
		}
		
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader keyboard = new BufferedReader(isr);
		// End of initialization
		
		// Begin Receiving of commands
		while(true)
		{
			System.out.println("Type a command to perform.");
			String command = keyboard.readLine().trim();
			
			if(command.contains("(") && command.contains(")"))
			{
				String commandName = command.substring(0, command.indexOf("("));

				if(commandName.equals("openNewAccount"))
				{
					if(command.contains(","))
					{
						String accountType = command.substring(command.indexOf("(")+1,command.indexOf(","));
						if(checkAccountType(accountType))
						{
							String accountName = command.substring(command.indexOf(",")+1,command.indexOf(")"));
							if(checkAccountName(accountName))
							{
								// Everything checks out call command
								String serverMessage = server.openNewAccount(accountType, accountName);
								System.out.println(serverMessage);
							}
						}
					}
					else
					{
						System.out.println("Incorrect Syntax");
					}
				}
				else if(commandName.equals("closeOutAccount"))
				{
					if(command.contains(","))
					{
						String accountNumber = command.substring(command.indexOf("(")+1,command.indexOf(","));
						if(checkAccountNumber(accountNumber))
						{
							String accountName = command.substring(command.indexOf(",")+1,command.indexOf(")"));
							if(checkAccountName(accountName))
							{
								// Everything checks out call command
								String serverMessage = server.closeOutAccount(Integer.parseInt(accountNumber), accountName);
								System.out.println(serverMessage);
							}
						}
					}
					else
					{
						System.out.println("Incorrect Syntax");
					}
				}
				else if(commandName.equals("processAccount"))
				{
					if(command.contains(","))
					{
						String processType = command.substring(command.indexOf("(")+1,command.indexOf(","));
						if(checkProcessType(processType))
						{
							String postComma = command.substring(command.indexOf(",")+1);
							if(postComma.contains(","))
							{
								String accountNumber = postComma.substring(0,postComma.indexOf(","));
								if(checkAccountNumber(accountNumber))
								{
									String amount = postComma.substring(postComma.indexOf(",")+1,postComma.indexOf(")"));
									if(checkAmount(amount))
									{
										// Everything checks out call command
										String serverMessage = server.processAccount(processType, Integer.parseInt(accountNumber), Double.parseDouble(amount));
										System.out.println(serverMessage);
									}
								}
							}
							else
							{
								System.out.println("Not enough parameters entered.");
							}
						}
					}
					else
					{
						System.out.println("Incorrect Syntax");
					}
				}
				else if(commandName.equals("showAccount"))
				{
					String accountNumber = command.substring(command.indexOf("(")+1,command.indexOf(")"));
					if(checkAccountNumber(accountNumber))
					{
						// Everything checks out call command
						String serverMessage = server.showAccount(Integer.parseInt(accountNumber));
						System.out.println(serverMessage);
					}
				}
				else if(commandName.equals("showAccounts"))
				{
					String accountName = command.substring(command.indexOf("(")+1,command.indexOf(")"));
					if(checkAccountName(accountName))
					{
						// Everything checks out call command
						String serverMessage = server.showAccounts(accountName);
						System.out.println(serverMessage);
					}
				}
				else
				{
					System.out.println("Incorrect command name entered.");
				}
			}
			else
			{
				System.out.println("Please enter the correct syntax of the command to be performed");
			}
		}
	}
	
	
	// Helper methods for parsing typed command
	private static boolean checkAccountType(String type)
	{
		if(!(type.equalsIgnoreCase("CHECKING") || type.equalsIgnoreCase("SAVINGS")))
		{
			System.out.println("Incorrect Account Type");
			return false;
		}
		return true;
	}
	
	private static boolean checkAccountNumber(String number)
	{
		if(!(number.matches("[0-9]+")))
		{
			System.out.println("Please enter an Account Number with only digits");
			return false;
		}
		return true;
	}
	
	private static boolean checkProcessType(String process)
	{
		if(!(process.equalsIgnoreCase("DEPOSIT") || process.equalsIgnoreCase("WITHDRAW")))
		{
			System.out.println("Incorrect Account Type");
			return false;
		}
		return true;
	}
	
	private static boolean checkAmount(String amount)
	{
		if(!(amount.matches("[0-9,.]+")))
		{
			System.out.println("Please enter a valid amount");
			return false;
		}
		return true;
	}
	
	private static boolean checkAccountName(String name)
	{
		if(name == null)
		{
			System.out.println("Please enter an Account Name");
			return false;
		}
		return true;
	}
	// end helper methods
}
