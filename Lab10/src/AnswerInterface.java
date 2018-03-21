import java.rmi.*;

public interface AnswerInterface extends Remote {

	String getAnswerFor(String name) throws RemoteException;

}
