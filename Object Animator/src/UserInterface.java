import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class UserInterface{
	
	JFrame frame;
	JPanel menu, animatePanel, editorPanel, wrapper, editorPanel2;
	
	PrintObjects animator;
	JMenuBar menuBar;
	JMenu file, help;
	JMenuItem newFile, exit, faq;
	
	JTextArea textEditor, textEditor2;
	
	JButton play;
	
	Parser p;
	
	public UserInterface(){
		
		//Creates all of components to build the user interface
		frame = new JFrame("Object Animator");
		
		menuBar = new JMenuBar();
		
		file = new JMenu("File");
		help = new JMenu("Help");
		
		newFile = new JMenuItem("New File");
		exit = new JMenuItem("Exit");
		faq = new JMenuItem("Frequently Asked Questions");
		
		textEditor = new JTextArea("class Main {\n Main() {\n Class cl = new Class(); \n } \n }");
		textEditor2 = new JTextArea("class Class {\n\n Class() { \n \n } \n }");
		
		animator = new PrintObjects();
		
		play = new JButton("Play");
		
		menu = new JPanel();
		animatePanel = new JPanel();
		editorPanel = new JPanel();
		editorPanel2 = new JPanel();
		wrapper = new JPanel();
		init();
		frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		frame.setBackground(Color.green);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);//This specifies the size of the interface
		frame.setVisible(true); //This ensures the window is visible	
	}
	
	public void init(){
		frame.add(buildMenu(), BorderLayout.NORTH);
		wrapper.setLayout(new GridLayout(1,3));		
		wrapper.add(buildTextEditor());
		wrapper.add(buildTextEditor2());
		wrapper.add(buildAnimator());
		
		frame.add(wrapper, BorderLayout.CENTER);
		
		play.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				animator.objects.clear();
				animator.rects.clear();
				animator.repaint();
				p = new Parser(textEditor, textEditor2, animator);
			}
		});
		
		//Action listener for FAQ menu option
		faq.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				JTextArea msg = new JTextArea("FAQ\n\n");
				msg.append("1. What is the syntax of the code that I can use?");
				msg.append("\n\nTo make a class : \n\"class\" <class name beginning with an upper case letter> { }");
				msg.append("\n\nInside the braces must be a constructor with the structure : ");
				msg.append("\n<same class name you created before the opening brace> ( <insert 0 or more parameters> ) { }"); 
				msg.append("\nVariables can be created in or outside of the constructor, but objects can only ");
				msg.append("be created inside the constructor.");
				msg.append("\n\nTo create a variable : ");
				msg.append("\n<Datatype> <variable name beginning with a lower case letter>;");
				msg.append("\nOnly \"String\" and \"int\" datatypes can be used.");
				msg.append("\n\nTo create an object of another class : ");
				msg.append("\n<class name> <variable name beginning with a lower case letter> = new <class name> \n( <insert 0 or more arguments> ) ;");
				msg.append("\n\nTo assign a value to a String : ");
				msg.append("\n<variable name> = \"<new value without spaces>\"; ");
				msg.append("\n\nTo assign a value to an int : ");
				msg.append("\n<variable name> = <enter a number>;");
				msg.append("\n\nTo assign an object to another object : ");
				msg.append("\n<object name> = <other object name>;");
				msg.append("\n\nParameters of a constructor and arguments of a new object must be the same.");
				msg.append("\nVariable and object creation, and assignments must end with a semicolon.");
				msg.append("\n\n2. How does the animation work?");
				msg.append("\n\nThe program will highlight each line to show what is being processed.");
				msg.append("\nIf the program creates an object, it will then process that class before returning");
				msg.append("to the object creation. If everything is ok, the object will be shown in the right panel.");
				msg.append("\n\n3. What happens if my program is incorrect?");
				msg.append("\n\nIf your program isn't running, a dialogue box pops up giving advice on how to fix it.");
				msg.append("\n\n4. What names/identifiers are allowed?");
				msg.append("\n\nString: begin with a lowercase character; spaces and numbers are not allowed");
				msg.append("\nint: only numbers are allowed");
				msg.append("\nClass names: begin with an uppercase character; numbers and spaces are not allowed");
				msg.append("\nVariable names: begin with a lowercase character; numbers and spaces are not allowed");
				
				msg.setLineWrap(true);
				msg.setWrapStyleWord(true);

				JScrollPane scrollPane = new JScrollPane(msg);
				scrollPane.setPreferredSize(new Dimension(600, 600));
				msg.setEditable(false);
				
				JOptionPane.showMessageDialog(null, scrollPane);
				
			}
			
		});
		
		//Action listener for new file menu option
		newFile.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				
				textEditor.setText("class Main {\n Main() {\n Class cl = new Class(); \n } \n }");
				textEditor2.setText("class Class {\n\n Class() { \n String name; \n } \n }");
			}
			
		});
		
		//Action listener for exit menu option
		exit.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
					frame.dispose();	
			}
			
		});
		
	}
	
	//Builds the menu and returns a JPanel
	public JPanel buildMenu(){
		menu.setLayout(new BorderLayout());
		
		file.add(newFile);
		file.add(exit);
		
		help.add(faq);
		
		menuBar.add(file);
		menuBar.add(help);
		
		menu.add(menuBar);
		
		return menu;
	}
	
	//Builds the animator panel and returns it
	public JPanel buildAnimator(){
		GridBagConstraints c = new GridBagConstraints();
		
		animatePanel.setBackground(Color.decode("#fff2cc"));
		animatePanel.setLayout(new GridBagLayout());
		
		c.weightx = 1.0;
		c.weighty = 1.0;
		
		animator.setBackground(Color.decode("#fff2cc"));
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 3;
		animatePanel.add(animator, c);
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridheight = 1;
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(10, 10, 10, 10);
		c.fill = GridBagConstraints.NONE;
		animatePanel.add(play, c);
		
		return animatePanel;		
	}
	
	//Builds the left text editor and returns the JPanel
	public JPanel buildTextEditor(){
		editorPanel.setLayout(new BorderLayout());
		
		textEditor.setBackground(Color.decode("#fff2cc"));
		JScrollPane js = new JScrollPane(textEditor);
		
		editorPanel.add(js, BorderLayout.CENTER);
		
		return editorPanel;
	}
	
	//Builds the right text editor and returns the JPanel
	public JPanel buildTextEditor2(){
		editorPanel2.setLayout(new BorderLayout());
		
		textEditor2.setBackground(Color.decode("#fff2cc"));
		JScrollPane js = new JScrollPane(textEditor2);
		
		editorPanel2.add(js, BorderLayout.CENTER);
		
		return editorPanel2;
	}
	
	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable() {
		      public void run() {
		        UserInterface ui = new UserInterface();
		      }
		    });
	}
}
