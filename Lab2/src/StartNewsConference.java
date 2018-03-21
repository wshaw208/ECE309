
public class StartNewsConference {
	public static void main(String[] args)
	{
		if(args.length == 0)
		{
			System.out.println("Restart. Provide a command line parameter for each Reporter topic-of-interest.");
			System.out.println("(Use quotes to contain a topic that has multiple words.)");
			return; // terminate the program
		}
		
		System.out.println("ECE309 Lab2 Spring 2017 by William Shaw");
		System.out.println("Running in main() in StartNewsConference");
		System.out.println("Loading WhiteHouse");
		WhiteHouse wh = new WhiteHouse();
		
		System.out.println("Loading Reporters");
		for(int i=0; i< args.length; i++)
		{
			new Reporter(wh, i+1, args[i]);
		}
		
		System.out.println("In main() loading President");
		new President(wh);
		
		
	}
}
