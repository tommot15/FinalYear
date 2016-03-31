import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;

//WHEN SCANNING THE CODE, ASSIGN A LINE NUMBER WHICH CAN BE USED IN THE PARSER
//TO HIGHLIGHT THE TEXT IN THE TEXT AREA

public class Parser {
	String className;
	String code;
	int curr = 0;
	List<TokenNode> tnArray;
	
	public Parser(String c, JTextArea j){
		tnArray = new ArrayList<TokenNode>(); //Stores each Line into a list
		code = c;
		j.append("HELLO WORLD");
		String text = j.getText();
		
		DefaultHighlighter.DefaultHighlightPainter hl = 
		        new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
		String pattern = "Main{";
		int index = text.indexOf(pattern);
		    try {                
		        j.getHighlighter().addHighlight(0, index + pattern.length(), DefaultHighlighter.DefaultPainter);
		        index = text.indexOf(pattern, index + pattern.length());            
		    } catch (BadLocationException ex) {
		        ex.printStackTrace();
		    }//Code to highlight a specific line
		    //figure out a way to keep up with the line the token being parsed is on
		    
		ScanText s = new ScanText();
		tnArray = s.scanLine(code); //Splits the code into TokenNodes
		if(isBlock()){
			System.out.println("IT IS A block");
		}
		else{
			System.out.println("ITS NOT A block");
		}
	}

	public boolean isBlock(){
		
		boolean left = isClass();
		className = tnArray.get(curr-1).getVal();
		boolean mid = isLeftBrace();
		boolean constructor = isConstructor();
		boolean right = isRightBrace();
		
		if(left && mid && constructor && right)
		{
			return true;
		}
		
		return false;
		
	}
	
	public boolean isConstructor(){
		
		boolean isClass = isClass();
		//System.out.println("Class : " + isClass);
		boolean leftParan = isLeftParan();
		
		while(isArg()){
			
		}
		
		boolean rightParan = isRightParan();
		boolean leftBrace = isLeftBrace();
		
		while(isStatement()){
			
		}
		boolean rightBrace = isRightBrace();
		
		if(isClass && leftParan && rightParan && leftBrace && rightBrace){
			return true;
		}
		return false;
	}

	public boolean isStatement() {
		boolean left = isVar();
		boolean mid = isEqual();
		boolean right = isExpression();
		boolean semiCol = isSemiColon();
		
		if(left && mid && right && semiCol)
		{
			return true;
		}
		else if(left && semiCol)
		{
			return true;
		}
		else if(left && !semiCol){
			System.out.println("NO SEMI COLON");
		}
		
		return false;
	}
	
	public boolean isExpression() {
		
		boolean left = isVar();
		boolean mid = isOp();
		boolean right = isVar();
		
		if(left && mid && right)
		{
			return true;
		}
		return false;
	}
	
	public boolean isClass(){
		
		if(tnArray.get(curr).getType().equals("class")){
			curr++;
			System.out.println("Class is true");
			return true;
		}
		System.out.println("Type found: " + tnArray.get(curr).getType());
		return false;
	}
	
	public boolean isVar(){
		
		if(tnArray.get(curr).getType().equals("identifier"))
		{
			curr++;
			return true;
		}
		return false;
	}
	
	public boolean isEqual(){
		if(tnArray.get(curr).getType().equals("equal"))
		{
			curr++;
			return true;
		}
		return false;
	}
	
	public boolean isOp(){
		if(tnArray.get(curr).getType().equals("op")){
			curr++;
			return true;
		}
		return false;
	}
	
	public boolean isSemiColon(){
		if(tnArray.get(curr).getType().equals("eol")){
			curr++;
			return true;
		}
		return false;
	}
	
	public boolean isLeftParan(){
		
		if(tnArray.get(curr).getType().equals("leftParan")){
			curr++;
			return true;
		}
		return false;
	}

	public boolean isRightParan(){
		if(tnArray.get(curr).getType().equals("rightParan")){
			curr++;
			System.out.println("RIGHT PARAN IS TRUE");
			return true;
		}
		return false;
	}
	
	public boolean isLeftBrace(){
		if(tnArray.get(curr).getType().equals("leftBrace")){
			curr++;
			System.out.println("LEFT BRACE IS TRUE");
			return true;
		}
		return false;
	}

	public boolean isRightBrace(){
		if(tnArray.get(curr).getType().equals("rightBrace")){
			curr++;
			System.out.println("RIGHT BRACE IS TRUE");
			return true;
		}
		return false;
	}
	
	public boolean isArg(){
		boolean data = isDataType();
		boolean name = isVar();
		return false;
	}
	
	public boolean isDataType(){
		if(tnArray.get(curr).getType().equals("dataType")){
			curr++;
			return true;
		}
		return false;
	}
}