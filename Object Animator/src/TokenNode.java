
public class TokenNode {
	private String type;
	private String value;
	
	public TokenNode(){}
	
	public TokenNode(String v, String t){
		type = t;
		value = v;
		
	}
	
	public String getType(){
		
		return type;
	}
	
	public String getVal(){
		return value;
	}
}
