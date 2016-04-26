import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

//WHEN SCANNING THE CODE, ASSIGN A LINE NUMBER WHICH CAN BE USED IN THE PARSER
//TO HIGHLIGHT THE TEXT IN THE TEXT AREA

public class Parser{
	String name, className, targetClass, code, text, cName;
	int curr = 0;
	boolean constructor, correctClass, hasHighlighted, finished;
	List<TokenNode> tnArray;
	List<Object> params = new ArrayList<Object>();
	List<Object> tmpParam = new ArrayList<Object>();
	List<Object> updatedParams = new ArrayList<Object>();
	List<Object> vars = new ArrayList<Object>();
	List<HighlightLine> highlighters = new ArrayList<HighlightLine>();
	String[] lines;
	Animator a;
	int c, lineNo;
	boolean painted = false;
	boolean shownError;
	int startIndex, endIndex;
	JTextArea main, other;
	int count;
	Rectangle r;
	
	//Constructor for the main method text area
	public Parser(JTextArea m, JTextArea o, PrintObjects print){
		tnArray = new ArrayList<TokenNode>(); //Stores each Line into a list
		code = m.getText(); //Gets the code from the left text area
		lines = m.getText().split("\n"); //Splits the code to be tokenized line by line
		main = m; 
		other = o;
		targetClass = "Main"; //sets the target class to Main
		
		shownError = false; //An error has not been found
		
		ScanText s = new ScanText(); //Scans the text
		tnArray = s.scanLine(code); //Splits the code into TokenNodes
		count = 0;
		a = new Animator(main, other, highlighters, print); //Creates a new Animator class
		
		try{
			if(!isBlock()){ //Calls isBlock and checks the code
				if(shownError == false){ //Pops up with a dialog box informing of an error
					JOptionPane.showMessageDialog(null, "Error in the code, check that all the braces are paired, or\n that parameters are complete");
					shownError = true;
				}
			}
		}
		catch(IndexOutOfBoundsException ie){
			if(shownError == false){ //catches an index out of bounds exception error for when braces aren't paired
				JOptionPane.showMessageDialog(null, "Error in the code, check that all the braces are paired, or\n that parameters are complete");
				shownError = true;
			}
		}
		//If there are no errors, the animation is started
		if(shownError!=true){ 
			a.highlight();
		}		
	}
	
	//Constructor for the animation text area
	public Parser(JTextArea o, String n, List<Object> p, String c, Animator e, int l, boolean error){ 
		tmpParam = p;
		name = n;
		tnArray = new ArrayList<TokenNode>(); //Stores each Line into a list
		code = o.getText(); //Gets the text from the animator text area
		lines = o.getText().split("\n");
		other = o;
		ScanText s = new ScanText();
		tnArray = s.scanLine(code); //Splits the code into TokenNodes
		shownError = error;
		a = e;
		targetClass = c;
		lineNo = l;
		
		while(!correctClass && tnArray.get(curr) != null){ //scans code until the correct class is found
			isBlock();
			curr++;
		}
	}
	
	//Recursively checks if the code is a constructor or a new variable
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

	//Checks if the code is a class block
	public boolean isBlock(){
		boolean classDec, thisClass, leftBrace, varOrConst, rightBrace;
		
		className = tnArray.get(curr+1).getVal();
		
		//Checks if the class is correct and if the declaration has a right brace
		if(className.equals(targetClass) && tnArray.get(curr + 2).getVal().equals("{")){
			
			correctClass = true; //Found the class
			cName = className; //Sets the class name of the Parser
			
			//Checks whether the class has the correct structure
			classDec = isClassDec();
			thisClass = isClass();
			leftBrace = isLeftBrace();
			varOrConst = isConstOrIsVar();
			rightBrace = isRightBrace();
			
			//Returns true if it's the correct structure
			if(classDec && thisClass && leftBrace && varOrConst && rightBrace){
				return true;
			}
		}
		return false;	
	}
	
