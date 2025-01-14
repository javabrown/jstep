package com.jbrown.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;

public class FileEditor extends JPanel {
	static String DEFAULT_FILE = "c:/test/tweets.tsv";
	
	private Action _openAction;
	private Action _saveAction;
	private JTextComponent _textComponent;
	private Hashtable _actionHash;
	
	public static void main(String[] args) {
		FileEditor editor = new FileEditor();
		
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(editor, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(3);
		frame.setVisible(true);
	}

	// Create an editor.
	public FileEditor() {
		
		initComponent();
		makeActionsPretty();

		this.setLayout(new BorderLayout());
		 
		this.add(_textComponent, BorderLayout.CENTER);
		//content.add(createToolBar(), BorderLayout.NORTH);
		//setJMenuBar(createMenuBar());
		this.add(createMenuBar(), BorderLayout.NORTH);
		
		//setSize(320, 240);
		//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//this.setVisible(true);
	}

	private void initComponent() {
		_textComponent = createTextComponent();
		_openAction = new OpenAction();
		_saveAction = new SaveAction();
		_actionHash = new Hashtable();
	}

	// Create the JTextComponent subclass.
	protected JTextComponent createTextComponent() {
		JTextArea ta = new JTextArea();
		ta.setLineWrap(true);
		return ta;
	}

	// Add icons and friendly names to actions we care about.
	protected void makeActionsPretty() {
		Action a;
		a = _textComponent.getActionMap().get(DefaultEditorKit.cutAction);
		a.putValue(Action.SMALL_ICON, new ImageIcon("cut.gif"));
		a.putValue(Action.NAME, "Cut");

		a = _textComponent.getActionMap().get(DefaultEditorKit.copyAction);
		a.putValue(Action.SMALL_ICON, new ImageIcon("copy.gif"));
		a.putValue(Action.NAME, "Copy");

		a = _textComponent.getActionMap().get(DefaultEditorKit.pasteAction);
		a.putValue(Action.SMALL_ICON, new ImageIcon("paste.gif"));
		a.putValue(Action.NAME, "Paste");

		a = _textComponent.getActionMap().get(DefaultEditorKit.selectAllAction);
		a.putValue(Action.NAME, "Select All");
	}

	// Create a simple JToolBar with some buttons.
	protected JToolBar createToolBar() {
		JToolBar bar = new JToolBar();

		// Add simple actions for opening & saving.
		bar.add(getOpenAction()).setText("");
		bar.add(getSaveAction()).setText("");
		bar.addSeparator();

		// Add cut/copy/paste buttons.
		bar.add(_textComponent.getActionMap().get(DefaultEditorKit.cutAction))
				.setText("");
		bar.add(_textComponent.getActionMap().get(DefaultEditorKit.copyAction))
				.setText("");
		bar.add(_textComponent.getActionMap().get(DefaultEditorKit.pasteAction))
				.setText("");
		return bar;
	}

	// Create a JMenuBar with file & edit menus.
	protected JPanel createMenuBar() {
		JMenuBar menubar = new JMenuBar();
		
		JMenu file = new JMenu("File");
		JMenu edit = new JMenu("Edit");
		menubar.add(file);
		menubar.add(edit);
		 
		
		file.add(getOpenAction());
		file.add(getSaveAction());
		file.add(new ExitAction());
		edit.add(_textComponent.getActionMap().get(DefaultEditorKit.cutAction));
		edit.add(_textComponent.getActionMap().get(DefaultEditorKit.copyAction));
		edit.add(_textComponent.getActionMap()
				.get(DefaultEditorKit.pasteAction));
		edit.add(_textComponent.getActionMap().get(
				DefaultEditorKit.selectAllAction));
		

		JPanel jp = new JPanel();
		jp.setLayout(new GridLayout(2, 1, 2, 5));
		jp.add(menubar);
		jp.add(new JTextField("c:\test\tweets.tsv"));
		
		
		return jp;//menubar;
	}

	// Subclass can override to use a different open action.
	protected Action getOpenAction() {
		return _openAction;
	}

	// Subclass can override to use a different save action.
	protected Action getSaveAction() {
		return _saveAction;
	}

	protected JTextComponent getTextComponent() {
		return _textComponent;
	}

	// ********** ACTION INNER CLASSES ********** //

	// A very simple exit action
	public class ExitAction extends AbstractAction {
		public ExitAction() {
			super("Exit");
		}

		public void actionPerformed(ActionEvent ev) {
			System.exit(0);
		}
	}

	// An action that opens an existing file
	class OpenAction extends AbstractAction {
		public OpenAction() {
			super("Open", new ImageIcon("icons/open.gif"));
			readDefaultFileOnLoad();
		}
		
		private void readDefaultFileOnLoad(){
			File defaultFile = new File(DEFAULT_FILE);
			if(!defaultFile.exists()) {
				try {
					defaultFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} 
			
			this.read(defaultFile);
		}

		public void actionPerformed(ActionEvent ev) {
			JFileChooser chooser = new JFileChooser("c:/test/");
			if (chooser.showOpenDialog(FileEditor.this) != JFileChooser.APPROVE_OPTION)
				return;
			File file = chooser.getSelectedFile();
			if (file == null) {
				return;
			}

			this.read(file);
		}

		public void read(File file) {
			FileReader reader = null;
			try {
				reader = new FileReader(file);
				_textComponent.read(reader, null);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(FileEditor.this,
						"File Not Found", "ERROR", JOptionPane.ERROR_MESSAGE);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException x) {
					}
				}
			}
		}
	}

	class SaveAction extends AbstractAction {
		public SaveAction() {
			super("Save", new ImageIcon("icons/save.png"));
		}

		// Query user for a filename and attempt to open and write the text
		// component's content to the file.
		public void actionPerformed(ActionEvent ev) {
			JFileChooser chooser = new JFileChooser("c:/test/tweets.tsv");
			if (chooser.showSaveDialog(FileEditor.this) != JFileChooser.APPROVE_OPTION)
				return;
			File file = chooser.getSelectedFile();
			if (file == null)
				return;

			FileWriter writer = null;
			try {
				writer = new FileWriter(file);
				_textComponent.write(writer);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(FileEditor.this,
						"File Not Saved", "ERROR", JOptionPane.ERROR_MESSAGE);
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException x) {
					}
				}
			}
		}
	}
}
