// William Shaw wtshaweagle
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

public class ChatClientGUI implements ActionListener {

	private ObjectOutputStream oos;
	private String username;			
	
	private JFrame chatWindow = new JFrame();
	private JButton sendButton = new JButton("Send To All");
	private JTextArea inTextArea = new JTextArea();
	private JTextArea outTextArea = new JTextArea();
	private JPanel centerPanel = new JPanel();
	private JPanel northPanel = new JPanel();
	private JLabel incomingMessagesLabel = new JLabel(" Chat Messages");
	private String newLine = System.lineSeparator();
	private JScrollPane outScrollPane = new JScrollPane(outTextArea);
	
	// Setup of objects to allow pressing 'enter' key to trigger action event*****
	private static final String key = "ENTER";
	private KeyStroke keyStroke;
	private Action enterPress = new AbstractAction()
			{
				@Override
				public void actionPerformed(ActionEvent ae)
				{
					sendButton.doClick();
				}
			};
	
	public ChatClientGUI(String userChatName, ObjectOutputStream sendingOOS)
	{
		try
		{
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		
		username = userChatName;
		oos = sendingOOS;
		
		sendButton.setBackground(Color.red);
		sendButton.setForeground(Color.white);
		sendButton.addActionListener(this); // give our program's address to the button
		
		incomingMessagesLabel.setFont(new Font("default",Font.BOLD,24)); // change the font
		
		inTextArea.setFont(new Font("default",Font.BOLD,24)); // change the font
		inTextArea.setLineWrap(true);
		inTextArea.setWrapStyleWord(true);
		
		// Code to allow pressing the 'enter' key to trigger the action event*****
		keyStroke = KeyStroke.getKeyStroke(key);
		Object actionKey = inTextArea.getInputMap(JComponent.WHEN_FOCUSED).get(keyStroke);
		inTextArea.getActionMap().put(actionKey, enterPress);
		
		outTextArea.setFont(new Font("default",Font.BOLD,24)); // change the font
		outTextArea.setEditable(false);
		outTextArea.setLineWrap(true);
		outTextArea.setWrapStyleWord(true);
		
		chatWindow.setTitle(username + "'s Chat Window! (Close window to leave the Chat Room.)"); // set title of the window
		chatWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		chatWindow.setSize(1200, 800); //width, height
		chatWindow.setVisible(true); // show the window
		
		centerPanel.setLayout(new GridLayout(1,2)); // rows, columns
		centerPanel.add(inTextArea);
		centerPanel.add(outScrollPane);
		chatWindow.getContentPane().add(centerPanel, "Center");
		
		northPanel.setLayout(new GridLayout(1,2)); // rows, columns
		northPanel.add(sendButton);
		northPanel.add(incomingMessagesLabel);
		chatWindow.getContentPane().add(northPanel, "North");
		
	}
	
	public void showIncomingMessage(String message)
	{
		outTextArea.append(newLine + message); //Shows each message on a new line
		outTextArea.setCaretPosition(outTextArea.getDocument().getLength());
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		String chat = inTextArea.getText().trim(); // removes blanks at front and end
		if(chat.length() == 0) return;
		System.out.println("Sending: " + chat);
		inTextArea.setText(""); // once sent, clear all text
		
		try
		{
			oos.writeObject(chat);
		}
		catch(Exception e)
		{
			System.out.println("(in send) Communications with the room server has been lost.");
			System.out.println("Restart ChatClient to re-establish a connection.");
			inTextArea.setEditable(false); // keeps the cursor out
		}
	}
}
