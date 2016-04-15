import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

//WHEN SCANNING THE CODE, ASSIGN A LINE NUMBER WHICH CAN BE USED IN THE PARSER
//TO HIGHLIGHT THE TEXT IN THE TEXT AREA

public class Parser{
	String name, className, targetClass, code, text, cName;
	int curr = 0;
	boolean constructor, correctClass;
	List<TokenNode> tnArray;
	List<Object> params = new ArrayList<Object>();
	List<Object> tmpParam = new ArrayList<Object>();
	List<Object> updatedParams = new ArrayList<Object>();
	List<Object> vars = new ArrayList<Object>();
	String[] lines;
	DefaultHighlighter.DefaultHighlightPainter hl;
	int startIndex, endIndex;
	JTextArea main, other;
	PrintObjects panel;
	PrintObjects po;
	int count = 0;
	Rectangle r;
	
	public Parser(JTextArea m, JTextArea o, PrintObjects print){
		tnArray = new ArrayList<TokenNode>(); //Stores each Line into a list
		code = m.getText();
		text = m.getText();
		lines = m.getText().split("\n");
		main = m;
		other = o;
		targetClass = "Main";
		panel = print;
		hl = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
		po = print;
		
		ScanText s = new ScanText();
		tnArray = s.scanLine(code); //Splits the code into TokenNodes
		if(isBlock()){
			System.out.println("IT IS A BLOCK");
		}
		else{
			System.out.println("ITS NOT A block");
		}
		
		for(Object p : vars){
			if(p instanceof Parser){
				Parser par = (Parser) p;
				System.out.println("Object Name = " + par.getName());
				for(Object v : par.getVars()){
					if(v instanceof Variable){
						Variable thisVar = (Variable)v;
						System.out.println("\t DataType = " + thisVar.getDataType() + " Name = " + thisVar.getName() + " Value = " + thisVar.getValue());

					}
				}
			}
			if(p instanceof Variable){
				Variable v = (Variable) p;
				System.out.println("====== Var name = " + v.getName() + " Var val = " + v.getValue());
			}
		}
	}
	
	public Parser(JTextArea o, String n, List<Object> p, String c){
		tmpParam = p;
		name = n;
		tnArray = new ArrayList<TokenNode>(); //Stores each Line into a list
		code = o.getText();
		text = o.getText();
		lines = o.getText().split("\n");
		other = o;
		hl = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
		ScanText s = new ScanText();
		tnArray = s.scanLine(code); //Splits the code into TokenNodes
		
		targetClass = c;
		while(!correctClass && tnArray.get(curr) != null){
			isBlock();
			curr++;
		}
		
		for(Object obj : tmpParam)
		{
			if(obj instanceof Parser){
				Parser ob = (Parser) obj;
			}
			else{
				Variable v = (Variable) obj;
			}
		}
	}
	
	public boolean isConstOrIsVar(){
		boolean isVar, isConst;
		if(tnArray.get(curr).getType().equals("rightBrace") && constructor){
			return true;
		}
		else if(tnArray.get(curr).getType().equals("dataType")){
			isVar = isNewVar();
			if(isVar){
				isConstOrIsVar();
				return true;
			}
		}
		else if(tnArray.get(curr).getType().equals("class") && !constructor){
			isConst = isConstructor();
			if(isConst){
				constructor = true;
				isConstOrIsVar();
			}
			return true;
		}
		
		return false;
	}

	public boolean isBlock(){
		boolean left, mid, varOrConst, right;
		className = tnArray.get(curr).getVal();
		System.out.println("Class NAME = " + className);
		if(className.equals(targetClass) && tnArray.get(curr + 1).getVal().equals("{")){
			correctClass = true;
			cName = className;
			left = isClass();
			mid = isLeftBrace();
			varOrConst = isConstOrIsVar();
			right = isRightBrace();
			
			if(left && mid && varOrConst && right)
			{
				return true;
			}
		}
		return false;	
	}
	
