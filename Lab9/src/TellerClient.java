import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import javax.swing.*;

public class TellerClient implements ActionListener 
{
	private TellerServer server;
	
	private JFrame tellerWindow = new JFrame();
	
	private JButton displayAccount = new JButton("Display Account");
	private JButton deposit = new JButton("Deposit");
	private JButton withdraw = new JButton("Withdraw");
	private JButton clear = new JButton("Clear");
	private JButton close = new JButton("Close Account");
	private JButton newCheck = new JButton("Open New Checking");
	private JButton newSaving = new JButton("Open New Savings");
	
	private JTextArea accountNum = new JTextArea();
	private JTextArea amount = new JTextArea();
	private JTextArea customerName = new JTextArea();
	private JTextArea outputArea = new JTextArea();
	private String newLine = System.lineSeparator();

	
	private JLabel accountPrompt = new JLabel("Account#:");
	private JLabel amountPrompt = new JLabel("Amount:");
	private JLabel namePrompt = new JLabel("Name:");
	
	private JScrollPane output = new JScrollPane(outputArea);
	
	private JPanel topPanel = new JPanel();
	private JPanel middlePanel = new JPanel();
	private JPanel bottomPanel = new JPanel();
	
	
	public TellerClient(String serverAddress) throws Exception
	{
		server = (TellerServer) Naming.lookup("rmi://" + serverAddress + "/TellerServices");
		
		// Begin Gui
		
		displayAccount.addActionListener(this);
		deposit.addActionListener(this);
		withdraw.addActionListener(this);
		clear.addActionListener(this);
		close.addActionListener(this);
		newCheck.addActionListener(this);
		newSaving.addActionListener(this);
		
		accountNum.setFont(new Font("default",Font.BOLD,24));
		accountNum.setLineWrap(true);
		accountNum.setWrapStyleWord(true);
		
		amount.setFont(new Font("default",Font.BOLD,24));
		amount.setLineWrap(true);
		amount.setWrapStyleWord(true);
		
		customerName.setFont(new Font("default",Font.BOLD,24));
		customerName.setLineWrap(true);
		customerName.setWrapStyleWord(true);
		
		outputArea.setFont(new Font("default",Font.BOLD,24));
		outputArea.setEditable(false);
		outputArea.setLineWrap(true);
		outputArea.setWrapStyleWord(true);
		
		tellerWindow.setTitle("Teller Client"); // set title of the window
		tellerWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tellerWindow.setSize(1200, 800); //width, height
		tellerWindow.setVisible(true); // show the window
		
		topPanel.setLayout(new GridLayout(1,7));
		topPanel.add(accountPrompt);
		topPanel.add(accountNum);
		topPanel.add(displayAccount);
		topPanel.add(amountPrompt);
		topPanel.add(amount);
		topPanel.add(deposit);
		topPanel.add(withdraw);
		tellerWindow.getContentPane().add(topPanel,"North");
		
		middlePanel.setLayout(new GridLayout(1,1));
		middlePanel.add(output);
		tellerWindow.getContentPane().add(middlePanel,"Center");
		
		bottomPanel.setLayout(new GridLayout(1,6));
		bottomPanel.add(clear);
		bottomPanel.add(close);
		bottomPanel.add(namePrompt);
		bottomPanel.add(customerName);
		bottomPanel.add(newCheck);
		bottomPanel.add(newSaving);
		tellerWindow.getContentPane().add(bottomPanel, "South");	
	}
	
	public static void main(String[] args) 
	{
		if(args.length == 0)
		{
			System.out.println("Restart and please enter a server address.");
			return;
		}
		
		try
		{
			TellerClient teller = new TellerClient(args[0]);
			System.out.println("Connected to the server!");
		}
		catch(Exception e)
		{
			System.out.println("Server connection failure.!");
		}

	}
	
	@Override
	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource() == clear)
		{
			outputArea.setText("");
			return;
		}
		try
		{
			if(ae.getSource() ==  newSaving)
			{
				openNewAccount("SAVINGS");
			}
			if(ae.getSource() == newCheck)
			{
				openNewAccount("CHECKING");
			}
			if(ae.getSource() == deposit)
			{
				processAccount("DEPOSIT");
			}
			if(ae.getSource() == withdraw)
			{
				processAccount("WITHDRAW");
			}
			if(ae.getSource() == close)
			{
				closeOutAccount();	
			}
			if(ae.getSource() == displayAccount)
			{
				showAccount();
			}
		}
		catch(Exception e)
		{
			outputArea.append(e.getMessage() + newLine);
		}
	}
	
	private void openNewAccount(String type) throws Exception
	{
		if(isName())
		{
			outputArea.append(server.openNewAccount(type, getName()) + newLine);
		}
		else
		{
			outputArea.append("Please enter all arguments to make an account." + newLine);
		}
	}
	
	private void processAccount(String process) throws Exception
	{
		if(isAccount() && isAmount())
		{
			String amounts = getAmount();
			if(amounts.contains("$"))
			{
				amounts = amounts.substring(0,amounts.indexOf("$")) + amounts.substring(amounts.indexOf("$")+1);
			}
			outputArea.append(server.processAccount(process, Integer.parseInt(getAccountNumber()), Double.parseDouble(amounts)) + newLine);
		}
		else
		{
			outputArea.append("Please enter all arguments to process an account." + newLine);
		}
	}

	private void closeOutAccount() throws Exception
	{
		if(isAccount() && isName())
		{
			outputArea.append(server.closeOutAccount(Integer.parseInt(getAccountNumber()), getName()) + newLine);
		}
		else
		{
			outputArea.append("Please enter all arguments to close an account." + newLine);
		}
	}
	
	private void showAccount() throws Exception
	{
		if(isAccount())
		{
			outputArea.append(server.showAccount(Integer.parseInt(getAccountNumber())) + newLine);
		}
		else
		{
			outputArea.append("Please enter an Account Number." + newLine);
		}
	}
	
	private boolean isAccount()
	{
		if(getAccountNumber() != null && getAccountNumber().matches("[0-9]+"))
		{
			return true;
		}
		outputArea.append("Please enter an account number with only digits." + newLine);
		return false;
	}
	
	private boolean isAmount()
	{
		if(getAmount() != null && getAmount().matches("[0-9,.,$]+"))
		{
			return true;
		}
		outputArea.append("Please enter an amount without letters or special characters." + newLine);
		return false;
	}
	
	private boolean isName()
	{
		String name = getName();
		if(name != null && name.contains(",") && name.indexOf(",") != 0 && name.indexOf(",") != name.length())
		{//    length > 0		has a ,					, is not at start        		, is not at end	
			String lastName = name.substring(name.indexOf(",")+1);
			if((!lastName.contains(",") && !lastName.contains("_")))
			{
				return true;
			}
		}
		outputArea.append("Please enter a vaild name." + newLine);
		return false;
	}
	
	private String getAccountNumber()
	{
		return accountNum.getText();
	}
	
	private String getAmount()
	{
		return amount.getText();
	}
	
	private String getName()
	{
		return customerName.getText();
	}
}