	public boolean isConstructor(){
		
		boolean isClass, leftParan, args, rightParan, leftBrace, statements, rightBrace;
		
		//Checks if the class name is the same as the constructor name
		if(className.equals(tnArray.get(curr).getVal())){
			
			//Checks whether the constructor has the right structure
			isClass = isClass();
			leftParan = isLeftParan();
			args = isArg(0);
			rightParan = isRightParan();
			leftBrace = isLeftBrace();
			statements = isVarOrStmt();
			rightBrace = isRightBrace();
			
			//If it's the correct structure, return true
			if(isClass && leftParan && args && rightParan && leftBrace && statements && rightBrace){
				return true;
			}else{
				//Output a dialog box informing the user what is wrong
				if(shownError == false){
					JOptionPane.showMessageDialog(null, "Syntax for a constructor isn't correct");
					shownError = true;
				}
				return false;
			}
		}
		else{
			
			//Output a dialog box informing the user what is wrong
			if(shownError == false){
				JOptionPane.showMessageDialog(null, "Class name and constructor name are not the same");
				shownError = true;
			}
		}
		return false;
	}
	
	public boolean isVarOrStmt(){
		boolean newVar;
		boolean statement;
		boolean init;
		
		//Recursively check if there is a new variable or statement or right brace
		if(tnArray.get(curr).getType().equals("rightBrace")){
			return true;
		}
		//Check if it is a new variable
		else if(tnArray.get(curr).getType().equals("dataType")){
			newVar = isNewVar();
			if(newVar){
				isVarOrStmt();
				return true;
			}
		}
		//Checks if it is an assignment
		else if(tnArray.get(curr).getType().equals("identifier")){
			statement = isStatement();
			if(statement){
				isVarOrStmt();
				return true;
			}
		}
		//Checks if it is an object initialisation
		else if(tnArray.get(curr).getType().equals("class")){
			init = isInit();
						
			if(init){
				isVarOrStmt();
				return true;
			}
		}
		//Outputs an error message if any of the above statements return false
		if(shownError == false){
			JOptionPane.showMessageDialog(null, "Error with a new variable statement, an assignment,\n a new object creation or a right brace");
			shownError = true;
		}
		return false;
	}
	
	public boolean isInit(){
		
		Parser p; 
		boolean newClass, name, equal, newKey, newClass2, leftParan, isParam, rightParan, semiCol;
		String thisName, className, constClass;
		params.clear(); //Clears the parameters list for the new objects parameters
		
		//Checks if the initialisation is the correct structure
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
		
		//Sets lineNo to the objects line if in the Main text area, for use in Animator
		if(this.cName.equals("Main")){
			lineNo = tnArray.get(curr).getLine();
		}
		
		semiCol = isSemiColon();
		
		//Checks if the class name and constructor name are the same
		if(className.equals(constClass)){
			
			//If the correct structure, carry on
			if(newClass && name && equal && newKey && newClass2 && leftParan && isParam && rightParan && semiCol){
				
				//Checks the objects name against variables that already exist
				for(Object v : vars){
					
					//If the object is a Parser, cast as a Parser and check the names
					if(v instanceof Parser){
						Parser var = (Parser) v;
						
						//If name already exists, output an error to the user
						if(var.getName().equals(thisName)){
							if(shownError == false){
								JOptionPane.showMessageDialog(null, "Duplicate names");
								shownError = true;
							}
							return false;
						}
					}
					
					//If the object is a variable, cast as a variable and check names
					else if(v instanceof Variable){
						Variable var = (Variable) v;
						if(var.getName().equals(thisName)){
							
							//If name already exists, output an error to the user
							if(shownError == false){
								JOptionPane.showMessageDialog(null, "Duplicate names");
								shownError = true;
							}
							return false;
						}
					}
				}
				
				//If the name doesn't exist, create a new parser and initiate the objects variables
				p = new Parser(other, thisName, params, className, a, lineNo, shownError);
				
				//Check if an error was shown in the Objects parse tree
				// and return false if true
				if(p.getError() == true){
					shownError = p.getError();
					return false;
				}
				//Add the variable to this objects variable list
				vars.add(p);
				if(this.cName.equals("Main")){
					a.vars.add(p); //Add to the Animators list if in the Main text area
				}
				return true;
			}
			else{
				//Output an error if the object creation went wrong
				if(shownError == false){
					JOptionPane.showMessageDialog(null, "Syntax for object creation isn't correct x");
					shownError = true;
				}
				return false;
			}
		}else{
			//Output an error if the object creation went wrong
			if(shownError == false){
				JOptionPane.showMessageDialog(null, "Syntax for object creation isn't correct");
				shownError = true;
			}
		}
		return false;
	}
	