	public boolean isConstructor(){
		
		boolean isClass, leftParan, args, rightParan, leftBrace, statements, rightBrace;
		if(className.equals(tnArray.get(curr).getVal())){
			
			isClass = isClass();
			leftParan = isLeftParan();
			args = isArg(0);
			for(Object o : updatedParams)
			{
				if(o instanceof Parser){
					Parser p = (Parser) o;
					System.out.println("UPDATED PARAM NAME(TYPE PARSER): " + p.getName());
				}
			}
			rightParan = isRightParan();
			leftBrace = isLeftBrace();
			statements = isVarOrStmt();
			rightBrace = isRightBrace();
		
			System.out.println("isClass = " + isClass + " leftParan = " + leftParan + " isArg=" + args + " rightParan=" + rightParan +
					" leftBrace=" + leftBrace + " statements=" + statements + " rightBrace=" + rightBrace);
		
			if(isClass && leftParan && args && rightParan && leftBrace && statements && rightBrace){
				return true;
			}else{
				System.out.println("CONSTRUCTOR SYNTAX INCORRECT");
				return false;
			}
		}
		else{
			System.out.println("Class name and constructor name aren't the same");
		}
		return false;
	}
	
	public boolean isVarOrStmt(){
		boolean newVar;
		boolean statement;
		boolean init;
		if(tnArray.get(curr).getType().equals("rightBrace")){
			return true;
		}
		else if(tnArray.get(curr).getType().equals("dataType")){
			newVar = isNewVar();
			if(newVar){
				isVarOrStmt();
				return true;
			}
		}
		else if(tnArray.get(curr).getType().equals("identifier")){
			statement = isStatement();
			if(statement){
				isVarOrStmt();
				return true;
			}
		}
		else if(tnArray.get(curr).getType().equals("class")){
			init = isInit();
			System.out.println("INIT VAL = " + init);
			
			if(init){
				isVarOrStmt();
				return true;
			}
		}
		return false;
	}
	
