//import java.io.*;

public class Reporter implements Runnable
{
	
	private WhiteHouse whiteHouse;
	private int reporterNumber;
	private String topicOfInterest;
	
	public Reporter(WhiteHouse wh, int reporter, String topic)
	{
		whiteHouse = wh;
		reporterNumber = reporter;
		topicOfInterest = topic;
		new Thread(this).start();
		System.out.println("main thread running in the constructor of Reporter #" + reporterNumber + " with topic-of-interest " + topicOfInterest);
	}

	public void run()
	{
		System.out.println("Reporter #" + reporterNumber + " thread running in the run() method.");
		String thePresidentsStatement = null;
		
		thePresidentsStatement = whiteHouse.attendTheNewsConference(reporterNumber, topicOfInterest);
		
		
		if(thePresidentsStatement.contains(topicOfInterest))
		{
			System.out.println("Reporter #" + reporterNumber + " topic " + topicOfInterest + " was addressed and is leaving now.");
			System.out.println("The presidents statement about " + topicOfInterest + " is " + thePresidentsStatement);
		}
		else if(thePresidentsStatement.contains("God bless America"))
		{
			System.out.println("Reporter #" + reporterNumber + " topic " + topicOfInterest + " was not addressed and is leaving now due to conference being over.");
		}
		else
		{
			System.out.println("Reporter #" + reporterNumber + " topic " + topicOfInterest + " was not addressed and is leaving now due to an emergency.");
		}
		
	}
}