	public boolean isNewVar(){
		Variable newVar;
		String dataType = "";
		String getName = "";
		boolean data, name, semiCol;
		
		//Check if the new variable syntax is correct
		dataType = tnArray.get(curr).getVal(); //Used for creating a new Variable object
		data = isDataType();
		getName = tnArray.get(curr).getVal(); //Used for creating a new Variable object
		name = isVar();
		semiCol = isSemiColon();
		
		//If the syntax is correct, check if the variable name already exists
		if(data && name && semiCol){
			for(Object v : vars){
				if(v instanceof Variable){
					Variable var = (Variable) v;
					
					//Outputs an error message if name already exists
					if(var.getName().equals(getName)){
						if(shownError == false){
							JOptionPane.showMessageDialog(null, "Duplicate names");
							shownError = true;
						}
						return false;
					}
				}
			}
			//Creates a new variable if the name isn't in use
			newVar = new Variable(dataType, getName);
			vars.add(newVar);
			return true;
		}
		//Outputs an error if the variable declaration is incorrect
		if(shownError == false){
			JOptionPane.showMessageDialog(null, "New variable declaration is incorrect");
			shownError = true;
		}
		return false;
	}
	
	public boolean isStatement() {
		String getName;
		String newVal;
		getName = tnArray.get(curr).getVal();
		boolean name, equal, value, leftQuote, isVar, rightQuote, semiCol;
		name = isVar();
		equal = isEqual();
		
		//Checks whether the assignment statement is correct
		
		//Checks if the next token is a quote
		if(tnArray.get(curr).getType().equals("quote")){
			//Checks the vars list to see if the variable exists
			for(Object v : vars){
				if(v instanceof Variable){
					Variable var = (Variable)v;
					
					//If the variable exists, and is a String, check String assignment structure is correct
					if(var.getName().equals(getName) && var.getDataType().equals("String")){
						leftQuote = isQuote();
						newVal = tnArray.get(curr).getVal();
						isVar = isVar();
						rightQuote = isQuote();
						semiCol = isSemiColon();
						
						//If the string assignment structure is correct, set the variable to store the new value
						//and return true
						if(name && equal && leftQuote && isVar && rightQuote && semiCol){
							var.setValue(newVal);
							vars.set(vars.indexOf(v), var);
							return true;
						}
					}
				}
			}
			//Outputs an error if the String assignment structure is incorrect
			if(shownError == false){
				JOptionPane.showMessageDialog(null, "Cannot find a String variable in an assignment");
				shownError = true;
			}
			return false;
		}
		//Checks if the next token is an identifier
		else if(tnArray.get(curr).getType().equals("identifier")){
			for(Object v : vars){
				if(v instanceof Variable){
					Variable var = (Variable) v;
					
					//Checks if the variable exists, and if it is of type String
					//then checks variable assignment structure
					if(var.getName().equals(getName) && var.getDataType().equals("String")){
						newVal = tnArray.get(curr).getVal();
						isVar = isVar();
						semiCol = isSemiColon();
						
						//If isVar is true, check if the new variable exists and is of the type String
						//in both the updatedParams and vars list arrays
						if(isVar){
							for(Object w : vars){
								if(w instanceof Variable){
									Variable otherVar = (Variable)w;
									
									//Checks the new name against the new value name, and if the type is a String in vars list
									if(otherVar.getName().equals(newVal) && otherVar.getDataType().equals("String")){
										var.setValue(otherVar.getValue());
										return true;
									}
								}
							}
							for(Object w : updatedParams){
								if(w instanceof Variable){
									Variable thisVar = (Variable)w;
									
									//Checks the new name against the new value name, and if the type is a String in updatedParams list
									if(thisVar.getName().equals(newVal) && thisVar.getDataType().equals("String")){
										var.setValue(thisVar.getValue());
										
										return true;
									}									
								}
							}
							//If the variable doesn't exist, output an error
							if(shownError == false){
								JOptionPane.showMessageDialog(null, "Cannot find a String variable in an assignment");
								shownError = true;
							}
							return false;
						}
					}
					
					//Checks if the names are the same and if it is of type int
					else if(var.getName().equals(getName) && var.getDataType().equals("int")){
						newVal = tnArray.get(curr).getVal(); //Gets the new value (Variable name)
						isVar = isVar();
						semiCol = isSemiColon();
						
						//If isVar is true, checks vars and updatedParams list arrays for the new variable
						//name that the current variable is being assigned it's value
						if(isVar){
							for(Object w : vars){
								if(w instanceof Variable){
									Variable otherVar = (Variable)w;
									
									//Checks if the new variable is of the type int in the vars list
									if(otherVar.getName().equals(newVal) && otherVar.getDataType().equals("int")){
										var.setValue(otherVar.getValue());
										return true;
									}
								}
							}
							
							for(Object w : updatedParams){
								if(w instanceof Variable){
									Variable thisVar = (Variable)w;
									
									//Checks if the new variable is of the type int in the updatedParams
									if(thisVar.getName().equals(newVal) && thisVar.getDataType().equals("int")){
										var.setValue(thisVar.getValue());
										return true;
									}									
								}
							}
							
							//If the int variable isn't found, an error is output to the user
							if(shownError == false){
								JOptionPane.showMessageDialog(null, "Cannot find an int variable in an assignment");
								shownError = true;
							}
							return false;
						}
					}
				}
				//If the object in the for loop is a Parser
				//cast it as a Parser
				else if(v instanceof Parser){
					Parser var = (Parser) v;
					
					//Checks if the current variable in vars has the same name that is in the assignment
					//statement
					if(var.getName().equals(getName)){
						//Gets the new value that the current variable is being assigned its value
						newVal = tnArray.get(curr).getVal();
						isVar = isVar();
						semiCol = isSemiColon();
						
						if(isVar){
							//Checks if the second variable exists in vars,
							//and assigns the current Parser reference to the new one
							for(Object w : vars){
								if(w instanceof Parser){
									Parser otherVar = (Parser)w;
									if(otherVar.getName().equals(newVal) && otherVar.className.equals(var.className)){
										var = otherVar;
										System.out.println("VAR LINE NO = " + var.getLineNo());
										System.out.println("otherVar line no = " + otherVar.getLineNo());
										return true;
									}
								}
							}
							//Checks if the second variable exists in vars,
							//and assigns the current Parser reference to the new one
							for(Object w : updatedParams){
								if(w instanceof Parser){
									Parser thisVar = (Parser)w;
									if(thisVar.getName().equals(newVal) && thisVar.className.equals(var.className)){
										var = thisVar;
										vars.set(vars.indexOf(v), var);
										System.out.println("VAR LINE NO = " + var.getLineNo());
										System.out.println("otherVar line no = " + thisVar.getLineNo());
										return true;
									}										
								}
							}
							
							//Outputs an error dialog to the user if the object cannot be found
							if(shownError == false){
								JOptionPane.showMessageDialog(null, "Cannot find an object with that name in an assignment");
								shownError = true;
							}
							return false;
						}
					}
				}
			}
			return false;
		}
		//Checks if the token on the right side of the = sign is a number
		else if(tnArray.get(curr).getType().equals("num")){
			
			//Loops through the vars list to check for the variable on the left side of the = sign
			for(Object v : vars){
				if(v instanceof Variable){	
					Variable var = (Variable)v;
					
					//Checks if the current variable has the same name as the one the user has typed in
					//and if the variable is of type int
					if(var.getName().equals(getName) && var.getDataType().equals("int")){
					
						newVal = tnArray.get(curr).getVal();
						value = isInt();
						semiCol = isSemiColon();
						
						//If the int assignment structure is correct, assign the variable with the new value
						if(name && equal && value && semiCol){
							
							var.setValue(newVal);
							vars.set(vars.indexOf(v), var);
							return true;
						}
					}
				}
			}
			//Output an error if the variable isn't the correct type, or the variable isn't found
			if(shownError == false){
				JOptionPane.showMessageDialog(null, "Cannot find an int variable in an assignment");
				shownError = true;
			}
			return false;
		}
		return false;
	}
	
