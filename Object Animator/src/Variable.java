
public class Variable{
	private String dataType;
	private String name;
	private String value;
	
	public Variable(String d, String n){
		this.dataType = d;
		this.name = n;
		this.value = "empty";
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
	
	public void setValue(String v){
		this.value = v;
	}
	
	public void printVar(){
		System.out.println("DataType: " + this.dataType + "\nName: " + this.name + "\nValue: " + this.value + "\n");
	}
}
