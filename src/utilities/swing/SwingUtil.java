package utilities.swing;

import java.awt.Frame;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import utilities.Function;

public class SwingUtil {

	public static final class JFrameUtil {

		private JFrameUtil() {}

		public static void focus(JFrame frame, Function<Void, Boolean> callBackRender) {
			if (frame.getState() == Frame.ICONIFIED) {
				frame.setState(Frame.NORMAL);
			}

			if (!frame.isVisible()) {
				callBackRender.apply(null);
				frame.setVisible(true);
			}

			frame.requestFocus();
			frame.toFront();
		}
	}

	public static final class TableUtil {

		private TableUtil() {}

		public static class TableSearcher {
			private int index;
			private final Function<Point, Void> action;
			private final List<Point> found;

			public TableSearcher(JTable table, String value, Function<Point,Void> functions){
				this.action = functions;

				this.found = new ArrayList<Point>();
				index = -1;
				for (int row = 0; row < table.getRowCount(); row++) {
					for (int column = 0; column < table.getColumnCount(); column++) {
						if (value.equals(getStringValueTable(table, row, column))) {
							found.add(new Point(row, column));
						}
					}
				}

				if (!found.isEmpty()) {
					index = 0;
				}
			}

			public boolean endedForward() {
				return (index == -1) || (index == found.size() - 1);
			}

			public boolean endedBackward() {
				return (index == -1) || (index == 0);
			}

			public void searchForward() {
				if (!found.isEmpty()) {
					index = Math.min(index + 1, found.size() - 1);
					action.apply(found.get(index));
				}
			}

			public void searchBackward() {
				if (!found.isEmpty()) {
					index = Math.max(index - 1, 0);
					action.apply(found.get(index));
				}
			}
		}

		public static void findValue(JTable table, String value, Function<Point, Void> action) {
			for (int row = 0; row < table.getRowCount(); row++) {
				for (int column = 0; column < table.getColumnCount(); column++) {
					if (value.equals(table.getValueAt(row, column))) {
						action.apply(new Point(row, column));
					}
				}
			}
		}

		public static void scrollToSelectedRow(JTable table) {
			int selectedRow = table.getSelectedRow();
			table.scrollRectToVisible(table.getCellRect(selectedRow, 0, true));
		}

		public static void highLightCell(JTable table, int row, int column) {
			table.setRowSelectionInterval(row, row);
			table.setColumnSelectionInterval(column, column);
			scrollToSelectedRow(table);
		}

		public static boolean highLightRow(JTable table, int column, String data) {
			for (int i = 0; i < table.getRowCount(); i++) {
				String value = getStringValueTable(table, i, column);
				if (value.equals(data)) {
					focusRowTable(table, i);
					return true;
				}
			}
			return false;
		}

		public static void ensureRowNumber(JTable table, int maxSize) {
			//Make sure enough space
			if (maxSize > table.getRowCount()) {
				int rowCount = table.getRowCount();
				//Add more rows
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				for (int i = 0; i < maxSize - rowCount; i++) {
					model.addRow(new Object[model.getColumnCount()]);
				}
			}
		}

		public static void setRowNumber(JTable table, int numberOfRow) {
			ensureRowNumber(table, numberOfRow);
			//Now table has at least numberOfRow. We make sure table does not have any extra row
			int toDelete = table.getRowCount() - numberOfRow;
			removeLastRowTable(table, toDelete);
		}

		public static void clearTable(JTable table) {
			for (int i = 0; i < table.getRowCount(); i++) {
				for (int j = 0; j < table.getColumnCount(); j++) {
					table.setValueAt("", i, j);
				}
			}
		}

		public static void clearSelectedTable(JTable table) {
			int[] columns = table.getSelectedColumns();
			int[] rows = table.getSelectedRows();

			for (int i = 0; i < rows.length; i++) {
				for (int j = 0; j < columns.length; j++) {
					table.setValueAt("", rows[i], columns[j]);
				}
			}
		}

		public static String getStringValueTable(JTable table, int row, int column) {
			try {
				Object value = table.getValueAt(row, column);
				if (value == null) {
					return "";
				} else {
					return value.toString().trim();
				}
			} catch (Exception e) {
				return "";
			}
		}

		public static void focusRowTable(JTable table, int row) {
			table.setRowSelectionInterval(row, row);
			table.setColumnSelectionInterval(0, table.getColumnCount() - 1);
			scrollToSelectedRow(table);
		}

		public static void focusColumnTable(JTable table, int column) {
			table.setColumnSelectionInterval(column, column);
			table.setRowSelectionInterval(0, table.getColumnCount() - 1);
		}