	//Recursively checks if the argument list is correct
	public boolean isArg(int i){
		
		boolean data, name, thisClass;
		String thisDataType;
		String thisName;
		int index;
		
		//index is used to keep an index in tmpParams
		index = i;
		
		//Checks if the argument list is empty
		if(tnArray.get(curr).getType().equals("rightParan")){
			return true;
		}
		//Checks if the current token is a datatype
		else if(tnArray.get(curr).getType().equals("dataType")){
			
			if(tmpParam.get(index) instanceof Variable){
				
				Variable var = (Variable)tmpParam.get(index);
				
				//Checks if the argument structure DataType and then Variable is followed
				thisDataType = tnArray.get(curr).getVal();
				data = isDataType();
				thisName = tnArray.get(curr).getVal();
				name = isVar();
				
				//If the list tmpParam is empty or not initialized
				//output an error dialog
				if(tmpParam == null || tmpParam.isEmpty()){
					if(shownError == false){
						JOptionPane.showMessageDialog(null, "Expected parameters");
						shownError = true;
					}
					return false;
				}
				//If the argument structure and datatype of the tmpParam var is the same
				//set the name to equal the arguments name, and add it to updatedParams
				else if(data && name && var.getDataType().equals(thisDataType)){
					var.setName(thisName);
					updatedParams.add(var);
					isArg(index + 1);
					return true;
				}
				else{
					//Outputs an error dialog if the arguments and parameters are not the same datatypes
					if(shownError == false){
						JOptionPane.showMessageDialog(null, "Constructor arguments and object parameters don't match");
						shownError = true;
					}
				}
			}
		}
		//Checks if the current token is of type comma
		else if(tnArray.get(curr).getType().equals("comma")){
			//Increments the curr value to move on to the next token
			curr++;
			if(isArg(index)){
				return true;
			}
			else{
				return false;
			}
		}
		//Checks if the current token is a class name
		else if(tnArray.get(curr).getType().equals("class")){
			
			//Checks if the variable at the current index in tmpParam
			//is a Parser
			if(tmpParam.get(index) instanceof Parser){
				Parser obj = (Parser) tmpParam.get(index);
				thisDataType = tnArray.get(curr).getVal();
				thisClass = isClass();
				thisName = tnArray.get(curr).getVal();
				name = isVar();
				
				//Checks if the tmpParam list is null or is empty
				//and outputs an error message if it is
				if(tmpParam == null || tmpParam.isEmpty()){
					if(shownError == false){
						JOptionPane.showMessageDialog(null, "Expected parameters");
						shownError = true;
					}
					return false;
				}
				//checks if the class name is the same type as the parameter
				else if(thisClass && name && obj.className.equals(thisDataType)){
					//Sets the name of the current variable to the argument name and also adds it
					//to the updateParams list
					obj.setName(thisName);
					updatedParams.add(obj);
					isArg(index + 1);
					return true;
				}
				else{
					//Outputs an error message if the arguments and parameters don't match
					if(shownError == false){
						JOptionPane.showMessageDialog(null, "Constructor arguments and object parameters don't match");
						shownError = true;
					}
				}
			}
		}
		//Outputs an error message if an argument is expected or a closing parenthesis
		if(shownError == false){
			JOptionPane.showMessageDialog(null, "Expected an argument or a closing paranthesis");
			shownError = true;
		}
		return false;
	}
	
