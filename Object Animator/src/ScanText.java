import java.util.ArrayList;
import java.util.List;

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
		String[] lines = l.split("\n");
		String[] tokens;
		
		//Delim holds the regex to split up the line by specific characters
		String delim = "\\s|(?<=\\{)|(?=\\{)|(?<=\\})|(?=\\})|(?<=;)|(?=;)|(?<=\\()|(?=\\()|(?<=\\))|(?=\\)|(?<=,)|(?=,)|(?<=\")|(?=\"))";
		
		//Splits the line up by the delim and adds them to a token array
		for(int i = 0; i < lines.length; i++)
		{
			tokens = lines[i].trim().split(delim);
			for(String word : tokens){
				if(word.equals("")){
					
				}else{
					//Calls matchToken to create a Token and adds it to tnArray
					tnArray.add(matchToken(word, i));
				}
			}
		}
		return tnArray;
	}
	
	public TokenNode matchToken(String s1, int l){
		TokenNode token = new TokenNode();
		
		
		//Check which format the string matches and create a TokenNode
		
		if(s1.equals("new")){
			token = new TokenNode(s1, "new", l);
		}
		else if(s1.equals("class")){
			token = new TokenNode(s1, "classDec", l);
		}
		else if(s1.equals("=")){
			token = new TokenNode(s1, "equal", l);
		}
		else if(s1.equals("{")){
			token = new TokenNode(s1, "leftBrace", l);
		}
		else if(s1.equals("}")){
			token = new TokenNode(s1, "rightBrace", l);
		}
		else if(s1.equals("+")){
			token = new TokenNode(s1, "op", l);
		}
		else if(s1.equals(";")){
			token = new TokenNode(s1, "eol", l);
		}
		else if(s1.equals("(")){
			token = new TokenNode(s1, "leftParan", l);
		}
		else if(s1.equals(")")){
			token = new TokenNode(s1, "rightParan", l);
		}
		else if(s1.equals("String") || s1.equals("int")){
			token = new TokenNode(s1, "dataType", l);
		}
		else if(s1.matches("[a-z][a-zA-Z]+")){
			token = new TokenNode(s1, "identifier", l);
		}
		else if(s1.matches("[A-Z][a-zA-Z]+")){
			token = new TokenNode(s1, "class", l);
		}
		else if(s1.matches("[0-9]+")){
			token = new TokenNode(s1, "num", l);
		}
		else if(s1.equals(",")){
			token = new TokenNode(s1, "comma", l);
		}
		else if(s1.equals("\"")){
			token = new TokenNode(s1, "quote", l);
		}
		
		return token;
	}
}
