import java.rmi.Naming;

public class examQuestion22 {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		AnswerInterface server = null;
		
		server = (AnswerInterface) Naming.lookup("rmi://10.139.233.75/Quiz2AnswerServer");

		String answer = server.getAnswerFor("SHAW");
		System.out.println(answer);
	}

}