	public boolean isInit(){
		
		Parser p;
		boolean newClass, name, equal, newKey, newClass2, leftParan, isParam, rightParan, semiCol;
		String thisName, className, constClass;
		params.clear();
		className = tnArray.get(curr).getVal();
		newClass = isClass();
		thisName = tnArray.get(curr).getVal();
		name = isVar();
		equal = isEqual();
		newKey = isNewKeyword();
		constClass = tnArray.get(curr).getVal();
		newClass2 = isClass();
		leftParan = isLeftParan();
		isParam = isParam();
		
		rightParan = isRightParan();
		semiCol = isSemiColon();
		//System.out.println(newClass + " " + name + " " + equal + " " + newKey + " " + newClass2 + " " + leftParan + " " + isParam + " " + rightParan + " " + semiCol);
		if(className.equals(constClass)){
			if(newClass && name && equal && newKey && newClass2 && leftParan && isParam && rightParan && semiCol){
				for(Object v : vars){
					if(v instanceof Parser){
						Parser var = (Parser) v;
						if(var.getName().equals(thisName)){
							System.out.println("Object Already Exists");
							return false;
						}
					}
					else if(v instanceof Variable){
						Variable var = (Variable) v;
						if(var.getName().equals(thisName)){
							System.out.println("Object Already Exists");
							return false;
						}
					}
				}
				p = new Parser(other, thisName, params, className);
				vars.add(p);
				
				r = new Rectangle();
				if(this.cName.equals("Main")){
					panel.objects.add(p);
					panel.repaint();
				}
				return true;
			}
			else{
				System.out.println("Syntax for object creation isn't correct");
				return false;
			}
		}else{
			System.out.println("Class names in initialization aren't the same");
		}
		return false;
	}
	public boolean isNewVar(){
		Variable newVar;
		String dataType = "";
		String getName = "";
		boolean data, name, semiCol;
		
		dataType = tnArray.get(curr).getVal();
		data = isDataType();
		getName = tnArray.get(curr).getVal();
		name = isVar();
		semiCol = isSemiColon();
		
		if(data && name && semiCol){
			for(Object v : vars){
				if(v instanceof Variable){
					Variable var = (Variable) v;
					if(var.getName().equals(getName)){
						System.out.println("VAR ALREADY EXISTS");
						return false;
					}
				}
			}
			newVar = new Variable(dataType, getName);
			vars.add(newVar);
			return true;
		}
		return false;
	}
	public boolean isStatement() {
		String getName;
		String newVal;
		getName = tnArray.get(curr).getVal();
		boolean left, mid, right, leftQuote, isVar, rightQuote, semiCol;
		left = isVar();
		mid = isEqual();
		
		System.out.println("left = " + left + " mid = " + mid);
		if(tnArray.get(curr).getType().equals("quote"))
		{
			for(Object v : vars){
				if(v instanceof Variable){
					Variable var = (Variable)v;
					if(var.getName().equals(getName) && var.getDataType().equals("String")){
						leftQuote = isQuote();
						newVal = tnArray.get(curr).getVal();
						isVar = isVar();
						rightQuote = isQuote();
						semiCol = isSemiColon();
					
						if(left && mid && leftQuote && isVar && rightQuote && semiCol){
							var.setValue(newVal);
							return true;
						}
					}
				}
			}
			System.out.println("NOT A VARIABLE");
			return false;
		}
		else if(tnArray.get(curr).getType().equals("identifier")){
			for(Object v : vars){
				if(v instanceof Variable){
					Variable var = (Variable) v;
					if(var.getName().equals(getName) && var.getDataType().equals("String")){
						newVal = tnArray.get(curr).getVal();
						isVar = isVar();
						semiCol = isSemiColon();
						if(isVar){
							for(Object w : vars){
								if(w instanceof Variable){
									Variable otherVar = (Variable)w;
									if(otherVar.getName().equals(newVal) && otherVar.getDataType().equals("String")){
										var.setValue(otherVar.getValue());
										return true;
									}
								}
							}
							for(Object w : updatedParams){
								if(w instanceof Variable){
									Variable thisVar = (Variable)w;
									if(thisVar.getName().equals(newVal) && thisVar.getDataType().equals("String")){
										var.setValue(thisVar.getValue());
										
										return true;
									}									
								}
							}
							return false;
						}
					}
					else if(var.getName().equals(getName) && var.getDataType().equals("int")){
						newVal = tnArray.get(curr).getVal();
						isVar = isVar();
						semiCol = isSemiColon();
						if(isVar){
							for(Object w : vars){
								if(w instanceof Variable){
									Variable otherVar = (Variable)w;
									if(otherVar.getName().equals(newVal) && otherVar.getDataType().equals("int")){
										var.setValue(otherVar.getValue());
										return true;
									}
								}
							}
							for(Object w : updatedParams){
								if(w instanceof Variable){
									Variable thisVar = (Variable)w;
									System.out.println("NAME: " + thisVar.getName() + " NEW VAR NAME: " + newVal);
									if(thisVar.getName().equals(newVal) && thisVar.getDataType().equals("int")){
										System.out.println("HELLO I AM HERE");
										var.setValue(thisVar.getValue());
										
										return true;
									}									
								}
							}
							return false;
						}
					}
				}
				else if(v instanceof Parser){
				Parser var = (Parser) v;
				if(var.getName().equals(getName)){
					newVal = tnArray.get(curr).getVal();
					isVar = isVar();
					semiCol = isSemiColon();
					if(isVar){
						for(Object w : vars){
							if(w instanceof Parser){
								Parser otherVar = (Parser)w;
								if(otherVar.getName().equals(newVal) && otherVar.className.equals(var.className)){
									var = otherVar;
									return true;
								}
							}
						}
						for(Object w : updatedParams){
							if(w instanceof Parser){
								Parser thisVar = (Parser)w;
								System.out.println("NAME: " + thisVar.getName() + " NEW VAR NAME: " + newVal);
								if(thisVar.getName().equals(newVal) && thisVar.className.equals(var.className)){
									System.out.println("HELLO I AM HERE");
									var = thisVar;
									
									return true;
								}									
							}
						}
						return false;
					}
				}
			}
			}
			return false;
		}
		else if(tnArray.get(curr).getType().equals("num")){
			for(Object v : vars){
				if(v instanceof Variable){	
					Variable var = (Variable)v;
					if(var.getName().equals(getName) && var.getDataType().equals("int")){
					
						newVal = tnArray.get(curr).getVal();
						right = isInt();
						semiCol = isSemiColon();
					
						System.out.println("Right = " + right + " SemiCol = " + semiCol);
					
						if(left && mid && right && semiCol){
							var.setValue(newVal);
							return true;
						}
					}
				}
			}
			System.out.println("WRONG VARIABLE OR TYPE");
			return false;
		}
		System.out.println("NOT A STATEMENT");
		return false;
	}
	public boolean isExpression() {
		boolean left;
		boolean mid;
		boolean right;
		
		if(tnArray.get(curr+1).getType().equals("eol"))
		{
			left = isInt();
			if(left){
				return true;
			}
		}
		else if(tnArray.get(curr+1).getType().equals("op")){
			left = isInt();
			mid = isOp();
			right = isExpression();
			if(left && mid && right){
				return true;
			}
		}
		System.out.println("WHY AM I HERE");
		return false;
	}
	
