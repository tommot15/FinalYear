import java.util.ArrayList;
import java.util.List;

public class Parser {

	String code;
	int curr = 0;
	List<TokenNode> tnArray;
	
	public Parser(String c) {
		tnArray = new ArrayList<TokenNode>(); //Stores each Line into a list
		code = c;
		
		ScanText s = new ScanText();
		tnArray = s.scanLine(code); //Splits the code into TokenNodes
		if(isBlock())
		{
			System.out.println("IT IS A block");
		}else{
			System.out.println("ITS NOT A block");
		}
		
	}

	public boolean isBlock(){
		
		boolean left = isClass();
		boolean mid = tnArray.get(curr).getType().equals("openBrace");
		curr++;
		while(isStatement())
		{
			System.out.println("THIS IS TRUE: ");
		}
		boolean right = tnArray.get(curr).getType().equals("closeBrace");
		
		if(left && mid && right)
		{
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
			return true;
		}
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
}