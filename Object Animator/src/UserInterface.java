import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.event.*;

public class UserInterface{
	
	JFrame frame;
	JPanel menu, animatePanel, editorPanel, wrapper, editorPanel2;
	
	PrintObjects animator;
	JMenuBar menuBar;
	JMenu file, edit, help;
	JMenuItem newFile, exit, undo, redo, faq;
	
	JTextArea textEditor, textEditor2;
	
	JButton play;
	
	Parser p;
	
	public UserInterface(){
		
		frame = new JFrame("Object Animator");
		
		menuBar = new JMenuBar();
		
		file = new JMenu("File");
		edit = new JMenu("Edit");
		help = new JMenu("Help");
		
		newFile = new JMenuItem("New File");
		exit = new JMenuItem("Exit");
		undo = new JMenuItem("Undo");
		redo = new JMenuItem("Redo");
		faq = new JMenuItem("Frequently Asked Questions");
		
		textEditor = new JTextArea("Main {\n Main() {\n Class cl = new Class(); \n } \n }");
		textEditor2 = new JTextArea("Class {\n\n Class(String line) { \n String name; \n } \n }");
		
		animator = new PrintObjects();
		
		play = new JButton("Play");
		
		play.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				animator.objects.clear();
				animator.rects.clear();
				p = new Parser(textEditor, textEditor2, animator);
			}
		});
		
		menu = new JPanel();
		animatePanel = new JPanel();
		editorPanel = new JPanel();
		editorPanel2 = new JPanel();
		wrapper = new JPanel();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
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
	}
	
	public JPanel buildMenu(){
		menu.setLayout(new BorderLayout());
		
		file.add(newFile);
		file.add(exit);
		
		edit.add(undo);
		edit.add(redo);
		
		help.add(faq);
		
		menuBar.add(file);
		menuBar.add(edit);
		menuBar.add(help);
		
		menu.add(menuBar);
		
		return menu;
	}
	
	public JPanel buildAnimator(){
		GridBagConstraints c = new GridBagConstraints();
		
		animatePanel.setBackground(Color.cyan);
		animatePanel.setLayout(new GridBagLayout());
		
		c.weightx = 1.0;
		c.weighty = 1.0;
		
		animator.setBackground(Color.cyan);
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
	
	public JPanel buildTextEditor(){
		editorPanel.setLayout(new BorderLayout());
		
		textEditor.setBackground(Color.cyan);
		JScrollPane js = new JScrollPane(textEditor);
		
		editorPanel.add(js, BorderLayout.CENTER);
		
		return editorPanel;
	}
	public JPanel buildTextEditor2(){
		editorPanel2.setLayout(new BorderLayout());
		
		textEditor2.setBackground(Color.cyan);
		JScrollPane js = new JScrollPane(textEditor2);
		
		editorPanel2.add(js, BorderLayout.CENTER);
		
		return editorPanel2;
	}
	
	public static void main(String[] args){
		UserInterface ui = new UserInterface();
	}
}
