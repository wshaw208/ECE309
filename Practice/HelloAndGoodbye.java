public class HelloAndGoodbye
{
	public static void main(String[] args)
	{
		String name = "Will";
		System.out.println("Hello " + name + "!");
		SayStuff woman = new SayStuff(); // load program stuff("new" is load)
		woman.sayGoodbye(name);
	}
}