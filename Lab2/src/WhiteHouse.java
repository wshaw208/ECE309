
public class WhiteHouse {

	private String thePresidentsStatement = "";
	
	public synchronized void speakAtTheNewsConference(String statement)
	{
		thePresidentsStatement = statement;
		System.out.println("The President is speaking at the news conference: " + thePresidentsStatement);
		notifyAll();
	}
	
	public synchronized String attendTheNewsConference(int reporterNumber, String topicOfInterest)
	{
		System.out.println("Reporter #" + reporterNumber + " is joining the news conference with topic-of-interest: " + topicOfInterest);
		while(true)
		{
			try
			{
				wait();
			}
			catch(InterruptedException ie)
			{}
			
			if(thePresidentsStatement.contains(topicOfInterest) || thePresidentsStatement.contains("God bless America"))
			{
				return thePresidentsStatement;
			}
		}
	}
}
