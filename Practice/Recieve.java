import java.io.*;
import java.net.*;

public class Recieve
{
	public static void main(String[] args)
	{
		// Make a ServerSocket to answer the phone!
		ServerSocket ss = new ServerSocket(1234); // listen on port 1234
		System.out.println("Receive program is up at " + InetAddress.getLocalHost());
		Socket s = ss.accept();
		DataInputStream dis = new DataInputStream(s.getInputStream());
		String message = dis.readUTF();
		System.out.println(message);
		dis.close();
		
	}
}