package frontEnd;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.jnativehook.NativeHookException;

import utilities.FileUtility;
import utilities.Function;

import commonTools.AreaClickerTool;
import commonTools.ClickerTool;

import core.UserDefinedAction;
import core.controller.Core;
import core.recorder.Recorder;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 2804146302677040692L;

	private static final Logger LOGGER = Logger.getLogger(MainFrame.class.getName());

	private final BackEndHolder backEnd;

	protected TrayIcon trayIcon;

	protected HotkeySetting hotkey;
	private final JPanel contentPane;
	protected final JTextField tfRepeatCount;
	protected final JTextField tfRepeatDelay;
	protected JButton bRecord, bReplay, bCompile, bRun;
	protected JTextArea taSource, taStatus;
	protected JRadioButtonMenuItem rbmiCompileJava, rbmiCompilePython;
	private final JTextField tfMousePosition;
	protected final JTable tTasks;

	/**
	 * Create the frame.
	 * @throws NativeHookException
	 */
	@SuppressWarnings("serial")
	public MainFrame() throws NativeHookException {
		backEnd = new BackEndHolder(this);
		backEnd.config.loadConfig(null);
		hotkey = new HotkeySetting(backEnd);

		if (!SystemTray.isSupported()) {
			LOGGER.warning("System tray is not supported!");
			trayIcon = null;
		} else {
			trayIcon = new MinimizedFrame(Toolkit.getDefaultToolkit().getImage("Repeat.jpg"), backEnd);
		}

		/*************************************************************************************/
		backEnd.keysManager.startGlobalListener();
		backEnd.keysManager.setDisablingFunction(new Function<Void, Boolean>(){
			@Override
			public Boolean apply(Void r) {
				return hotkey.isVisible();
			}
		});
		backEnd.keysManager.registerKey(backEnd.config.getRECORD(), new UserDefinedAction() {
			@Override
			public void action(Core controller) throws InterruptedException {
				backEnd.switchRecord();
			}
		});

		backEnd.keysManager.registerKey(backEnd.config.getREPLAY(), new UserDefinedAction() {
			@Override
			public void action(Core controller) throws InterruptedException {
				backEnd.switchReplay();
			}
		});

		backEnd.keysManager.registerKey(backEnd.config.getCOMPILED_REPLAY(), new UserDefinedAction() {
			@Override
			public void action(Core controller) throws InterruptedException {
				backEnd.switchRunningCompiledAction();
			}
		});
		/*************************************************************************************/
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 590, 327);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				backEnd.exit();
			}

			@Override
			public void windowIconified(WindowEvent e) {
				if (trayIcon == null) {
					return;
				}

				try {
					SystemTray.getSystemTray().add(trayIcon);
				} catch (AWTException e1) {
					LOGGER.log(Level.SEVERE, "Unable to add program to system tray", e);
					return;
				}
				MainFrame.this.setVisible(false);
			}
		});

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);

		JMenuItem miExit = new JMenuItem("Exit");
		miExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_MASK));
		miExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.exit();
			}
		});

		taSource = new JTextArea();
		taStatus = new JTextArea();
		taStatus.setToolTipText("Right click to clear");
		taStatus.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					taStatus.setText("");
				}
			}
		});

		JMenuItem miLoadSource = new JMenuItem("Load Source...");
		miLoadSource.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK));
		miLoadSource.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnVal = chooser.showOpenDialog(MainFrame.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					taSource.setText(FileUtility.readFromFile(file).toString());
				}
			}
		});

		JMenuItem miSaveSource = new JMenuItem("Save Source...");
		miSaveSource.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		miSaveSource.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnVal = chooser.showOpenDialog(MainFrame.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					FileUtility.writeToFile(new StringBuffer(taSource.getText()), file, false);
				}
			}
		});

		JMenuItem miLoadConfig = new JMenuItem("Load config...");
		miLoadConfig.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnVal = chooser.showOpenDialog(MainFrame.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					backEnd.config.loadConfig(chooser.getSelectedFile());
				}
			}
		});

		mnNewMenu.add(miLoadConfig);
		mnNewMenu.add(miLoadSource);
		mnNewMenu.add(miSaveSource);
		mnNewMenu.add(miExit);

		ButtonGroup group = new ButtonGroup();

		JMenu mnNewMenu_2 = new JMenu("Tool");
		menuBar.add(mnNewMenu_2);

		JMenuItem mntmNewMenuItem = new JMenuItem("Generate source");
		mntmNewMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK));
		mntmNewMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (rbmiCompileJava.isSelected()) {
					taSource.setText(backEnd.recorder.getGeneratedCode(Recorder.JAVA_LANGUAGE));
				} else if (rbmiCompilePython.isSelected()) {
					taSource.setText(backEnd.recorder.getGeneratedCode(Recorder.PYTHON_LANGUAGE));
				}
			}
		});
		mnNewMenu_2.add(mntmNewMenuItem);

		JMenu mnNewMenu_3 = new JMenu("Compiling Language");
		mnNewMenu_2.add(mnNewMenu_3);

		rbmiCompileJava = new JRadioButtonMenuItem("Java");
		mnNewMenu_3.add(rbmiCompileJava);
		rbmiCompileJava.setSelected(true);
		rbmiCompileJava.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshCompilingLanguage();
			}
		});
		group.add(rbmiCompileJava);

		rbmiCompilePython = new JRadioButtonMenuItem("Python");
		mnNewMenu_3.add(rbmiCompilePython);
		rbmiCompilePython.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshCompilingLanguage();
			}
		});
		group.add(rbmiCompilePython);
		mntmNewMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_MASK));

		JMenu mnNewMenu_1 = new JMenu("Common Tools");
		mnNewMenu_2.add(mnNewMenu_1);

		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Clicker");
		mntmNewMenuItem_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				taSource.setText(new ClickerTool().getSource(Recorder.JAVA_LANGUAGE));
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_1);

		JMenuItem mntmNewMenuItem_2 = new JMenuItem("Area Clicker");
		mntmNewMenuItem_2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				taSource.setText(new AreaClickerTool().getSource(Recorder.JAVA_LANGUAGE));
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_2);

		JMenu mSetting = new JMenu("Setting");
		JMenuItem miSetReplayHotkey = new JMenuItem("Hotkeys");
		miSetReplayHotkey.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hotkey.setVisible(true);
			}
		});
		mSetting.add(miSetReplayHotkey);

		JMenuItem miClassPath = new JMenuItem("Set compiler path");
		miClassPath.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (rbmiCompileJava.isSelected()) {
					JFileChooser chooser = new JFileChooser("Java home");
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					if (chooser.showDialog(MainFrame.this, "Set Java home") == JFileChooser.APPROVE_OPTION) {
						backEnd.config.compilerFactory().getCompiler("java").setPath(chooser.getSelectedFile());
					}
				} else if (rbmiCompilePython.isSelected()) {
					JFileChooser chooser = new JFileChooser("Python interpreter");
					chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					if (chooser.showDialog(MainFrame.this, "Set Python interpreter") == JFileChooser.APPROVE_OPTION) {
						backEnd.config.compilerFactory().getCompiler("python").setPath(chooser.getSelectedFile());
					}
				}

			}
		});
		mSetting.add(miClassPath);

		menuBar.add(mSetting);

		final JCheckBoxMenuItem chckbxmntmNewCheckItem = new JCheckBoxMenuItem("Record Mouse Click Only");
		mSetting.add(chckbxmntmNewCheckItem);
		chckbxmntmNewCheckItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (chckbxmntmNewCheckItem.isSelected()) {
					backEnd.recorder.setRecordMode(Recorder.MODE_MOUSE_CLICK_ONLY);
				} else {
					backEnd.recorder.setRecordMode(Recorder.MODE_NORMAL);
				}
			}
		});

		/*************************************************************************************/
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		tfMousePosition = new JTextField();
		tfMousePosition.setColumns(10);
		tfMousePosition.setEditable(false);

		backEnd.executor.scheduleWithFixedDelay(new Runnable(){
			@Override
			public void run() {
				Point p = backEnd.core.mouse().getPosition();
				tfMousePosition.setText(p.x + ", " + p.y);
			}
		}, 0, 200, TimeUnit.MILLISECONDS);

		bRecord = new JButton("Record");
		bRecord.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.switchRecord();
			}
		});

		bReplay = new JButton("Replay");
		bReplay.setEnabled(false);
		bReplay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.switchReplay();
			}
		});

		bCompile = new JButton("Compile source");
		bCompile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.compileSource();
			}
		});

		JLabel lblNewLabel = new JLabel("Repeat");

		tfRepeatCount = new JTextField("1");
		tfRepeatCount.setEnabled(false);
		tfRepeatCount.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("with delay each time");

		tfRepeatDelay = new JTextField("0");
		tfRepeatDelay.setEnabled(false);
		tfRepeatDelay.setColumns(10);

		JLabel lblNewLabel_2 = new JLabel("ms");

		JScrollPane scrollPane = new JScrollPane();

		JScrollPane scrollPane_1 = new JScrollPane();

		bRun = new JButton("Run Compiled Action");
		bRun.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.switchRunningCompiledAction();
			}
		});

		JLabel lblNewLabel_3 = new JLabel("Mouse position");

		JScrollPane scrollPane_2 = new JScrollPane();

		JButton bAddTask = new JButton("Add task");
		bAddTask.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.addCurrentTask();
			}
		});

		JButton bRemoveTask = new JButton("Remove task");
		bRemoveTask.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.removeCurrentTask();
			}
		});

		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(bReplay, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(bRecord, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(lblNewLabel)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(tfRepeatCount, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(lblNewLabel_1)
									.addGap(17)
									.addComponent(tfRepeatDelay, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(lblNewLabel_2))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGap(10)
									.addComponent(lblNewLabel_3)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(tfMousePosition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
						.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.UNRELATED))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(bCompile)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(bRun)
									.addGap(32)))
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(bAddTask)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(bRemoveTask))
								.addGroup(Alignment.TRAILING, gl_contentPane.createParallelGroup(Alignment.LEADING)
									.addGroup(gl_contentPane.createSequentialGroup()
										.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
										.addPreferredGap(ComponentPlacement.RELATED))
									.addGroup(gl_contentPane.createSequentialGroup()
										.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
										.addGap(1))))))
					.addGap(7))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(bRecord)
						.addComponent(lblNewLabel_3)
						.addComponent(tfMousePosition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(bReplay)
						.addComponent(lblNewLabel)
						.addComponent(tfRepeatCount, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_2)
						.addComponent(tfRepeatDelay, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_1))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(bCompile)
						.addComponent(bRun)
						.addComponent(bAddTask)
						.addComponent(bRemoveTask))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
							.addGap(11)
							.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE))
						.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE))
					.addContainerGap())
		);

		tTasks = new JTable();
		tTasks.setRowSelectionAllowed(false);
		tTasks.setColumnSelectionAllowed(false);
		tTasks.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Name", "Key chain"
			}
		){
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
	            return columnIndex != 1;
	        }
		});

		tTasks.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				backEnd.keyReleaseTaskTable(e);
			}
		});

		tTasks.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				backEnd.mouseReleaseTaskTable(e);
			}
		});

		DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
		centerRender.setHorizontalAlignment(SwingConstants.CENTER);
		tTasks.getColumnModel().getColumn(1).setCellRenderer(centerRender);

		scrollPane_2.setViewportView(tTasks);

		taSource.setTabSize(1);
		backEnd.promptSource();
		taSource.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (taSource.getText().length() == 0) {
					backEnd.promptSource();
				}
			}
		});

		scrollPane.setViewportView(taSource);
		contentPane.setLayout(gl_contentPane);
		taStatus.setEditable(false);

		scrollPane_1.setViewportView(taStatus);

		/*************************************************************************************/
		backEnd.renderTasks();

		/*************************************************************************************/
		LOGGER.info("Successfully intialized application");
	}

	private void refreshCompilingLanguage() {
		backEnd.customFunction = null;
		if (rbmiCompileJava.isSelected()) {
			bCompile.setText("Compile source");
		} else if (rbmiCompilePython.isSelected()) {
			bCompile.setText("Load source");
			JOptionPane.showMessageDialog(MainFrame.this, "Using python interpreter at "
					+ backEnd.config.compilerFactory().getCompiler("python").getPath().getAbsolutePath(),
					"Python interpreter not chosen", JOptionPane.OK_OPTION);
		}

		backEnd.promptSource();
	}
}
