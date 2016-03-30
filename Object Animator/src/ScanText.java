import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ScanText {
	String line;
	List<TokenNode> tnArray;
	String[] keywords;
	String s1;
	int count;

	public ScanText() {
		tnArray = new ArrayList<TokenNode>();
		keywords = new String[]{"String", "Int"};
	}

	public List<TokenNode> scanLine(String l) throws NullPointerException{
		line = l;
		System.out.println(line);
		Scanner s = new Scanner(line);
		count = 0;
		
		while(s.hasNext()){
			
			s1 = s.next(); 
			
			//take out unwanted characters from the string
			s1 = s1.replaceAll("[\n]", " ");
			s1 = s1.replaceAll("[\t]", "" );
			
			System.out.println("S1 = \"" + s1 + "\"");
			
			if(s1.matches("[A-Z][a-zA-Z]+")){//check which format the string matches and create a TokenNode
				tnArray.add(new TokenNode("class", s1));
			}
			else if(s1.matches("[a-z][a-zA-Z]+")){
				tnArray.add(new TokenNode("identifier", s1));
			}
			else if(s1.equals("=")){
				tnArray.add(new TokenNode("equal", s1));
			}
			else if(s1.equals("{")){
				tnArray.add(new TokenNode("openBrace", s1));
			}
			else if(s1.equals("}")){
				tnArray.add(new TokenNode("closeBrace", s1));
			}
			else if(s1.equals("+")){
				tnArray.add(new TokenNode("op", s1));
			}
			else if(s1.equals(";")){
				tnArray.add(new TokenNode("eol", s1));
			}
		}
		s.close();
		return tnArray;
	}
}
