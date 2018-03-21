
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
		whiteHouse.attendTheNewsConference(reporterNumber, topicOfInterest);
	}
}
