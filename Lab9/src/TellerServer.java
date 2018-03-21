import java.rmi.*;

public interface TellerServer extends Remote
{
	String openNewAccount(String accountType, String customerName) throws RemoteException;
	
	String closeOutAccount(int accountNumber, String customerName) throws RemoteException;
	
	String processAccount(String processType, int accountNumber, double amount) throws RemoteException;
	
	String showAccount(int accountNumber) throws RemoteException;
	
	String showAccounts(String customerName) throws RemoteException;
}
