import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

public class NekoTheCat implements MouseListener, Runnable {
	
	Image catRight1 = new ImageIcon(getClass().getResource("Neko1.gif")).getImage();
	Image catRight2 = new ImageIcon(getClass().getResource("Neko2.gif")).getImage();
	Image catLeft1 = new ImageIcon(getClass().getResource("Neko3.gif")).getImage();
	Image catLeft2 = new ImageIcon(getClass().getResource("Neko4.gif")).getImage();
	Image redBall = new ImageIcon(getClass().getResource("red-ball.gif")).getImage();
	
	Image cat1 = catRight1;
	Image cat2 = catRight2;
	Image currentImage = catRight1;
	
	JFrame gameWindow = new JFrame("Neko The Cat!");
	JPanel gamePanel = new JPanel();
	
	int catxPosition = 1;
	int catyPosition = 50;
	int catWidth = catRight1.getWidth(gamePanel);
	int catHeight = catRight1.getHeight(gamePanel);
	int ballxPosition = 0;
	int ballyPosition = 0;
	int ballSize = redBall.getWidth(gamePanel);
	int sleepTime = 100;
	int xBump = 10;
	boolean catIsRunningToTheRight = true;
	boolean catIsRunningToTheLeft = false;
	boolean ballHasBeenPlaced = false;
	Graphics g;
	AudioClip soundFile = Applet.newAudioClip(getClass().getResource("spacemusic.au"));
	
	public NekoTheCat()
	{
		gameWindow.add(gamePanel,"Center");
		gameWindow.setBackground(Color.WHITE);
		gameWindow.setSize(800,600);
		gameWindow.setVisible(true);
		gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		g = gamePanel.getGraphics();
		gamePanel.addMouseListener(this);
		soundFile.loop();
		
		g.setFont(new Font("Times Roman", Font.BOLD, 20));
		g.drawString("Neko the cat is looking for it's red ball!",100,100);
		g.drawString("Click the mouse to place Neko's ball.",100,120);
		g.drawString("Can you move the ball to keep Neko from getting it?",100,140);
		g.drawString("(Pull window larger to make the game easier)",100,160);
		
		new Thread(this).start();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		NekoTheCat iHateMyLife = new NekoTheCat();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		g.drawImage(catRight1,0,0,gamePanel); // imageName, x coordinate, y coordinate, where to draw
		g.drawImage(catRight2,1*catWidth,0,gamePanel);
		g.drawImage(catLeft1,2*catWidth,0,gamePanel);
		g.drawImage(catLeft2,3*catWidth,0,gamePanel);
		g.drawImage(redBall,4*catWidth,0,gamePanel);
		
		while(true)
		{
			// 1. Blank out the last image
			g.setColor(Color.white);
			g.fillRect(catxPosition, catyPosition, catWidth, catHeight);
			
			// 2. If necessary, redirect Neko toward the ball.
			if(ballHasBeenPlaced)
			{
				if(catyPosition < ballyPosition)
				{
					catyPosition += 10;
				}
				
				if(catyPosition > ballyPosition)
				{
					catyPosition -= 10;
				}
				
				if(catxPosition < ballxPosition)
				{
					cat1 = catRight1;
					cat2 = catRight2;
					catIsRunningToTheRight = true;
					catIsRunningToTheLeft = false;
					catxPosition += xBump;
				}
				
				if(catxPosition > ballxPosition)
				{
					cat1 = catLeft1;
					cat2 = catLeft2;
					catIsRunningToTheRight = false;
					catIsRunningToTheLeft = true;
					catxPosition -= xBump;
				}
			}
			
			// 3. Select the next image.
			if(currentImage == cat1)
			{
				currentImage = cat2;
			}
			else
			{
				currentImage = cat1;
			}
			
			// 4. Draw the next cat image.
			g.drawImage(currentImage, catxPosition, catyPosition, gamePanel);
			
			// 5. Pause briefly to let human eye see the new image!
			try
			{
				Thread.sleep(sleepTime);
			}
			catch(InterruptedException ie)
			{}
						
			// 6. When Neko reaches the ball
			if((Math.abs(catyPosition - ballyPosition) < catHeight) && (Math.abs(catxPosition - ballxPosition) < xBump))
			{
				gamePanel.removeMouseListener(this);
				g.setColor(Color.red);
				g.setFont(new Font("Times Roman", Font.BOLD, 50));
				g.drawString("At last, I have my ball!",0,100);
				soundFile.stop();
				return;
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent me) {
		// TODO Auto-generated method stub
		ballHasBeenPlaced = true;
		
		g.setColor(Color.white);
		g.fillRect(ballxPosition, ballyPosition, ballSize, ballSize);
		
		ballxPosition = me.getX();
		ballyPosition = me.getY();
		System.out.println("Mouse clicked at x=" + ballxPosition + ",y=" + ballyPosition);
		
		g.drawImage(redBall, ballxPosition, ballyPosition, gamePanel);
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}



}
