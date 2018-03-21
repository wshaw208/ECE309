
public class WhiteHouse {

	private String thePresidentsStatement = "";
	
	public synchronized void speakAtTheNewsConference(String statement)
	{
		System.out.println("The President is speaking at the news conference: " + statement);
	}
	
	public synchronized String attendTheNewsConference(int reporterNumber, String topicOfInterest)
	{
		System.out.println("Reporter #" + reporterNumber + " is joining the news conference with topic-of-interest: " + topicOfInterest);
		return thePresidentsStatement;
	}
}
