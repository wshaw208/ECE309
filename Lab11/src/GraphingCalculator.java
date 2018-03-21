//ECE 309 Lab 10 
//Authors William Shaw and Krista Wickersham

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class GraphingCalculator implements ActionListener {

	private static int errorCase = 0;
	private int graphResolution = 100;
	
	// GUI Objects
	private JFrame calculatorWindow = new JFrame("Expression Calculator");
	
	private JTextField expressionTextField = new JTextField();
	private JTextField xValueTextField = new JTextField();
	private JLabel xValueLabel = new JLabel("for x=");
	
	private JLabel xBounds = new JLabel("Bounds for X = ");
	private JTextField lowerXbound = new JTextField();
	private JLabel xBounds2 = new JLabel(" to ");
	private JTextField upperXbound = new JTextField();
	
	private JTextArea logTextArea = new JTextArea();
	private JScrollPane logScrollPane = new JScrollPane(logTextArea);
	
	private JLabel errorLabel = new JLabel("Errors");
	private JTextField errorTextArea = new JTextField();
	
	private JPanel topPanel = new JPanel();
	private JPanel topPanel1 = new JPanel();
	private JPanel topPanel2 = new JPanel();
	private JPanel middlePanel = new JPanel();
	private JPanel bottomPanel = new JPanel();
	private JButton enterButton = new JButton("Enter");

	private static final String key = "ENTER";
	private KeyStroke keyStroke;
	private Action enterPress = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent ae) {
			enterButton.doClick();
		}
	};
	
	private String newLine = System.lineSeparator();
	
	public GraphingCalculator() {
		// allow enter key to work as enter button
		keyStroke = KeyStroke.getKeyStroke(key);
		Object actionKey = expressionTextField.getInputMap(JComponent.WHEN_FOCUSED).get(keyStroke);
		expressionTextField.getActionMap().put(actionKey, enterPress);
		Object actionKey2 = xValueTextField.getInputMap(JComponent.WHEN_FOCUSED).get(keyStroke);
		xValueTextField.getActionMap().put(actionKey2, enterPress);
		Object actionKey3 = lowerXbound.getInputMap(JComponent.WHEN_FOCUSED).get(keyStroke);
		lowerXbound.getActionMap().put(actionKey2, enterPress);
		Object actionKey4 = upperXbound.getInputMap(JComponent.WHEN_FOCUSED).get(keyStroke);
		upperXbound.getActionMap().put(actionKey2, enterPress);

		expressionTextField.setFont(new Font("default",Font.BOLD,24));
		xValueTextField.setFont(new Font("default",Font.BOLD,24));
		
		logTextArea.setEditable(false);
		logTextArea.setLineWrap(true);
		logTextArea.setWrapStyleWord(true);
		logTextArea.setFont(new Font("default",Font.BOLD,24));
		
		JScrollBar vertical = logScrollPane.getVerticalScrollBar();
		vertical.setValue(vertical.getMaximum());

		// Set GUI appearance
		bottomPanel.setLayout(new GridLayout(1,1));
		bottomPanel.add(errorTextArea);
		calculatorWindow.getContentPane().add(bottomPanel, "South");

		topPanel.setLayout(new GridLayout(2,1));
		topPanel1.setLayout(new GridLayout(1,3));
		topPanel2.setLayout(new GridLayout(1,4));
		topPanel1.add(expressionTextField);
		topPanel1.add(xValueLabel);
		topPanel1.add(xValueTextField);
		topPanel2.add(xBounds);
		topPanel2.add(lowerXbound);
		topPanel2.add(xBounds2);
		topPanel2.add(upperXbound);
		topPanel.add(topPanel1);
		topPanel.add(topPanel2);
		
		calculatorWindow.getContentPane().add(topPanel, "North");

		middlePanel.setLayout(new GridLayout(1,1));
		middlePanel.add(logScrollPane);
		calculatorWindow.getContentPane().add(middlePanel, "Center");

		errorTextArea.setOpaque(true);
		errorTextArea.setEditable(false);
		errorTextArea.setBackground(Color.pink);
		errorTextArea.setFont(new Font("default",Font.BOLD,24));

		enterButton.addActionListener(this);

		calculatorWindow.setSize(1200, 800);
		calculatorWindow.setVisible(true);
		calculatorWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String answer = null;
		String expression = expressionTextField.getText();
		String variable = xValueTextField.getText();
		String lowerBound = lowerXbound.getText();
		String upperBound = upperXbound.getText();
		String incorrectChar = "";

		if(expression.contains("increments ="))
		{
			String incrementChange = expression.substring(expression.indexOf('=')+1).trim();
			graphResolution = Integer.parseInt(incrementChange);
			errorCase = 20;
		}
		else if(expression.equals(""))
		{
			errorCase = 4;
		}
		else if(expression.contains("x") && variable.equals("") && (lowerBound.equals("") || upperBound.equals("")))
		{
			errorCase = 5;
		} 
		else if(!expression.contains("x") && !variable.equals(""))
		{
			errorCase = 8;
		}
		else
		{
			answer = removeSpaces(expression);
			answer = checkNegatives(answer);
			answer = replaceValues(answer);
			
			incorrectChar = validExpression(answer);
			
			if(errorCase == 0 && !lowerBound.equals("") && !upperBound.equals(""))
			{
				// code to run graphing functions
				double lower = Double.parseDouble(lowerBound);
				double upper = Double.parseDouble(upperBound);
				double currentX = lower;
				double range = upper - lower;
				double increment = range / (graphResolution-1);
				double answers[][] = new double[2][graphResolution]; // array to hold answers
				expression = answer; // reuse expression space since we are making a graph
				String currentXspot = String.valueOf(currentX);
				for(int i = 0; i < graphResolution; i++)
				{
					answer = editVariables(expression,currentXspot);
					answer = checkNegatives(answer);
					answer = evalExpression(answer);
					
					answers[0][i] = currentX;					//Save x and answer
					answers[1][i] = Double.parseDouble(answer);
		
					currentX += increment;						//Update values for next loop
					currentXspot = String.valueOf(currentX);
				}
				
				GraphPanel graph = new GraphPanel(answer, graphResolution, answers);
			}
			else if(variable != null)
			{
				answer = editVariables(answer,variable);
				if(errorCase == 0)
				{
					answer = evalExpression(answer);
				}
			}
			else
			{
				if(errorCase == 0)
				{
					answer = evalExpression(answer);
				}
			}
			
		}
		
		if(errorCase == 0)
		{
			logTextArea.append(expression + " = " + answer);
			if(!variable.equals(""))
			{
				logTextArea.append(" for x = " + variable + newLine);
			}
			else
			{
				logTextArea.append(newLine);
			}
			expressionTextField.setText("");
			xValueTextField.setText("");
			errorTextArea.setText("");
		}
		else
		{
			
			if(errorCase == 7)
			{
				logTextArea.append(answer + newLine);
			}
			else if(errorCase == 20)
			{
				errorTextArea.setText("The new graph resolution is: " + graphResolution);
			}
			else
			{
				errorTextArea.setText(errorReport(incorrectChar));
			}
			errorCase = 0;
		}
	}

	public static void main(String[] args) throws Exception
	{
		GraphingCalculator ec = new GraphingCalculator();
	}
	
	private static String evalExpression(String expression)
	{		
		while(!checkAnswer(expression))
		{
			if(expression.contains("(") && expression.contains(")"))
			{
				expression = handleParenthesis(expression);
			}
			else
			{
				expression = processAlgebra(expression);
			}
		}

		if(expression.charAt(0) == 'n')
		{
			expression = "-" + expression.substring(1);
		}
		return expression;
	}
	
	//validExpression will check the expression for valid entry of only numbers and operands
	// returns true if contains only numbers and operands
	// returns false if it does not
	private static String validExpression(String expression)
	{
		if(!expression.matches("[0-9,.,x,n,(,),^,r,*,/,+,-]+")) // checks characters
		{
			errorCase = 1;
			for(int i = 0; i<expression.length();i++)
			{
				if(expression.charAt(i) != '0' && expression.charAt(i) != '1' && expression.charAt(i) != '2'
				&& expression.charAt(i) != '3' && expression.charAt(i) != '4' && expression.charAt(i) != '5'
				&& expression.charAt(i) != '6' && expression.charAt(i) != '7' && expression.charAt(i) != '8'
				&& expression.charAt(i) != '9' && expression.charAt(i) != '.' && expression.charAt(i) != 'n'
				&& expression.charAt(i) != '(' && expression.charAt(i) != ')' && expression.charAt(i) != '^'
				&& expression.charAt(i) != 'r' && expression.charAt(i) != '*' && expression.charAt(i) != '/'
				&& expression.charAt(i) != '+' && expression.charAt(i) != '-' && expression.charAt(i) != 'x')
				{
					return String.valueOf(expression.charAt(i));
				}
			}
		}
		int parentheseCount = 0;
		for(int i=0;i<expression.length();i++)
		{
			if(expression.charAt(i) == ')' && parentheseCount == 0) // checks order of parentheses
			{
				errorCase = 2;
				return "";
			}
			if(expression.charAt(i) == '(')
			{
				parentheseCount++;
			}
			else if(expression.charAt(i) == ')')
			{
				parentheseCount--;
			}
		}
		if(parentheseCount != 0) // checks number of parentheses
		{
			errorCase = 3;
		}
		return "";
	}
	
	//errorReport
	//returns the name of the error based on the global variable errorCase
	private static String errorReport(String incorrectChar)
	{
		String error = "";
		switch(errorCase)
		{
		case 1:
			error = "Unidentified Operator: " + incorrectChar;
			break;
		case 2:
			error = "A right parenthese is un-paired";
			break;	
		case 3:
			error = "There is an uneven pair of parentheses";
			break;	
		case 4:
			error = "Please enter an expression.";
			break;	
		case 5:
			error = "Please enter a value for x.";
			break;
		case 6:
			error = "Error while trying to divide by Zero!";
			break;
		case 7:
			error = "Value in expression is too large.";
			break;
		case 8:
			error = "An x value has been specified but expression does not contain x.";
			break;
		}
		return error;
	}
	
	//editVariables changes the variable x to var
	//returns expression without the x variable
	private static String editVariables(String expression, String var)
	{
		if(!var.equals(""))// && var.matches("[0-9,-,.]+"))
		{
			expression = expression.replaceAll("x", var);
		}
		return expression;
	}
	
	//removeSpaces removes any spaces within the expression
	//returns expression without spaces
	private static String removeSpaces(String expression)
	{
		expression = expression.replace(" ", "");
		return expression;
	}
	
	//checkNegatives evaluates the indices of - signs to decide if they are a minus or negative
	//takes in an expression without spaces
	//returns an edited expression with negative values denoted with preface 'n' 
	private static String checkNegatives(String expression)
	{
		int i = 0, j = 0;
		String answer = "";
		while(i != expression.length())
		{
			if(expression.charAt(i) == '+')
			{
				if(i<expression.length() && expression.charAt(i+1) == '-')
				{
					answer = answer + expression.substring(j, i) + "-";
					i++;
					j = i+1;
				}
			}
			else if(expression.charAt(i) == '-')
			{
				if(i>0 && (expression.charAt(i-1) == '('
						|| expression.charAt(i-1) == '^' || expression.charAt(i-1) == 'r' 
						|| expression.charAt(i-1) == '*' || expression.charAt(i-1) == '/'))
				{
					answer = answer + expression.substring(j,i) + "n";
					j = i+1;
				}
				else if(i>0 && expression.charAt(i-1) == 'n')
				{
					answer = answer + expression.substring(j,i) + "n";
					j = i+1;
				}
				else if(i == 0)
				{
					answer = answer + "n";
					j = 1;
				}
				else if(i<expression.length() && expression.charAt(i+1) == '-')
				{
					answer = answer + expression.substring(j, i) + "+";
					i++;
					j = i+1;
				}
			}
			i++;
		}
		answer = answer + expression.substring(j);
		return answer;
	}
	
	//replaceValues replaces the values of pi and e in a given expression
	//returns the edited expression
	private static String replaceValues(String expression)
	{
		expression = expression.replace("pi", String.valueOf(Math.PI));
		expression = expression.replace("e", String.valueOf(Math.E));
		expression = expression.replace("PI", String.valueOf(Math.PI));
		expression = expression.replace("E", String.valueOf(Math.E));
		
		return expression;
	}
	
	//handleParenthesis will take in an expression with known parenthesis
	//and resolve all algebra within the parenthesis
	//returns the string format for the answer
	private static String handleParenthesis(String expression) 
	{
		int start = expression.indexOf("(");
		int end = expression.indexOf(")");
		String newExpression = expression.substring(start+1,end);
		int newStart = 0;
		if(newExpression.contains("("))
		{
			newStart = newExpression.indexOf("(");
		}
		if(newExpression.lastIndexOf("(") >= 0 )
		{
			newStart = newExpression.lastIndexOf("(") + 1;
			start += newStart;
		}
		String answer = processAlgebra(newExpression.substring(newStart));
		String answer2 = expression.substring(0, start) + answer + expression.substring(end+1);
		return answer2;
	}
	
	//processAlgebra takes in a simplified algebra expression without
	//variables and parenthesis
	//computes algebra of the expression
	//returns the result in string format
	private static String processAlgebra(String expression)
	{
		while(!checkAnswer(expression))
		{
			if(expression.contains("^") || expression.contains("r"))
			{
				int operatorSpot, operator = 1;
				if(expression.contains("^") && expression.contains("r"))
				{
					if(expression.indexOf('^') < expression.indexOf('r'))
					{
						operatorSpot = expression.indexOf('^'); //Save spot to divide expression
						operator = 1;
					}
					else
					{
						operatorSpot = expression.indexOf('r'); //Save spot to divide expression
						operator = 0;
					}
				}
				else if(expression.contains("^"))
				{
					operatorSpot = expression.indexOf('^'); //Save spot to divide expression
					operator = 1;
				}
				else
				{
					operatorSpot = expression.indexOf('r'); //Save spot to divide expression
					operator = 0;
				}
				
				String lhs = expression.substring(0,operatorSpot); //divide left hand side
				StringBuilder reverser = new StringBuilder(lhs); //reverse lhs to find first operator
				String reverseLHS = reverser.reverse().toString();
				String rhs = expression.substring(operatorSpot+1,expression.length()); //divide right hand side

				int i;
				for(i= 0; i<reverseLHS.length(); i++) // find spot of first operator
				{
					if((reverseLHS.charAt(i) == '+')
						||reverseLHS.charAt(i) == '-'
						||reverseLHS.charAt(i) == '*'
						||reverseLHS.charAt(i) == '/'
						||reverseLHS.charAt(i) == '^'
						||reverseLHS.charAt(i) == 'r')
					{
						break;
					}
				}
				int concatStart = lhs.length()-i; // Save spot for rebuilding expression at end
				lhs = expression.substring(concatStart,operatorSpot).trim(); // replace lhs with the number adjacent to operator
				for(i= 0; i<rhs.length(); i++) // find spot of first operator
				{
					if((rhs.charAt(i) == '+')
						||rhs.charAt(i) == '-'
						||rhs.charAt(i) == '*'
						||rhs.charAt(i) == '/'
						||rhs.charAt(i) == '^'
						||rhs.charAt(i) == 'r')
					{
						break;
					}
				}
				int concatEnd = operatorSpot + i; // Save spot for rebuilding expression at end
				rhs = rhs.substring(0,i).trim(); // replace rhs with the number adjacent to operator
				if(lhs.contains("n"))
				{
					lhs = lhs.replace('n', '-');
				}
				if(rhs.contains("n"))
				{
					rhs = rhs.replace('n', '-');
				}
				double left = Double.parseDouble(lhs); //convert to doubles
				double right = Double.parseDouble(rhs);
				double result;
				if(operator == 1)
				{
					result = Math.pow(left, right); // compute simple expression
				}
				else
				{
					result = Math.pow(left, 1/right); // compute simple expression
				}
				String answer = String.valueOf(result); // convert to string
				if(answer.charAt(0) == '-')
				{
					answer = "n" + answer.substring(1);
				}
				String empty = ""; //--------------------- Rebuild of expression
				empty = expression.substring(0,concatStart) + answer + expression.substring(concatEnd+1); 
				// add lhs 					  answer			rhs
				expression = empty; // set as new expression to compute
			}
			
			
			else if(expression.contains("*") || expression.contains("/"))
			{
				int operatorSpot, operator = 1;
				if(expression.contains("*") && expression.contains("/"))
				{
					if(expression.indexOf('*') < expression.indexOf('/'))
					{
						operatorSpot = expression.indexOf('*'); //Save spot to divide expression
						operator = 1;
					}
					else
					{
						operatorSpot = expression.indexOf('/'); //Save spot to divide expression
						operator = 0;
					}
				}
				else if(expression.contains("*"))
				{
					operatorSpot = expression.indexOf('*'); //Save spot to divide expression
					operator = 1;
				}
				else
				{
					operatorSpot = expression.indexOf('/'); //Save spot to divide expression
					operator = 0;
				}
				String lhs = expression.substring(0,operatorSpot); //divide left hand side
				StringBuilder reverser = new StringBuilder(lhs); //reverse lhs to find first operator
				String reverseLHS = reverser.reverse().toString();
				String rhs = expression.substring(operatorSpot+1,expression.length()); //divide right hand side

				int i;
				for(i= 0; i<reverseLHS.length(); i++) // find spot of first operator
				{
					if((reverseLHS.charAt(i) == '+')
						||reverseLHS.charAt(i) == '-'
						||reverseLHS.charAt(i) == '*'
						||reverseLHS.charAt(i) == '/')
					{
						break;
					}
				}
				int concatStart = lhs.length()-i; // Save spot for rebuilding expression at end
				lhs = expression.substring(concatStart,operatorSpot).trim(); // replace lhs with the number adjacent to operator
				for(i= 0; i<rhs.length(); i++) // find spot of first operator
				{
					if((rhs.charAt(i) == '+')
						||rhs.charAt(i) == '-'
						||rhs.charAt(i) == '*'
						||rhs.charAt(i) == '/')
					{
						break;
					}
				}
				int concatEnd = operatorSpot + i; // Save spot for rebuilding expression at end
				rhs = rhs.substring(0,i).trim(); // replace rhs with the number adjacent to operator
				if(lhs.contains("n"))
				{
					lhs = lhs.replace('n', '-');
				}
				if(rhs.contains("n"))
				{
					rhs = rhs.replace('n', '-');
				}
				double left = Double.parseDouble(lhs); //convert to doubles
				double right = Double.parseDouble(rhs);
				double result = 0.0;
				if(operator == 1)
				{
					result = left * right; // compute simple expression
				}
				else
				{
					result = left / right; // compute simple expression
				}
				String answer = String.valueOf(result); // convert to string
				if(answer.charAt(0) == '-')
				{
					answer = "n" + answer.substring(1);
				}
				String empty = ""; //--------------------- Rebuild of expression
				empty = expression.substring(0,concatStart) + answer + expression.substring(concatEnd+1); 
				// add lhs 					  answer			rhs
				expression = empty; // set as new expression to compute
			}
			
			else if(expression.contains("+") || expression.contains("-"))
			{
				int operatorSpot, operator = 1;
				if(expression.contains("+") && expression.contains("-"))
				{
					if(expression.indexOf('+') < expression.indexOf('-'))
					{
						operatorSpot = expression.indexOf('+'); //Save spot to divide expression
						operator = 1;
					}
					else
					{
						operatorSpot = expression.indexOf('-'); //Save spot to divide expression
						operator = 0;
					}
				}
				else if(expression.contains("+"))
				{
					operatorSpot = expression.indexOf('+'); //Save spot to divide expression
					operator = 1;
				}
				else
				{
					operatorSpot = expression.indexOf('-'); //Save spot to divide expression
					operator = 0;
				}
			
				String lhs = expression.substring(0,operatorSpot); //divide left hand side
				StringBuilder reverser = new StringBuilder(lhs); //reverse lhs to find first operator
				String reverseLHS = reverser.reverse().toString();
				String rhs = expression.substring(operatorSpot+1,expression.length()); //divide right hand side

				int i;
				for(i= 0; i<reverseLHS.length(); i++) // find spot of first operator
				{
					if((reverseLHS.charAt(i) == '+')
						||reverseLHS.charAt(i) == '-')
					{
						break;
					}
				}
				int concatStart = lhs.length()-i; // Save spot for rebuilding expression at end
				lhs = expression.substring(concatStart,operatorSpot).trim(); // replace lhs with the number adjacent to operator
				for(i= 0; i<rhs.length(); i++) // find spot of first operator
				{
					if((rhs.charAt(i) == '+')
						||rhs.charAt(i) == '-')
					{
						break;
					}
				}
				int concatEnd = operatorSpot + i; // Save spot for rebuilding expression at end
				rhs = rhs.substring(0,i).trim(); // replace rhs with the number adjacent to operator
				if(lhs.contains("n"))
				{
					lhs = lhs.replace('n', '-');
				}
				if(rhs.contains("n"))
				{
					rhs = rhs.replace('n', '-');
				}
				double left = Double.parseDouble(lhs); //convert to doubles
				double right = Double.parseDouble(rhs);
				double result;
				if(operator == 1)
				{
					result = left + right; // compute simple expression
				}
				else
				{
					result = left - right; // compute simple expression
				}
				String answer = String.valueOf(result); // convert to string
				if(answer.charAt(0) == '-')
				{
					answer = "n" + answer.substring(1);
				}
				String empty = ""; //--------------------- Rebuild of expression
				empty = expression.substring(0,concatStart) + answer + expression.substring(concatEnd+1); 
				// add lhs 					  answer			rhs
				expression = empty; // set as new expression to compute
			}
		}
		return expression;
	}
	
	//checkAnswer checks to make sure if expression is only a number
	//returns true if a double
	//returns false if not a double
	private static boolean checkAnswer(String expression)
	{
		if(expression != null && expression.matches("[0-9,.,n]+"))
		{
			return true;
		}
		if(expression.contains("/")) // if divide by zero exit not to cause crash
		{
			int i = expression.indexOf('/');
			if(expression.charAt(i+1) == '0')
			{
				errorCase = 6;
				return true;
			}
		}
		if(expression.contains("E")) // Expression is too large for doubles to hold 
		{							 // we must return not to crash
			errorCase = 7;
			return true;
		}
		return false;
	}
}
