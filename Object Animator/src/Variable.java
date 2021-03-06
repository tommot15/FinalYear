//Variable class for use in the Parser class
//Stores a variables dataType, name and value
public class Variable{
	private String dataType;
	private String name;
	private String value;
	
	public Variable(String d, String n){
		this.dataType = d;
		this.name = n;
		this.value = "";
	}
	public Variable(){
	}
	
	public String getDataType(){
		return this.dataType;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getValue(){
		return this.value;
	}
	
	public void setDataType(String d){
		this.dataType = d;
	}
	public void setName(String n){
		this.name = n;		
	}
	
	public void setValue(String v){
		this.value = v;
	}
	
}
