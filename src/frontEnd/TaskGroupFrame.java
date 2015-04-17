package frontEnd;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import utilities.swing.SwingUtil;
import core.userDefinedTask.TaskGroup;
import frontEnd.graphics.BootStrapResources;

@SuppressWarnings("serial")
public class TaskGroupFrame extends JFrame {

	private static final int COLUMN_NAME = 0;
	private static final int COLUMN_COUNT = 1;
	private static final int COLUMN_ENABLED = 2;
	private static final int COLUMN_SELECTED = 3;

	private static final Logger LOGGER = Logger.getLogger(TaskGroupFrame.class.getName());

	private final JPanel contentPane;
	protected final JTable tGroups;

	private final BackEndHolder backEnd;

	public TaskGroupFrame(final BackEndHolder backEnd) {
		this.backEnd = backEnd;

		setTitle("Task group");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 450, 299);
		setResizable(false);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowIconified(WindowEvent e) {
				TaskGroupFrame.this.setVisible(false);
				TaskGroupFrame.this.setExtendedState(JFrame.NORMAL);
			}
		});

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JScrollPane scrollPane = new JScrollPane();

		JButton bAdd = new JButton();
		bAdd.setIcon(BootStrapResources.ADD);
		bAdd.setToolTipText("Add a new task group");
		bAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addGroup();
			}
		});

		JButton bRemove = new JButton();
		bRemove.setIcon(BootStrapResources.DELETE);
		bRemove.setToolTipText("Remove this task group");
		bRemove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeGroup();
			}
		});

		JButton bMoveUp = new JButton();
		bMoveUp.setIcon(BootStrapResources.UP);
		bMoveUp.setToolTipText("Move this task group up in the list");
		bMoveUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveGroupUp();
			}
		});

		JButton bMoveDown = new JButton();
		bMoveDown.setIcon(BootStrapResources.DOWN);
		bMoveDown.setToolTipText("Move this task group down in the list");
		bMoveDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveGroupDown();
			}
		});

		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(bAdd)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(bRemove)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(bMoveUp)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(bMoveDown)))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 202, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(bAdd)
						.addComponent(bRemove)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(bMoveUp)
							.addComponent(bMoveDown)))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);

		tGroups = new JTable();
		tGroups.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Group name", "Task count", "Enabled", "Selected"
			}
		){
			@Override
		    public boolean isCellEditable(int row, int column) {
		        return column == 0;
		    }
		});

		tGroups.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					removeGroup();
				} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					syncNames();
				}
			}
		});

		tGroups.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				syncNames();

				int row = tGroups.getSelectedRow();
				int column = tGroups.getSelectedColumn();
				if (row < 0) {
					return;
				}

				if (column == COLUMN_ENABLED) {
					TaskGroup chosen = backEnd.taskGroups.get(row);
					chosen.setEnabled(!chosen.isEnabled(), backEnd.keysManager);
					renderTaskGroup();
				} else if (column == COLUMN_SELECTED) {
					selectGroup();
				}
			}
		});

		DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
		centerRender.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0 ; i < tGroups.getColumnCount(); i++) {
			tGroups.getColumnModel().getColumn(i).setCellRenderer(centerRender);
		}

		scrollPane.setViewportView(tGroups);
		renderTaskGroup();

		contentPane.setLayout(gl_contentPane);
	}

	private void selectGroup() {
		int row = tGroups.getSelectedRow();

		TaskGroup selected = backEnd.taskGroups.get(row);
		if (selected != backEnd.getCurrentTaskGroup()) {
			backEnd.setCurrentTaskGroup(selected);
			backEnd.renderTasks();
		} else {
			backEnd.setCurrentTaskGroup(selected);
		}

		renderTaskGroup();
	}

	private void addGroup() {
		String[] name = SwingUtil.OptionPaneUtil.getInputs(new String[]{"New group name"});
		if (name != null && name.length == 1) {
			String newName = name[0];
			for (TaskGroup group : backEnd.taskGroups) {
				if (group.getName().equals(newName)) {
					JOptionPane.showMessageDialog(TaskGroupFrame.this, "This name already exists. Try again.");
					return;
				}
			}

			backEnd.taskGroups.add(new TaskGroup(newName));
			renderTaskGroup();
		}
	}

	private void removeGroup() {
		int selected = tGroups.getSelectedRow();
		if (selected >= 0) {
			TaskGroup removed = backEnd.taskGroups.remove(selected);

			if (backEnd.taskGroups.size() <= 1) {
				backEnd.taskGroups.add(new TaskGroup("default"));
			}

			if (backEnd.getCurrentTaskGroup() == removed) {
				backEnd.setCurrentTaskGroup(backEnd.taskGroups.get(0));
			}
			renderTaskGroup();
		} else {
			JOptionPane.showMessageDialog(this, "Select a group on the table first.");
		}
	}

	private void moveGroupUp() {
		int selected = tGroups.getSelectedRow();
		if (selected >= 1) {
			Collections.swap(backEnd.taskGroups, selected, selected - 1);
			renderTaskGroup();
		}
	}

	private void moveGroupDown() {
		int selected = tGroups.getSelectedRow();
		if (selected >= 0 && selected < backEnd.taskGroups.size() - 1) {
			Collections.swap(backEnd.taskGroups, selected, selected + 1);
			renderTaskGroup();
		}
	}

	private void syncNames() {
		List<TaskGroup> data = backEnd.taskGroups;

		Set<String> names = new HashSet<>();
		for (TaskGroup group : data) {
			names.add(group.getName());
		}

		int row = 0;
		for (TaskGroup group : data) {
			String newName = SwingUtil.TableUtil.getStringValueTable(tGroups, row, COLUMN_NAME);
			if (!newName.equals("") && !names.contains(newName)) {
				names.remove(group.getName());
				group.setName(newName);
				names.add(newName);
			} else if (!newName.equals(group.getName())) {
				LOGGER.warning("Unable to assign new name " + newName + " to task group with old name " + group.getName());
			}
			row++;
		}
	}

	protected void renderTaskGroup() {
		List<TaskGroup> data = backEnd.taskGroups;
		SwingUtil.TableUtil.setRowNumber(tGroups, data.size());
		SwingUtil.TableUtil.clearTable(tGroups);
		int row = 0;

		for (TaskGroup group : data) {
			tGroups.setValueAt(group.getName(), row, COLUMN_NAME);
			tGroups.setValueAt(group.getTasks().size(), row, COLUMN_COUNT);
			tGroups.setValueAt(group.isEnabled(), row, COLUMN_ENABLED);
			//\u2713 is check mark
			tGroups.setValueAt(backEnd.getCurrentTaskGroup() == group ? '\u2713' : "", row, COLUMN_SELECTED);
			row++;
		}
	}
}
