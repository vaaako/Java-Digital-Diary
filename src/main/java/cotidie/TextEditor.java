import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.plaf.metal.*;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TextEditor {
	static JTextArea textArea;
	static JFrame frmMain;
	static Container pane;

	static JMenuBar mnbBar;
	static JMenu mnFile, mnEdit;
	static JMenuItem mniClear, mniOpen, mniSave, mniPrint, mniClose;

	static FileManager manager;
	static int today; // Not really today, just the choosen day (I couldn't think of a better name)

	static boolean isSaved = false; // Alredy saved

	// Constructor
	TextEditor() {
		// Prepare frame
		frmMain = new JFrame("Editor"); // Create frame
		frmMain.setSize(500, 500); // Set size
		frmMain.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close when X is clicked

		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel"); // Set metal look and feel
			MetalLookAndFeel.setCurrentTheme(new OceanTheme()); // Set theme to ocean
		} catch (Exception e) {}

		manager = Calendar.manager;

		textArea = new JTextArea(); // Text component
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);

		mnbBar = new JMenuBar(); // Create a menubar
		mnFile = new JMenu("File"); // Create a menu for menu

		// Create menu items
		mniSave = new JMenuItem("Save");
		mniClear = new JMenuItem("Clear");
		mniClose = new JMenuItem("Close");

		// Add action listener
		mniSave.addActionListener(new mniSaveAction());
		mniClear.addActionListener(new mniClearAction());
		mniClose.addActionListener(new mniCloseAction());

		mnFile.add(mniSave);
		mnFile.add(mniClear);
		mnFile.add(mniClose);

		mnbBar.add(mnFile);

		frmMain.setJMenuBar(mnbBar);
		frmMain.add(textArea);

		// Exit window event
		frmMain.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				if(!isSaved) { // If is not saved, show Confirm Dialogue
					if(JOptionPane.showConfirmDialog(frmMain, "Save before exit?", "Save?", 
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
						saveText();
					}
				}
			}
		});
	}

	public static void open(String day) {
		today = Integer.parseInt(day);

		// Get existent text or default text
		textArea.setText(manager.getDayText(
			String.valueOf(Calendar.realYear),
			Calendar.monthName,
			day
		));

		// Text area listener (just add after load file)
		textArea.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent de) {
				unsavedText();	
			}

			@Override
			public void removeUpdate(DocumentEvent de) {
				unsavedText();
			}

			@Override
			public void changedUpdate(DocumentEvent de) {
				// Plain text components do not fire these events
			}
		});

		// Make frame visible
		frmMain.setResizable(true);
		frmMain.setVisible(true);
	}

	private static void saveText() {
		if(Calendar.realDay != today) {
			JOptionPane.showMessageDialog(frmMain, "You can't edit this!");
			return;
		} else if(textArea.getText().length() == 0) {
			JOptionPane.showMessageDialog(frmMain, "You can't save this, it's empty!");
			return;
		}

		manager.writeToJSON(
			String.valueOf(Calendar.realYear),
			Calendar.monthName,
			String.valueOf(Calendar.realDay),
			textArea.getText()
		);

		isSaved = true;
		frmMain.setTitle("Editor");
		// JOptionPane.showMessageDialog(frmMain, "Saved!");
	}

	private static void unsavedText() {
		isSaved = false;
		frmMain.setTitle("Editor (Unsaved)");
	}

	static class mniClearAction implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			textArea.setText("");
		}
	}

	static class mniSaveAction implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			saveText();
		}
	}

	static class mniCloseAction implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			saveText();
			frmMain.setVisible(false);
		}
	}
}