	//Recursively calls itself until a right parenthesis or an error
	public boolean isParam(){
		boolean name;
		String getName;
		
		//Checks if the current token is a right parenthesis
		if(tnArray.get(curr).getType().equals("rightParan")){
			return true;
		}
		//Checks if the current token is an identifier name
		else if(tnArray.get(curr).getType().equals("identifier"))
		{
			getName = tnArray.get(curr).getVal();
			name = isVar();
			
			//Checks if the parameter exists in vars list
			for(Object v : vars){
				if(v instanceof Variable){
					Variable var = (Variable)v;
					
					//Checks if the current object in the iteration has the same name
					//as the parameter variable
					if(var.getName().equals(getName) && name){ //checks whether the variable exists to pass through
						params.add(var);
						if(isParam()){
							return true;
						}
						else{
							//If the recursive call returns false, output an error message
							if(shownError == false){
								JOptionPane.showMessageDialog(null, "Paramaters in object creation are incorrect");
								shownError = true;
							}
							return false;
						}
					}
				}
				else if(v instanceof Parser){
					Parser var = (Parser)v;
					
					//Checks if the Parser name in the current iteration of the for loop
					//has the same name as the parameter
					if(var.getName().equals(getName) && name){
						params.add(var);
						if(isParam()){
							
							return true;
						}
						else{
							//If the recursive call returns false, output an error message
							if(shownError == false){
								JOptionPane.showMessageDialog(null, "Paramaters in object creation are incorrect");
								shownError = true;
							}
							return false;
						}
					}
					
				}
			}
			
			//If the variable doesn't exist in vars, output an error message
			if(shownError == false){
				JOptionPane.showMessageDialog(null, "Variable in the parameters of an object creation does not exist");
				shownError = true;
			}
		}
		//Checks if the next token is a comma
		else if(tnArray.get(curr).getType().equals("comma")){
			curr++;
			if(isParam()){
				return true;
			}
		}
		return false;
	}
	
