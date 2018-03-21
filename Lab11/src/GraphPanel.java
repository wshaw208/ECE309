import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GraphPanel extends JPanel implements MouseListener, Runnable {
	
	private int graphResolution;
	private int graphTics = 10; // number of values on x and y axis
	private double rangeX, rangeY;
	private int width = 800;
	private int height = 800;
	private int xOffset = 0;
	private int yOffset = 0;
	private String expression = "";
	private double answers[][]; 	// [0][i] = x value, [1][i] = y value
	private int graphAnswers[][]; 	// [0][i] = x value, [1][i] = y value
	
	private JFrame graphWindow = new JFrame("Graph of Function");
	private JPanel graphPanel = new JPanel();
	
	private Graphics g;
	
	private JFrame displayXYpairWindow = new JFrame("XYcordinates");
	private JPanel xyPanel = new JPanel();
	private JTextField xTextField = new JTextField();
	private JTextField yTextField = new JTextField();
	
	public GraphPanel(String expr, int graphRes, double[][] ans) throws IllegalArgumentException {
		// Save the constructor parameters in instance variables
		expression = expr;
		graphResolution = graphRes;
		answers = ans;
		rangeX = answers[0][graphResolution-1] - answers[0][0];
		
		scaleAnswers(answers);
		
		graphWindow.add(graphPanel, "Center");
		graphWindow.setBackground(Color.WHITE);
		graphWindow.setSize(width, height);
		graphWindow.setVisible(true);
		
		xyPanel.setLayout(new GridLayout(2,1));
		xyPanel.add(xTextField);
		xyPanel.add(yTextField);
		displayXYpairWindow.add(xyPanel, "North");
		displayXYpairWindow.setSize(100, 50);
		displayXYpairWindow.setUndecorated(true);
		
		g = graphPanel.getGraphics();
		graphPanel.addMouseListener(this);
		g.setColor(Color.red);
		g.setFont(new Font("Times Roman", Font.BOLD, 20));
		g.drawString(expression, 0, 0);

		Thread t = new Thread(this);
		t.start();
		graphWindow.addWindowListener(new WindowAdapter()
				{
					@Override
					public void windowClosing(WindowEvent e)
					{
						t.stop();
					}
				});
	}
	
	@Override
	public void run()
	{
		while(true)
		{
			paint(g);
			try
			{
				Thread.sleep(1000);
			}
			catch(InterruptedException ie)
			{}
		}
	}
	
	@Override
	public void paint(Graphics g) {		
		width = graphPanel.getWidth();
		height = graphPanel.getHeight();
		xOffset = graphPanel.getLocation().x;
		yOffset = graphPanel.getLocation().y;
		
		scaleAnswers(answers);
		drawGraph();
		drawAxis();
		
		System.out.println("Current graph size is " + width + " x " + height);
	}
	
	public void scaleAnswers(double answers[][]) 
	{
		graphAnswers = new int[2][graphResolution];
		rangeX = answers[0][graphResolution-1] - answers[0][0];
		rangeY = answers[1][graphResolution-1] - answers[1][0];
		for(int i=0;i<graphResolution;i++)
		{
			graphAnswers[0][i] = (int) (i*width/graphResolution); // x values
			graphAnswers[1][i] = (int) (height/2 - answers[1][i]*height*graphResolution/width); // y values
		}
	}
	
	public void drawGraph() 
	{
		g.setColor(Color.BLACK);
		for(int i=0;i<(graphResolution-1);i++) // i<(graphResolution-1) because we want to draw up to the last point 
		{
			g.drawLine(graphAnswers[0][i]+xOffset, graphAnswers[1][i]+yOffset, 
					   graphAnswers[0][i+1]+xOffset, graphAnswers[1][i+1]+yOffset);
		}
	}
	
	public void drawAxis() 
	{
		double minX = answers[0][0];
		double minY = answers[1][0];
		int xPos = 0;
		int yPos = 0;
		
		for(int i = 1; i<graphResolution;i++)
		{
			if(answers[0][i] > minX && answers[0][i] < 0) // checks negatives values
			{
				minX = answers[0][i];
				xPos = i;
			}
			else if(answers[0][i] < minX && answers[0][i] > 0) // checks positive values
			{
				minX = answers[0][i];
				xPos = i;
			}
			
			if(answers[1][i] > minY && answers[1][i] < 0) // checks negatives values
			{
				minY = answers[1][i];
				yPos = i;
			}
			else if(answers[1][i] < minY && answers[1][i] > 0) // checks positive values
			{
				minY = answers[1][i];
				yPos = i;
			}
		}
		
		double yAxisValues[] = new double[graphTics];
		double yMax = 0;
		int graphMin = height;
		for(int i=0;i<graphResolution;i++)
		{
			if(graphAnswers[1][i] < graphMin && graphAnswers[1][i] >= 0) // Finds max Y value
			{
				yMax = answers[1][i];
				graphMin = graphAnswers[1][i];
			}
		}
		for(int i=0;i<graphTics;i++)
		{
			yAxisValues[i] = yMax - i*2*yMax/graphTics;
		}
		
		int xAxis = graphAnswers[1][yPos]; //Position on y
		int yAxis = graphAnswers[0][xPos]; //Position on x
		g.drawLine(0, xAxis, width, xAxis); //draws xAxis
		g.drawLine(yAxis, 0, yAxis, height);//draws yAxis
		
		for(int i=0;i<graphResolution;i+=graphResolution/graphTics)
		{
			g.drawLine(graphAnswers[0][i] + xOffset, xAxis+5 + yOffset, 
					   graphAnswers[0][i] + xOffset, xAxis-5 + yOffset); // draws tick marks on xAxis
			
			g.drawString(String.valueOf(answers[0][i]).substring(0,4), width*i/graphResolution + xOffset, xAxis-5 + yOffset); // Writes scale on xAxis
			
			g.drawLine(yAxis+5 + xOffset, (height*i/graphResolution) + yOffset, 
					   yAxis-5 + xOffset, (height*i/graphResolution) + yOffset); // draws tick marks on yAxis
		}
		for(int i=0;i<graphTics;i++)
		{
			if(String.valueOf(yAxisValues[i]).length() < 4)
			{
				g.drawString(String.valueOf(yAxisValues[i]), yAxis+5 + xOffset, height*i/graphTics + yOffset); // Writes scale on yAxis
			}
			else
			{
				g.drawString(String.valueOf(yAxisValues[i]).substring(0,4), yAxis+5 + xOffset, height*i/graphTics + yOffset); // Writes scale on yAxis
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent me)
	{
		// xTextField and yTextField are in the mini displayXYpairWindow
	    int xInPixels = me.getX();
	    int xPos = 0;
		
		for(int i = 0; i<graphResolution;i++)
		{
			if(graphAnswers[0][i] <= xInPixels)
			{
				xPos = i;
			}			
		}

	    String xValueString = String.valueOf(answers[0][xPos]);
	    xTextField.setText("X = " + xValueString.substring(0,5));
	  
	    String yValueString = String.valueOf(answers[1][xPos]);
	    yTextField.setText("Y = " + yValueString.substring(0,5));

	    // show mini x,y display window
	    displayXYpairWindow.setLocation(me.getX()+graphWindow.getX(), me.getY()+graphWindow.getY());
	    displayXYpairWindow.setVisible(true); 
	}

	@Override
	public void mouseReleased(MouseEvent me) 
	{
		// "erase" mini x,y display window	
	    displayXYpairWindow.setVisible(false);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
