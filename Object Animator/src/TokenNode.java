
public class TokenNode {
	private String type;
	private String value;
	private int line;
	public TokenNode(){}
	
	public TokenNode(String v, String t, int l){
		type = t;
		value = v;
		line = l;
	}
	
	public String getType(){
		
		return type;
	}
	
	public String getVal(){
		return value;
	}
	
	public int getLine(){
		return line;
	}
}
