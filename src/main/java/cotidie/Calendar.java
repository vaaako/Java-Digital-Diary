import java.io.IOException;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.plaf.metal.*;

public class Calendar {
	static JFrame frmMain;
	static JPanel pnlCalendar;
	static JLabel lblMonth, lblYear;
	static JTable tblCalendar;
	static JButton btnPrev, btnNext, btnBackup;
	static JComboBox cmbYear;
	static JScrollPane stblCalendar; // The scrollpane
	
	static Container pane;
	static DefaultTableModel mtblCalendar; // Table model
	static int realYear, realMonth, realDay, currentYear, currentMonth;
	static String monthName;

	static TextEditor txtEditor;
	static FileManager manager;
	static final int WIDTH  = 660, // 330 - 660
						HEIGHT = 400; // 375 - 750

	// Constructor
	Calendar() {
		// Look and feel
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		catch (ClassNotFoundException e) {}
		catch (InstantiationException e) {}
		catch (IllegalAccessException e) {}
		catch (UnsupportedLookAndFeelException e) {}
		
		// Prepare frame
		frmMain = new JFrame("Cotidie"); // Create frame
		frmMain.setSize(WIDTH, HEIGHT); // Set size to 400x400 pixels
		frmMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close when X is clicked
		pane = frmMain.getContentPane(); // Get content pane
		pane.setLayout(null); // Apply null layout

		txtEditor = new TextEditor();
		manager = new FileManager();

		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel"); // Set metal look and feel
			MetalLookAndFeel.setCurrentTheme(new OceanTheme()); // Set theme to ocean
		} catch (Exception e) {}


		// Create controls
		lblMonth = new JLabel("January");
		lblYear = new JLabel("Change year:");
		cmbYear = new JComboBox();
		btnPrev = new JButton("<");
		btnNext = new JButton(">");
		btnBackup = new JButton("Backup");
		mtblCalendar = new DefaultTableModel() { public boolean isCellEditable(int rowIndex, int mColIndex) { return false; } };
		tblCalendar = new JTable(mtblCalendar);
		stblCalendar = new JScrollPane(tblCalendar);
		pnlCalendar = new JPanel(null);

		// Set border
		pnlCalendar.setBorder(BorderFactory.createTitledBorder("Calendar"));
		
		// Register action listeners
		btnPrev.addActionListener(new btnPrevAction());
		btnNext.addActionListener(new btnNextAction());
		btnBackup.addActionListener(new btnBackupAction());
		cmbYear.addActionListener(new cmbYearAction());

		// Add controls to pane
		pane.add(pnlCalendar);
		pnlCalendar.add(lblMonth);
		pnlCalendar.add(lblYear);
		pnlCalendar.add(cmbYear);
		pnlCalendar.add(btnPrev);
		pnlCalendar.add(btnNext);
		pnlCalendar.add(btnBackup);
		pnlCalendar.add(stblCalendar);
		
		// Set bounds (Up to down order)
		// X, Y, Width, Height
		pnlCalendar.setBounds(0, 0, WIDTH-10, HEIGHT-40); // Offset line
		
		btnPrev.setBounds(10, 25, 100, 25); // Prev Button
		btnNext.setBounds(WIDTH-120, 25, 100, 25); // Next Button
		btnBackup.setBounds(20, 305, 100, 20); // Prev Button

		lblMonth.setBounds(WIDTH/2, 25, 100, 100); // Label Month
		
		stblCalendar.setBounds(10, 50, WIDTH-30, HEIGHT-100); // Calendar area
		cmbYear.setBounds(WIDTH-115, 305, 80, 20); // Combo box years



		// Get real month/year
		GregorianCalendar cal = new GregorianCalendar(); // Create calendar
		realDay = cal.get(GregorianCalendar.DAY_OF_MONTH); // Get day
		realMonth = cal.get(GregorianCalendar.MONTH); // Get month
		realYear = cal.get(GregorianCalendar.YEAR); // Get year
		currentMonth = realMonth; // Match month and year
		currentYear = realYear;
		
		// Add headers
		String[] headers = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" }; // All headers
		for(int i=0; i<7; i++) {
			mtblCalendar.addColumn(headers[i]); // Where days will be in
		}
		
		tblCalendar.getParent().setBackground(tblCalendar.getBackground()); // Set background
		
		// No resize/reorder
		tblCalendar.getTableHeader().setResizingAllowed(false);
		tblCalendar.getTableHeader().setReorderingAllowed(false);
		