	public boolean isClass(){
		highlight();
		if(tnArray.get(curr).getType().equals("class")){
			curr++;
			removeHighlight();
			return true;
		}
		System.out.println("Type found: " + tnArray.get(curr).getType());
		return false;
	}
	
	public boolean isVar(){
		highlight();
		if(tnArray.get(curr).getType().equals("identifier"))
		{
			curr++;
			removeHighlight();
			return true;
		}
		System.out.println("NOT A VAR");
		return false;
	}
	
	public boolean isEqual(){
		highlight();
		if(tnArray.get(curr).getType().equals("equal"))
		{
			curr++;
			removeHighlight();
			return true;
		}
		System.out.println("NOT AN EQUAL");
		return false;
	}
	
	public boolean isOp(){
		highlight();
		if(tnArray.get(curr).getType().equals("op")){
			curr++;
			removeHighlight();
			return true;
		}
		System.out.println("NOT AN OP");
		return false;
	}
	
	public boolean isSemiColon(){
		highlight();
		if(tnArray.get(curr).getType().equals("eol")){
			curr++;
			removeHighlight();
			return true;
		}
		System.out.println("NOT A SEMICOLON");
		return false;
	}
	
	public boolean isLeftParan(){
		highlight();
		if(tnArray.get(curr).getType().equals("leftParan")){
			curr++;
			removeHighlight();
			return true;
		}
		System.out.println("NOT A LEFT PARAN");
		return false;
	}

	public boolean isRightParan(){
		
		highlight();
		if(tnArray.get(curr).getType().equals("rightParan")){
			curr++;
			removeHighlight();
			return true;
		}
		System.out.println("NOT A RIGHT PARAN");
		return false;
	}
	
	public boolean isLeftBrace(){
		highlight();
		if(tnArray.get(curr).getType().equals("leftBrace")){
			curr++;
			removeHighlight();
			return true;
		}
		System.out.println("NOT A LEFT BRACE = " + tnArray.get(curr).getVal());
		return false;
	}

	public boolean isRightBrace(){
		highlight();
		if(tnArray.get(curr).getType().equals("rightBrace")){
			curr++;
			removeHighlight();
			return true;
		}
		System.out.println("Type = " + tnArray.get(curr).getType());
		System.out.println("VAL = " + tnArray.get(curr).getVal());
		System.out.println("NOT A RIGHT BRACE");
		return false;
	}
	
