// William Shaw wtshaw
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.List;

public class PrivateChatClientGUI implements ActionListener {

	private ObjectOutputStream oos;
	private String username;			
	
	private JPanel topWhosInPanel = new JPanel();
	private JPanel middleWhosInPanel = new JPanel();
	private JPanel bottomWhosInPanel = new JPanel();
	private JButton sendPrivateButton = new JButton("Send Private To");
	private JButton saveMessageButton = new JButton("Save Message For");
	private JButton clearPrivateButton = new JButton("Clear Selection");
	private JButton clearSaveButton = new JButton("Clear Selection");
	private JButton futureButton = new JButton("(Future Use)");
	private JLabel errorMessageLabel = new JLabel();
	
	private JList<String> whosInList = new JList<String>();
	private JList<String> whosNotList = new JList<String>();
	private JScrollPane whosInScrollPane = new JScrollPane(whosInList);
	private JScrollPane whosNotScrollPane = new JScrollPane(whosNotList);
	
	private JFrame chatWindow = new JFrame();
	private JButton sendToAllButton = new JButton("Send To All");
	private JTextArea inTextArea = new JTextArea();
	private JTextArea outTextArea = new JTextArea();
	private JPanel bottomPanel = new JPanel();
	private JPanel middlePanel = new JPanel();
	private JPanel topPanel = new JPanel();
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
					sendToAllButton.doClick();
				}
			};
	
	public PrivateChatClientGUI(String userChatName, ObjectOutputStream sendingOOS)
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
				
		bottomWhosInPanel.setLayout(new GridLayout(1,2));
		bottomWhosInPanel.add(clearPrivateButton);
		bottomWhosInPanel.add(clearSaveButton);
		
		middleWhosInPanel.setLayout(new GridLayout(1,2));
		middleWhosInPanel.add(whosInScrollPane);
		middleWhosInPanel.add(whosNotScrollPane);
		
		topWhosInPanel.setLayout(new GridLayout(1,2));
		topWhosInPanel.add(sendPrivateButton);
		topWhosInPanel.add(saveMessageButton);
		
		saveMessageButton.setBackground(Color.yellow);
		saveMessageButton.addActionListener(this);
		
		sendPrivateButton.setBackground(Color.red);
		sendPrivateButton.addActionListener(this);
		
		sendToAllButton.setBackground(Color.green);
		sendToAllButton.setForeground(Color.black);
		sendToAllButton.addActionListener(this); // give our program's address to the button
		
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
		
		bottomPanel.setLayout(new GridLayout(1,3)); // rows, columns
		bottomPanel.add(bottomWhosInPanel);
		bottomPanel.add(errorMessageLabel);
		bottomPanel.add(futureButton);
		chatWindow.getContentPane().add(bottomPanel, "South");
		
		middlePanel.setLayout(new GridLayout(1,3)); // rows, columns
		middlePanel.add(middleWhosInPanel);
		middlePanel.add(inTextArea);
		middlePanel.add(outScrollPane);
		chatWindow.getContentPane().add(middlePanel, "Center");
		
		topPanel.setLayout(new GridLayout(1,3)); // rows, columns
		topPanel.add(topWhosInPanel);
		topPanel.add(sendToAllButton);
		topPanel.add(incomingMessagesLabel);
		chatWindow.getContentPane().add(topPanel, "North");
		
	}
	
	public void showIncomingMessage(String message)
	{
		outTextArea.append(newLine + message); //Shows each message on a new line
		outTextArea.setCaretPosition(outTextArea.getDocument().getLength());
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		// TODO Auto-generated method stub
		errorMessageLabel.setText(" ");//clear
		
		String chat = inTextArea.getText().trim(); // removes blanks at front and end
		if(chat.length() == 0) return;
		
		if(ae.getSource() == sendToAllButton)
		{
			
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
		
		if(ae.getSource() == sendPrivateButton)
		{
			List<String> whosIn = whosInList.getSelectedValuesList();
			if(whosIn.isEmpty())
			{
				errorMessageLabel.setText("No Recipients Selected");
				return;
			}
			else
			{
				System.out.println("Selected recipients of a private message are: " + whosIn);
			}
			String[] privateMsgArray = new String[whosIn.size() + 1];
			privateMsgArray[0] = chat;
			int index = 1;
			for(String chatName : whosIn)
			{
				privateMsgArray[index++] = chatName;
			}
			try
			{
				oos.writeObject(privateMsgArray);
			}
			catch(IOException ioe)
			{
				errorMessageLabel.setText("Chat Server Connection Failure");
				inTextArea.setEditable(false);
			}
		}
		
		if(ae.getSource() == saveMessageButton)
		{
			List<String> whosNotIn = whosNotList.getSelectedValuesList();
			if(whosNotIn.isEmpty())
			{
				errorMessageLabel.setText("No Recipients Selected");
				return;
			}
			else
			{
				System.out.println("Selected recipients of a private message are: " + whosNotIn);
			}
			String[] saveMsgArray = new String[whosNotIn.size() + 1];
			saveMsgArray[0] = chat;
			int index = 1;
			for(String chatName : whosNotIn)
			{
				saveMsgArray[index++] = chatName;
			}
			try
			{
				oos.writeObject(saveMsgArray);
			}
			catch(IOException ioe)
			{
				errorMessageLabel.setText("Chat Server Connection Failure");
				inTextArea.setEditable(false);
			}
		}
		
		if(ae.getSource() == clearPrivateButton)
		{
			whosInList.clearSelection();
		}
		
		if(ae.getSource() == clearSaveButton)
		{
			whosNotList.clearSelection();
		}
		
	}
	
	public void showWhosIn(String[] whosIn)
	{
		whosInList.setListData(whosIn);
	}
	
	public void showWhosNotIn(String[] whosNotIn)
	{
		whosNotList.setListData(whosNotIn);
	}
}
