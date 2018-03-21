import java.io.*;

public class President implements Runnable
{
	private WhiteHouse whiteHouse;
	
	public President(WhiteHouse wh)
	{
		whiteHouse = wh;
		new Thread(this).start();
		System.out.println("main thread in President constructor starting President thread");
		System.out.println("The President thread is now executing it's run() method.");
	}
	
	public void run()
	{
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader keyboard = new BufferedReader(isr);
		
		whiteHouse.speakAtTheNewsConference("Good afternoon.");
		
		try
		{
			while(true)
			{
				try 
				{
					Thread.sleep(1000);
				}
				catch(InterruptedException ie)
				{}
				System.out.println("Acting as the President, enter a statement at the news conference");

				String statement = keyboard.readLine().trim();
				if(statement.length() == 0) continue;
				whiteHouse.speakAtTheNewsConference(statement);
				
				if(statement.contains("God bless America"))
				{
					return;
				}
			}
		}
		catch(IOException ioe)
		{}
		
	}
}
