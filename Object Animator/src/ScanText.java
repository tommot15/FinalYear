import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ScanText {

	List<TokenNode> tnArray;
	String[] dataTypes;
	String s1;
	int count;

	public ScanText() {
		tnArray = new ArrayList<TokenNode>();
		dataTypes = new String []{"String", "int"};
	}

	public List<TokenNode> scanLine(String l) throws NullPointerException{
		String line = l.trim();
		String delim = "\\s|(?<=\\{)|(?=\\{)|(?<=\\})|(?=\\})|(?<=;)|(?=;)|(?<=\\()|(?=\\()|(?<=\\))|(?=\\))";
		String[] withoutDelim = line.split(delim);
		
		for(int i = 0; i < withoutDelim.length; i++){
			if(withoutDelim[i].equals("")){
				
			}else{
				//System.out.println(i + ": \"" + withoutDelim[i] + "\"");
				tnArray.add(matchToken(withoutDelim[i]));
			}
		}
		return tnArray;
	}
	
	public TokenNode matchToken(String s1){
		TokenNode token = new TokenNode();
		System.out.println("S1 = " + s1);
		if(s1.matches("[A-Z][a-zA-Z]+")){//check which format the string matches and create a TokenNode
			token = new TokenNode(s1, "class");
		}
		else if(s1.matches("[a-z][a-zA-Z]+")){
			token = new TokenNode(s1, "identifier");
		}
		else if(s1.equals("=")){
			token = new TokenNode(s1, "equal");
		}
		else if(s1.equals("{")){
			token = new TokenNode(s1, "leftBrace");
		}
		else if(s1.equals("}")){
			token = new TokenNode(s1, "rightBrace");
		}
		else if(s1.equals("+")){
			token = new TokenNode(s1, "op");
		}
		else if(s1.equals(";")){
			token = new TokenNode(s1, "eol");
		}
		else if(s1.equals("(")){
			token = new TokenNode(s1, "leftParan");
		}
		else if(s1.equals(")")){
			token = new TokenNode(s1, "rightParan");
		}
		else if(Arrays.asList(dataTypes).contains(s1)){
			token = new TokenNode(s1, "dataType");
		}
		
		return token;
	}
}
