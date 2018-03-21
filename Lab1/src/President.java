import java.io.IOException;

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
		whiteHouse.speakAtTheNewsConference("Good afternoon.");
	}
}