		// Single cell selection
		tblCalendar.setColumnSelectionAllowed(true);
		tblCalendar.setRowSelectionAllowed(true);
		tblCalendar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// Set row/column count
		tblCalendar.setRowHeight(38);
		mtblCalendar.setColumnCount(7);
		mtblCalendar.setRowCount(6);
		
		// Populate table
		for(int i=realYear-100; i<=realYear+100; i++) {
			cmbYear.addItem(String.valueOf(i));
		}


		// Mouse listerner
		tblCalendar.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) { // Run one time only
				if (e.getClickCount() == 2) { // Clicked twice
					JTable target = (JTable)e.getSource();
					int row = target.getSelectedRow(),
						column = target.getSelectedColumn();

					// System.out.println(tblCalendar.getValueAt(row, column));
					txtEditor.open(tblCalendar.getValueAt(row, column).toString());
				}
			}
		});
	}

	public static void open() {
		// Make frame visible
		frmMain.setResizable(false);
		frmMain.setVisible(true);

		// Refresh calendar
		Calendar.refreshCalendar(realMonth, realYear); // Refresh calendar
	}

	public static void refreshCalendar(int month, int year) {
		// Variables
		String[] months =  {
			"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
		}; int nod, som; // Number Of Days, Start Of Month
		
		// Allow/disallow buttons
		btnPrev.setEnabled(true);
		btnNext.setEnabled(true);
		if(month == 0 && year <= realYear-10)   { btnPrev.setEnabled(false); } // Too earl y
		if(month == 11 && year >= realYear+100) { btnNext.setEnabled(false); } // Too lat e
		monthName = months[month];
		lblMonth.setText(monthName); // Refresh the month label (at the top)
		lblMonth.setBounds((WIDTH/2)-40, 25, 180, 25); // Re-align label with calendar
		cmbYear.setSelectedItem(String.valueOf(year)); // Select the correct year in the combo box
		
		// Clear table
		for(int i=0; i<6; i++) {
			for(int j=0; j<7; j++) {
				mtblCalendar.setValueAt(null, i, j);
			}
		}
		
		// Get first day of month and number of days
		GregorianCalendar cal = new GregorianCalendar(year, month, 1);
		nod = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH); // What day the month ends (30, 31, 28)
		som = cal.get(GregorianCalendar.DAY_OF_WEEK); // What day of week the month starts (1-7)
		
		// Draw calendar
		for(int i=1; i<=nod; i++) {
			int row = (i+som-2)/7;
			int column  =  (i+som-2)%7;
			mtblCalendar.setValueAt(i, row, column); // Add days
		}
		
		// Apply renderers
		tblCalendar.setDefaultRenderer(tblCalendar.getColumnClass(0), new tblCalendarRenderer());
	}
	

	static class tblCalendarRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused, int row, int column) {
			super.getTableCellRendererComponent(table, value, selected, focused, row, column);

			if(column == 0 || column == 6) { // Week-end
				setBackground(new Color(255, 220, 220)); // Light red background
			} else { // Week
				setBackground(new Color(255, 255, 255)); // White background
			}


			if(value != null) {
				if (Integer.parseInt(value.toString()) == realDay && currentMonth == realMonth && currentYear == realYear) { // Today
					setBackground(new Color(220, 220, 255)); // Blue background
				}

				if(selected) {
					setBackground(new Color(227, 220, 255));
				}
			}

			setBorder(null);
			setForeground(Color.black);
			return this;
		}
	}
	
	static class btnPrevAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(currentMonth == 0){ // Back one year
				currentMonth = 11;
				currentYear -= 1;
			}
			else { // Back one month
				currentMonth -= 1;
			}
			refreshCalendar(currentMonth, currentYear);
		}
	}

	static class btnNextAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(currentMonth == 11){ // Foward one year
				currentMonth = 0;
				currentYear += 1;
			} else { // Foward one month
				currentMonth += 1;
			}
			refreshCalendar(currentMonth, currentYear);
		}
	}


	static class btnBackupAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				manager.backup("backup.json");
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		}
	}


	static class cmbYearAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(cmbYear.getSelectedItem() != null) {
				String b = cmbYear.getSelectedItem().toString();
				currentYear = Integer.parseInt(b);
				refreshCalendar(currentMonth, currentYear);
			}
		}
	}
}
