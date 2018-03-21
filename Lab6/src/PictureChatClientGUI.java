// William Shaw wtshaw
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.List;
import java.util.Vector;

public class PictureChatClientGUI implements ActionListener {

	private ObjectOutputStream oos;
	private String username;	
	private File localDirectory = new File(System.getProperty("user.dir"));
	
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
	
	private JPanel topWhosInPanel = new JPanel();
	private JPanel middleWhosInPanel = new JPanel();
	private JPanel bottomWhosInPanel = new JPanel();
	private JButton sendPrivateButton = new JButton("Send Private To");
	private JButton saveMessageButton = new JButton("Save Message For");
	private JButton clearPrivateButton = new JButton("Clear Selection");
	private JButton clearSaveButton = new JButton("Clear Selection");
	private JButton previewPicturesButton = new JButton("Preview Pictures To Send");
	private JLabel errorMessageLabel = new JLabel();
	
	private JList<String> whosInList = new JList<String>();
	private JList<String> whosNotList = new JList<String>();
	private JScrollPane whosInScrollPane = new JScrollPane(whosInList);
	private JScrollPane whosNotScrollPane = new JScrollPane(whosNotList);
	
	// Second window for selecting pictures
	private JFrame picturesToSendWindow = new JFrame();
	private JLabel selectPictureLabel = new JLabel("Select a picture to send");
	private JButton clearPictureSelectionButton = new JButton("Clear Selection");
	private JList<ImageIcon> picturesToSendList = new JList<ImageIcon>();
	private JScrollPane picturesScrollPane = new JScrollPane(picturesToSendList);
	
	
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
	
	public PictureChatClientGUI(String userChatName, ObjectOutputStream sendingOOS)
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
		
		System.out.println("Local directory is " + localDirectory);
				
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
		
		previewPicturesButton.addActionListener(this);
		
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
		bottomPanel.add(previewPicturesButton);
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
		
		//Second Window setup
		selectPictureLabel.setForeground(Color.blue);
		clearPictureSelectionButton.setForeground(Color.white);
		clearPictureSelectionButton.setBackground(Color.blue);		
		picturesToSendList.setSelectionMode(0);
		
		clearPictureSelectionButton.addActionListener(this);