	public boolean isArg(int i){
		highlight();
		boolean data, name, thisClass;
		String thisDataType;
		String thisName;
		int index;
		index = i;
		
		if(tnArray.get(curr).getType().equals("rightParan")){
			return true;
		}
		else if(tnArray.get(curr).getType().equals("dataType")){
			if(tmpParam.get(index) instanceof Variable)
			{
				Variable var = (Variable)tmpParam.get(index);
				
				thisDataType = tnArray.get(curr).getVal();
				data = isDataType();
				thisName = tnArray.get(curr).getVal();
				name = isVar();
				
				if(tmpParam == null || tmpParam.isEmpty()){
					System.out.println("NO PARAMETERS");
					return false;
				}
				else if(data && name && var.getDataType().equals(thisDataType)){
					var.setName(thisName);
					updatedParams.add(var);
					isArg(index + 1);
					return true;
				}
				else{
					System.out.println("HI IM HERE ARGUMENTS AND PARAMS DON'T MATCH");
				}
			}
		}
		else if(tnArray.get(curr).getType().equals("comma")){
			curr++;
			if(isArg(index)){
				return true;
			}
			else{
				return false;
			}
		}
		else if(tnArray.get(curr).getType().equals("class")){
			if(tmpParam.get(index) instanceof Parser){
				Parser obj = (Parser) tmpParam.get(index);
				thisDataType = tnArray.get(curr).getVal();
				thisClass = isClass();
				thisName = tnArray.get(curr).getVal();
				System.out.println("THIS NAME = " + thisName);
				name = isVar();

				if(tmpParam == null || tmpParam.isEmpty()){
					System.out.println("No parameters");
					return false;
				}
				else if(thisClass && name && obj.className.equals(thisDataType)){
					obj.setName(thisName);
					updatedParams.add(obj);
					isArg(index + 1);
					return true;
				}
				else{
					System.out.println("ARGUMENTS AND PARAMS DON'T MATCH");
				}
			}
		}
		System.out.println("NOT AN ARG");
		return false;
	}
	
	public boolean isParam(){
		highlight();
		boolean name;
		String getName;
		
		if(tnArray.get(curr).getType().equals("rightParan")){
			return true;
		}
		else if(tnArray.get(curr).getType().equals("identifier"))
		{
			getName = tnArray.get(curr).getVal();
			name = isVar();
			for(Object v : vars){
				if(v instanceof Variable){
					Variable var = (Variable)v;
					if(var.getName().equals(getName) && name){ //checks whether the variable exists to pass through
						params.add(var);
						if(isParam()){
							return true;
						}
						else{
							return false;
						}
					}
				}
				else if(v instanceof Parser){
					Parser var = (Parser)v;
					
					if(var.getName().equals(getName) && name){
						params.add(var);
						if(isParam()){
							
							return true;
						}
						else{
							System.out.println("PARSER IS FALSE");
							return false;
						}
					}
					
				}
			}
			System.out.println("Variable doesn't exist");
		}
		else if(tnArray.get(curr).getType().equals("comma")){
			System.out.println("HELLO THERE COMMA");
			curr++;
			if(isParam()){
				return true;
			}
		}
		System.out.println("WHAT IS WRONG : " + tnArray.get(curr).getVal());
		System.out.println("Unknown parameter");
		return false;
	}
	
	public boolean isDataType(){
		highlight();
		if(tnArray.get(curr).getType().equals("dataType")){
			curr++;
			removeHighlight();
			return true;
		}
		return false;
	}
	
	public boolean isNewKeyword(){
		highlight();
		if(tnArray.get(curr).getType().equals("new")){
			curr++;
			removeHighlight();
			return true;
		}
		return false;
	}
	
	public boolean isQuote(){
		highlight();
		if(tnArray.get(curr).getType().equals("quote")){
			curr++;
			removeHighlight();
			return true;
		}
		return false;
	}
	
	public boolean isInt(){
		highlight();
		if(tnArray.get(curr).getType().equals("num")){
			curr++;
			removeHighlight();
			return true;
		}
		return false;
	}
	
	public void highlight(){
		/*try {                
			startIndex = main.getLineStartOffset(tnArray.get(curr).getLine());
			endIndex = main.getLineEndOffset(tnArray.get(curr).getLine());
	        main.getHighlighter().addHighlight(startIndex, endIndex, hl);
	    } catch (BadLocationException ex) {
	        ex.printStackTrace();
	    }*/
	}
	
	public void removeHighlight(){
		//main.getHighlighter().removeAllHighlights();
		
	}
	
	public String getName(){
		return name;
	}
	public List<Object> getVars(){
		return vars;
	}
	public void setName(String n){
		name = n;
	}
}