	public boolean isClass(){
		//Calls the highlight method
		highlight();
		//Checks if the current token is a class name
		if(tnArray.get(curr).getType().equals("class")){
			//Increments the curr value to move on to the next token
			curr++;
			//returns true if it is a class name
			return true;
		}
		//returns false if it isn't a class name
		return false;
	}
	
	public boolean isVar(){
		//Calls the highlight method
		highlight();
		//Checks if the current token is an identifier name
		if(tnArray.get(curr).getType().equals("identifier")){
			//Increments the curr value to move on to the next token
			curr++;
			//Returns true if it is an identifier name
			return true;
		}
		//Returns false if it isn't an identifier name
		return false;
	}
	
	public boolean isEqual(){
		//Calls the highlight method
		highlight();
		//Checks if the current token is an equal sign
		if(tnArray.get(curr).getType().equals("equal")){
			//Increments the curr value to move on to the next token
			curr++;
			//Returns true if it is an equal
			return true;
		}
		//Returns false if it isn't an equal sign
		return false;
	}
	
	public boolean isSemiColon(){
		//Calls the highlight method
		highlight();
		//Checks if the current token is of type eol
		if(tnArray.get(curr).getType().equals("eol")){
			//Increments the curr value to move on to the next token
			curr++;
			//Returns true if it is of type eol
			return true;
		}
		//Returns false if it isn't of type eol
		return false;
	}
	
	public boolean isLeftParan(){
		//Calls the highlight method
		highlight();
		//Checks if the current token is a left parenthesis
		if(tnArray.get(curr).getType().equals("leftParan")){
			//Increments the curr value to move on to the next token
			curr++;
			return true;
		}
		return false;
	}

