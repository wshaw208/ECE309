import java.io.*;

public class FlexibleInput {
	
	public static void main(String[] args)
	{
		if(args.length == 0)
		{}
		BufferedReader br = new BufferedReader( new InputStreamReader(System.in));
		System.out.println("You may begin typing. To exit type 'exit'.");
		while(true)
		{
			try
			{
				String input = br.readLine();
				System.out.println(input);
				if(input.equals("exit"))
				{
					System.out.println("you have now exited keyboard input.");
					return;
				}
			}
			catch(IOException ioe)
			{}
		}
	}
}
