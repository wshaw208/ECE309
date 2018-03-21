import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class EchoBankServer extends UnicastRemoteObject implements TellerServer
{

	public static void main(String[] args) 
	{
		try 
		{
			new EchoBankServer();
		} 
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected EchoBankServer() throws Exception
	{
		super();
		System.out.println("rmiregistry must be started before the server.");
		LocateRegistry.createRegistry(1099);
		Naming.rebind("TellerServices", this);
		System.out.println("TellerServices is up at " + InetAddress.getLocalHost().getHostAddress());
	}
	
	public String openNewAccount(String accountType, String customerName) throws RemoteException
	{
		return "A new " + accountType + " account for " + customerName + " has been created.";
	}
	
	public String closeOutAccount(int accountNumber, String customerName) throws RemoteException
	{
		return "Account #" + accountNumber + " for " + customerName + " has been closed.";
	}
	
	public String processAccount(String processType, int accountNumber, double amount) throws RemoteException
	{
		return "A " + processType + " of " + amount + " has been performed on account #" + accountNumber;
	}
	
	public String showAccount(int accountNumber) throws RemoteException
	{
		return "Showing account #" + accountNumber;
	}
	
	public String showAccounts(String customerName) throws RemoteException
	{
		return "Showing all the accountss for " + customerName;
	}

}