	public boolean isRightParan(){
		//Calls the highlight method
		highlight();
		//Checks if the current token is a right parenthesis
		if(tnArray.get(curr).getType().equals("rightParan")){
			//Increments the curr value to move on to the next token
			curr++;
			return true;
		}
		return false;
	}
	
	public boolean isLeftBrace(){
		//Calls the highlight method
		highlight();
		//Checks if the current token is a left brace
		if(tnArray.get(curr).getType().equals("leftBrace")){
			//Increments the curr value to move on to the next token
			curr++;
			return true;
		}
		return false;
	}

	public boolean isRightBrace(){
		//Calls the highlight method
		highlight();
		//Checks if the current token is a right brace
		if(tnArray.get(curr).getType().equals("rightBrace")){
			curr++;
			return true;
		}
		return false;
	}
	
	public boolean isDataType(){
		//Calls the highlight method
		highlight();
		//Checks if the current token is type dataType
		if(tnArray.get(curr).getType().equals("dataType")){
			//Increments the curr value to move on to the next token
			curr++;
			return true;
		}
		return false;
	}
	
	public boolean isNewKeyword(){
		//Calls the highlight method
		highlight();
		//Checks if the current token is new
		if(tnArray.get(curr).getType().equals("new")){
			//Increments the curr value to move on to the next token
			curr++;
			return true;
		}
		return false;
	}
	
	public boolean isQuote(){
		//Calls the highlight method
		highlight();
		//Checks if the current token is quote
		if(tnArray.get(curr).getType().equals("quote")){
			//Increments the curr value to move on to the next token
			curr++;
			return true;
		}
		return false;
	}
	
	public boolean isInt(){
		//Calls the highlight method
		highlight();
		//Checks if the current token is num
		if(tnArray.get(curr).getType().equals("num")){
			//Increments the curr value to move on to the next token
			curr++;
			return true;
		}
		return false;
	}
	
	public boolean isClassDec(){
		//Calls the highlight method
		highlight();
		//Checks if the current token is classDec
		if(tnArray.get(curr).getType().equals("classDec")){
			//Increments the curr value to move on to the next token
			curr++;
			return true;
		}
		return false;
	}
	
	public void highlight(){
		
		//sets the current token to not have been highlighted yet
		hasHighlighted = false;
		finished = false;
		String highlightOn;
		
		try{
			//Checks if the parser is analysing the Main text area
			if(cName.equals("Main")){
				
				//Sets the start index of the current highlighter to the current tokens line number
				startIndex = main.getLineStartOffset(tnArray.get(curr).getLine());
				//Sets the end index of the current highlighter to the current tokens line number
				endIndex = main.getLineEndOffset(tnArray.get(curr).getLine());
				//Sets the highlightOn value to Main
				highlightOn = "Main";
				
			}
			else{
				//Sets the start index of the current highlighter to the current tokens line number
				startIndex = other.getLineStartOffset(tnArray.get(curr).getLine());
				//Sets the end index of the current highlighter to the current tokens line number
				endIndex = other.getLineEndOffset(tnArray.get(curr).getLine());
				//Sets the highlightOn value to Other
				highlightOn = "Other";
			}		
			//Adds a new HighlightLine to the Animators highlighters list
			a.highlighters.add(new HighlightLine(startIndex, endIndex, highlightOn, tnArray.get(curr).getLine()));
		}
		catch(BadLocationException ie){
			
		}   
	}
	
	//Checks if the object has been painted to the animated panel
	public boolean isPainted(){
		return painted;
	}
	
	//Checks the line number of the current Parser object creation
	public int getLineNo(){
		return lineNo;
	}
	
	//Checks if there has been an error in the Parser
	public boolean getError(){
		return shownError;
	}
	
	//Returns the objects name
	public String getName(){
		return name;
	}
	
	//Returns the list of variables in the class
	public List<Object> getVars(){
		return vars;
	}
	
	//Sets the objects name
	public void setName(String n){
		name = n;
	}

}