		public static void focusCellTable(JTable table, int row, int column) {
			table.setRowSelectionInterval(row, row);
			table.setColumnSelectionInterval(column, column);
		}

		public static void addRowTable(JTable table, int numberOfRow) {
			if (numberOfRow <= 0) {
				return;
			}

			for (int i = 0; i < numberOfRow; i++) {
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				model.addRow(new Object[model.getColumnCount()]);
			}
		}

		public static void removeLastRowTable(JTable table, int numberOfRow) {
			int toRemove = Math.min(numberOfRow, table.getRowCount());

			for (int i = 0; i < toRemove; i++) {
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				model.removeRow(model.getRowCount() - 1);
			}
		}

		/**
		 * Remove the last rows of the table that do not contain any data
		 * @param table the table with rows to be removed
		 */
		public static int removeLastRowsTable(JTable table) {
			if (table.getRowCount() == 0) {
				return 0;
			}

			DefaultTableModel model = (DefaultTableModel) table.getModel();
			int count = 0;

			while (true) {
				boolean remove = true;
				for (int i = 0; i < table.getColumnCount(); i++) {
					String data = getStringValueTable(table,
							table.getRowCount() - 1, i);
					if (!data.equals("")) {
						remove = false;
						break;
					}
				}

				if (remove) {
					count++;
					model.removeRow(model.getRowCount() - 1);
				} else {
					break;
				}
			}

			return count;
		}
	}

	public static final class OptionPaneUtil {

		private OptionPaneUtil() {}

		public static int getSelection(String title, String[] choices, int selected) {
			JList<String> list = new JList<String>(choices);
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			if (selected != -1 && selected < choices.length) {
				list.setSelectedIndex(selected);
			}

			int selection = JOptionPane.showConfirmDialog(null, list, title, JOptionPane.YES_NO_OPTION);

			if (selection == JOptionPane.OK_OPTION) {
				return list.getSelectedIndex();
			} else {
				return -1;
			}
		}

		public static void showValues(String[] titles, String[] values) {
			if (titles.length == 0 || (titles.length != values.length)) {
				return;
			}

			JTextField[] textFields = new JTextField[titles.length];

			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

			for (int i = 0; i < titles.length; i++) {
				JPanel myPanel = new JPanel();
				if (titles[i] != null) {
					myPanel.add(new JLabel(titles[i]));
				} else {
					myPanel.add(new JLabel(i + ")"));
				}

				textFields[i] = new JTextField(10);
				textFields[i].setText(values[i]);
				textFields[i].setEditable(false);

				myPanel.add(textFields[i]);
				mainPanel.add(myPanel);
			}

			JOptionPane.showMessageDialog(null, mainPanel,
					"Enter values", JOptionPane.OK_OPTION);
		}

		public static int confirmValues(String confirmTitle, String[] titles, String[] values) {
			if (titles.length == 0 || (titles.length != values.length)) {
				return -1;
			}

			JTextField[] textFields = new JTextField[titles.length];

			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

			for (int i = 0; i < titles.length; i++) {
				JPanel myPanel = new JPanel();
				if (titles[i] != null) {
					myPanel.add(new JLabel(titles[i]));
				} else {
					myPanel.add(new JLabel(i + ")"));
				}

				textFields[i] = new JTextField();
				textFields[i].setText(values[i]);
				textFields[i].setEditable(false);

				myPanel.add(textFields[i]);
				mainPanel.add(myPanel);
			}

			return JOptionPane.showConfirmDialog(null, mainPanel,
					confirmTitle, JOptionPane.YES_NO_OPTION);
		}

		public static String[] getInputs(String[] titles) {
			if (titles.length == 0) {
				return null;
			}

			JTextField[] textFields = new JTextField[titles.length];
			String[] output = new String[titles.length];

			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

			for (int i = 0; i < titles.length; i++) {
				JPanel myPanel = new JPanel();
				if (titles[i] != null) {
					myPanel.add(new JLabel(titles[i]));
				} else {
					myPanel.add(new JLabel(i + ")"));
				}
				textFields[i] = new JTextField(10);
				textFields[i].setText("");
				myPanel.add(textFields[i]);
				mainPanel.add(myPanel);
			}

			int result = JOptionPane.showConfirmDialog(null, mainPanel,
					"Enter values", JOptionPane.OK_CANCEL_OPTION);

			if (result == JOptionPane.OK_OPTION) {
				for (int i = 0; i < titles.length; i++) {
					output[i] = textFields[i].getText();
				}
				return output;
			} else {
				return null;
			}
		}
	}
}