		picturesToSendWindow.getContentPane().add(selectPictureLabel, "North");
		picturesToSendWindow.getContentPane().add(picturesScrollPane, "Center");
		picturesToSendWindow.getContentPane().add(clearPictureSelectionButton, "South");
		picturesToSendWindow.setTitle("Pictures in " + localDirectory); // set title of the window
		picturesToSendWindow.setSize(800, 800); //width, height
	}
	
	public void showIncomingMessage(String message)
	{
		outTextArea.append(newLine + message); //Shows each message on a new line
		outTextArea.setCaretPosition(outTextArea.getDocument().getLength());
	}
	
	public void showIncomingPicture(ImageIcon picture) 
	{	
		JFrame receivedPictureWindow = new JFrame();
		JList<ImageIcon> receivedPictureList = new JList<ImageIcon>();
		
		receivedPictureWindow.setLocation(0,500);
		receivedPictureWindow.setSize(picture.getIconWidth(),picture.getIconHeight());
		receivedPictureWindow.setVisible(true);
		receivedPictureWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		receivedPictureWindow.setTitle(picture.getDescription());
		receivedPictureWindow.getContentPane().add(receivedPictureList,"Center");
		ImageIcon[] arrayWithPicture = {picture}; // make an array for adding to the JList
		receivedPictureList.setListData(arrayWithPicture);
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		// TODO Auto-generated method stub
		errorMessageLabel.setText(" ");//clear
		
		if(ae.getSource() == clearPrivateButton)
		{
			whosInList.clearSelection();
			return;
		}
		if(ae.getSource() == clearSaveButton)
		{
			whosNotList.clearSelection();
			return;
		}
		if(ae.getSource() == clearPictureSelectionButton)
		{
			picturesToSendList.clearSelection();
			return;
		}
		
		if(ae.getSource() == previewPicturesButton)
		{
			System.out.println("PreviewPicturesButton pressed");
			String[] listOfFiles = localDirectory.list();
			Vector<ImageIcon> pictures = new Vector<ImageIcon>();
			for(String picture : listOfFiles)
			{
				if(picture.endsWith(".gif") || picture.endsWith(".jpg") || picture.endsWith(".png"))
				{
					pictures.add(new ImageIcon(picture, picture + " from " + username));
				}
			}
			if(pictures.isEmpty())
			{
				errorMessageLabel.setText("No pictures in " + localDirectory);
				System.out.println("No pictures (.jpg .gif .png) are found in the local directory " + localDirectory);
				return;
			}
			System.out.println("Pictures in the local directory are " + pictures);
			picturesToSendList.setListData(pictures);
			picturesToSendWindow.setVisible(true);
		}
		
		// common processing for all other  send buttons
		ImageIcon pictureToSend = picturesToSendList.getSelectedValue();
		String chat = inTextArea.getText().trim(); // removes blanks at front and end
		if((chat.length() == 0) &&(pictureToSend == null))
		{
			errorMessageLabel.setText("NO MESSAGE ENTERED or PICTURE SELECTED");
			return;
		}
		
		boolean bothMsgAndPic, pictureOnly, messageOnly;
		if((chat.length() != 0) && (pictureToSend == null))
		{
			messageOnly = true;
		}
		else
		{
			messageOnly = false;
		}
		if((chat.length() == 0) && (pictureToSend != null))
		{
			pictureOnly = true;
		}
		else
		{
			pictureOnly = false;
		}
		if((chat.length() != 0) && (pictureToSend != null))
		{
			bothMsgAndPic = true;
		}
		else
		{
			bothMsgAndPic = false;
		}
		
		
		if(ae.getSource() == sendToAllButton)
		{
			Object thingToSend = null;		
			if(messageOnly)
			{
				thingToSend = chat;
				System.out.println("Sending: " + chat + " to all.");
			}
			else if(pictureOnly)
			{
				thingToSend = pictureToSend;
				System.out.println("Sending picture to all with description: " + pictureToSend.getDescription());
			}
			else if(bothMsgAndPic)
			{
				ImageIcon copy = new ImageIcon( pictureToSend.getImage(),"from " + username + ": " + chat);
				thingToSend = copy;
				System.out.println("Sending picture to all with description: " + copy.getDescription());
			}
			
			try
			{
				oos.writeObject(thingToSend);
			}
			catch(Exception e)
			{
				System.out.println("(in send) Communications with the room server has been lost.");
				System.out.println("Restart ChatClient to re-establish a connection.");
				inTextArea.setEditable(false); // keeps the cursor out
			}
			inTextArea.setText(""); // once sent, clear all text
			picturesToSendList.clearSelection();
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
			Object[] privateMsgArray = new Object[whosIn.size() + 1];
			
			int index = 1;
			for(String chatName : whosIn)
			{
				privateMsgArray[index++] = chatName;
			}
			
			if(messageOnly)
			{
				privateMsgArray[0] = chat;
				System.out.println("Privately sending: " + privateMsgArray + " to:");
				for(int i=1;i<privateMsgArray.length;i++)
				{
					System.out.print(privateMsgArray[i]+", ");
				}
				System.out.println(""); // print blank line
			}
			else if(pictureOnly)
			{
				privateMsgArray[0] = pictureToSend;
				System.out.println("Privately sending picture to all with description: " + pictureToSend.getDescription());
			}
			else if(bothMsgAndPic)
			{
				ImageIcon copy = new ImageIcon( pictureToSend.getImage(),"from " + username + ": " + chat);
				privateMsgArray[0] = copy;
				System.out.println("Privately sending picture to all with description: " + copy.getDescription());
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
			inTextArea.setText(""); // once sent, clear all text
			picturesToSendList.clearSelection();
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
			Object[] saveMsgArray = new Object[whosNotIn.size() + 1];
			int index = 1;
			for(String chatName : whosNotIn)
			{
				saveMsgArray[index++] = chatName;
			}
			
			if(messageOnly)
			{
				saveMsgArray[0] = chat;
				System.out.println("Sending: " + saveMsgArray + " to:");
				for(int i=1;i<saveMsgArray.length;i++)
				{
					System.out.print(saveMsgArray[i]+", ");
				}
				System.out.println(""); // print blank line
			}
			else if(pictureOnly)
			{
				saveMsgArray[0] = pictureToSend;
				System.out.println("Saving picture to all with description: " + pictureToSend.getDescription());
			}
			else if(bothMsgAndPic)
			{
				ImageIcon copy = new ImageIcon( pictureToSend.getImage(),"from " + username + ": " + chat);
				saveMsgArray[0] = copy;
				System.out.println("Saving picture to all with description: " + copy.getDescription());	
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
			inTextArea.setText(""); // once sent, clear all text
			picturesToSendList.clearSelection();
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
