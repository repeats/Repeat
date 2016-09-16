package frontEnd;

import java.awt.AWTException;
import java.awt.SystemTray;
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
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
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

import staticResources.BootStrapResources;
import utilities.FileUtility;
import utilities.swing.LinedTextArea;
import utilities.swing.SwingUtil;

import commonTools.AreaClickerTool;
import commonTools.ClickerTool;

import core.languageHandler.Language;
import core.recorder.Recorder;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {


	private static final Logger LOGGER = Logger.getLogger(MainFrame.class.getName());
	protected static final int TTASK_COLUMN_TASK_NAME = 0;
	protected static final int TTASK_COLUMN_TASK_HOTKEY = 1;
	protected static final int TTASK_COLUMN_ENABLED = 2;
	protected static final int TTASK_COLUMN_USE_COUNT = 3;
	protected static final int TTASK_COLUMN_LAST_USE = 4;

	protected final MainBackEndHolder backEnd;

	protected TrayIcon trayIcon;

	protected HotkeySetting hotkey;
	protected TaskGroupFrame taskGroup;
	protected IpcFrame ipcs;

	protected JLabel lSecondsSaved;
	private final JPanel contentPane;
	protected final JTextField tfRepeatCount;
	protected final JTextField tfRepeatDelay;
	protected JButton bRecord, bReplay, bCompile, bRun, bTaskGroup;
	protected JTextArea taSource, taStatus;
	protected JRadioButtonMenuItem rbmiCompileJava, rbmiCompilePython, rbmiCompileCS, rbmiCompileScala;
	protected JRadioButtonMenuItem rbmiDebugSevere, rbmiDebugWarning, rbmiDebugInfo, rbmiDebugFine;
	protected JCheckBoxMenuItem cbmiUseTrayIcon, cbmiHaltByKey, cbmiExecuteOnReleased;
	protected final JTextField tfMousePosition;
	protected final JTable tTasks;

	protected final Map<Language, JRadioButtonMenuItem> languageSelection;
	protected final Map<JRadioButtonMenuItem, Language> rbmiSelection;

	/**
	 * Create the frame.
	 * @throws NativeHookException
	 * @throws IOException
	 */
	public MainFrame() throws IOException {
		setTitle("Repeat");
		setIconImage(BootStrapResources.TRAY_IMAGE);
		languageSelection = new HashMap<>();
		rbmiSelection = new HashMap<>();

		backEnd = new MainBackEndHolder(this);
		if (!SystemTray.isSupported()) {
			LOGGER.warning("System tray is not supported!");
			trayIcon = null;
		} else {
			trayIcon = new MinimizedFrame(BootStrapResources.TRAY_IMAGE, backEnd);
		}

		hotkey = new HotkeySetting(backEnd);
		taskGroup = new TaskGroupFrame(backEnd);
		ipcs = new IpcFrame(backEnd);

		/*************************************************************************************/
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 830, 412);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				backEnd.exit();
			}

			@Override
			public void windowIconified(WindowEvent e) {
				if (trayIcon == null || !backEnd.config.isUseTrayIcon()) {
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

		addWindowFocusListener(new WindowFocusListener() {
			@Override
			public void windowLostFocus(WindowEvent e) {
			}

			@Override
			public void windowGainedFocus(WindowEvent e) {
				backEnd.focusMainFrame();
			}
		});

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);

		JMenuItem miForceExit = new JMenuItem("Force exit");
		miForceExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(MainFrame.this,
						"This will not save configuration. Do you really want to exit?", "Force exit",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
			}
		});

		JMenuItem miExit = new JMenuItem("Exit");
		miExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.exit();
			}
		});

		taSource = new JTextArea();

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

		JMenuItem miCleanSource = new JMenuItem("Clean unused source...");
		miCleanSource.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.cleanUnusedSource();
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
					backEnd.loadConfig(chooser.getSelectedFile());
				}
			}
		});

		JMenuItem miSaveConfig = new JMenuItem("Save config");
		miSaveConfig.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (backEnd.config.writeConfig()) {
					JOptionPane.showMessageDialog(MainFrame.this, "Configuration saved successfully");
				} else {
					JOptionPane.showMessageDialog(MainFrame.this, "Failed to savd configuration");
				}
			}
		});

		mnNewMenu.add(miLoadConfig);
		mnNewMenu.add(miSaveConfig);

		JSeparator separator_2 = new JSeparator();
		mnNewMenu.add(separator_2);
		mnNewMenu.add(miLoadSource);
		mnNewMenu.add(miSaveSource);
		mnNewMenu.add(miCleanSource);

		JMenuItem mntmNewMenuItem_4 = new JMenuItem("Import...");
		mntmNewMenuItem_4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnVal = chooser.showOpenDialog(MainFrame.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					backEnd.importTasks(file);
				}
			}
		});
		mnNewMenu.add(mntmNewMenuItem_4);

		JMenuItem mntmNewMenuItem_5 = new JMenuItem("Export...");
		mntmNewMenuItem_5.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = chooser.showOpenDialog(MainFrame.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					backEnd.exportTasks(file);
				}
			}
		});
		mnNewMenu.add(mntmNewMenuItem_5);

		JSeparator separator_1 = new JSeparator();
		mnNewMenu.add(separator_1);
		mnNewMenu.add(miForceExit);
		mnNewMenu.add(miExit);

		ButtonGroup bGroupLanguage = new ButtonGroup();
		ButtonGroup bGroupDebugLevel = new ButtonGroup();

		JMenu mnNewMenu_2 = new JMenu("Tool");
		menuBar.add(mnNewMenu_2);

		JMenuItem mntmNewMenuItem = new JMenuItem("Generate source");
		mntmNewMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK));
		mntmNewMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.generateSource();
			}
		});

		JMenuItem miHaltAllTasks = new JMenuItem("Halt all tasks");
		miHaltAllTasks.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.haltAllTasks();
			}
		});
		mnNewMenu_2.add(miHaltAllTasks);
		mnNewMenu_2.add(mntmNewMenuItem);

		JMenuItem mntmNewMenuItem_3 = new JMenuItem("Native modules...");
		mntmNewMenuItem_3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainFrame.this.ipcs.ipcBackEndHolder.renderServices();
				MainFrame.this.ipcs.setVisible(true);
			}
		});
		mnNewMenu_2.add(mntmNewMenuItem_3);

		JMenu mnNewMenu_3 = new JMenu("Compiling Language");
		mnNewMenu_2.add(mnNewMenu_3);

		rbmiCompileJava = new JRadioButtonMenuItem(Language.JAVA.toString());
		mnNewMenu_3.add(rbmiCompileJava);
		rbmiCompileJava.setSelected(true);
		rbmiCompileJava.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.refreshCompilingLanguage();
			}
		});
		bGroupLanguage.add(rbmiCompileJava);

		rbmiCompilePython = new JRadioButtonMenuItem(Language.PYTHON.toString());
		mnNewMenu_3.add(rbmiCompilePython);
		rbmiCompilePython.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.refreshCompilingLanguage();
			}
		});
		bGroupLanguage.add(rbmiCompilePython);

		rbmiCompileCS = new JRadioButtonMenuItem("C#");
		rbmiCompileCS.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.refreshCompilingLanguage();
			}
		});
		mnNewMenu_3.add(rbmiCompileCS);
		bGroupLanguage.add(rbmiCompileCS);

		rbmiCompileScala = new JRadioButtonMenuItem("Scala");
		rbmiCompileScala.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.refreshCompilingLanguage();
			}
		});
		mnNewMenu_3.add(rbmiCompileScala);
		bGroupLanguage.add(rbmiCompileScala);

		languageSelection.put(Language.JAVA, rbmiCompileJava);
		languageSelection.put(Language.PYTHON, rbmiCompilePython);
		languageSelection.put(Language.CSHARP, rbmiCompileCS);
		languageSelection.put(Language.SCALA, rbmiCompileScala);

		rbmiSelection.put(rbmiCompileJava, Language.JAVA);
		rbmiSelection.put(rbmiCompilePython, Language.PYTHON);
		rbmiSelection.put(rbmiCompileCS, Language.CSHARP);
		rbmiSelection.put(rbmiCompileScala, Language.SCALA);

		mntmNewMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_MASK));

		JMenu mnNewMenu_1 = new JMenu("Common Tools");
		mnNewMenu_2.add(mnNewMenu_1);

		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Clicker");
		mntmNewMenuItem_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				taSource.setText(new ClickerTool().getSource(Language.JAVA));
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_1);

		JMenuItem mntmNewMenuItem_2 = new JMenuItem("Area Clicker");
		mntmNewMenuItem_2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				taSource.setText(new AreaClickerTool().getSource(Language.JAVA));
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_2);

		JMenu mSetting = new JMenu("Setting");
		JMenuItem miSetReplayHotkey = new JMenuItem("Hotkeys...");
		miSetReplayHotkey.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hotkey.setVisible(true);
			}
		});
		mSetting.add(miSetReplayHotkey);

		JMenuItem miClassPath = new JMenuItem("Set compiler path...");
		miClassPath.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.changeCompilerPath();
			}
		});
		mSetting.add(miClassPath);

		menuBar.add(mSetting);

		final JCheckBoxMenuItem chckbxmntmNewCheckItem = new JCheckBoxMenuItem("Record Mouse Click Only");
		mSetting.add(chckbxmntmNewCheckItem);

		cbmiHaltByKey = new JCheckBoxMenuItem("Halt task by ESC");
		cbmiHaltByKey.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.switchHaltByKey();
			}
		});
		mSetting.add(cbmiHaltByKey);

		cbmiExecuteOnReleased = new JCheckBoxMenuItem("Execute on released");
		cbmiExecuteOnReleased.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.switchExecuteOnReleased();
			}
		});
		mSetting.add(cbmiExecuteOnReleased);

		JSeparator separator = new JSeparator();
		mSetting.add(separator);

		JMenu mnNewMenu_4 = new JMenu("Debug Level");
		mSetting.add(mnNewMenu_4);

		rbmiDebugSevere = new JRadioButtonMenuItem("SEVERE");
		rbmiDebugSevere.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.changeDebugLevel();
			}
		});
		rbmiDebugSevere.setSelected(true);
		mnNewMenu_4.add(rbmiDebugSevere);

		rbmiDebugWarning = new JRadioButtonMenuItem("WARNING");
		rbmiDebugWarning.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.changeDebugLevel();
			}
		});
		mnNewMenu_4.add(rbmiDebugWarning);

		rbmiDebugInfo = new JRadioButtonMenuItem("INFO");
		rbmiDebugInfo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.changeDebugLevel();
			}
		});
		mnNewMenu_4.add(rbmiDebugInfo);

		rbmiDebugFine = new JRadioButtonMenuItem("FINE");
		rbmiDebugFine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.changeDebugLevel();
			}
		});
		mnNewMenu_4.add(rbmiDebugFine);

		bGroupDebugLevel.add(rbmiDebugSevere);
		bGroupDebugLevel.add(rbmiDebugWarning);
		bGroupDebugLevel.add(rbmiDebugInfo);
		bGroupDebugLevel.add(rbmiDebugFine);

		cbmiUseTrayIcon = new JCheckBoxMenuItem("Use tray icon");
		cbmiUseTrayIcon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.switchTrayIconUse();
			}
		});
		mSetting.add(cbmiUseTrayIcon);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem miAPI = new JMenuItem("API");
		miAPI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Language selected = backEnd.getSelectedLanguage();
				SwingUtil.OptionPaneUtil.showString(selected.toString() + " API", BootStrapResources.getAPI(selected));
			}
		});

		JMenuItem miAbout = new JMenuItem("About");
		miAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtil.OptionPaneUtil.showString("About", BootStrapResources.getAbout());
			}
		});

		mnHelp.add(miAPI);
		mnHelp.add(miAbout);

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



		bRecord = new JButton();
		bRecord.setIcon(BootStrapResources.RECORD);
		bRecord.setToolTipText("Record / Stop Recording");
		bRecord.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.switchRecord();
			}
		});

		bReplay = new JButton();
		bReplay.setIcon(BootStrapResources.PLAY);
		bReplay.setToolTipText("Replay / Stop Replay");
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

		JScrollPane scrollPane = new LinedTextArea(taSource);

		bRun = new JButton("Run Compiled Action");
		bRun.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.switchRunningCompiledAction();
			}
		});

		JLabel lblNewLabel_3 = new JLabel("Mouse position");

		JScrollPane scrollPane_2 = new JScrollPane();

		JButton bAddTask = new JButton();
		bAddTask.setIcon(BootStrapResources.ADD);
		bAddTask.setToolTipText("Add most recently compiled task");
		bAddTask.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.addCurrentTask();
			}
		});

		JButton bRemoveTask = new JButton();
		bRemoveTask.setIcon(BootStrapResources.DELETE);
		bRemoveTask.setToolTipText("Delete currently selected task from the list");
		bRemoveTask.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.removeCurrentTask();
			}
		});

		JButton bMoveTaskUp = new JButton();
		bMoveTaskUp.setIcon(BootStrapResources.UP);
		bMoveTaskUp.setToolTipText("Move currently selected task up in the list");
		bMoveTaskUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.moveTaskUp();
			}
		});

		JButton bMoveTaskDown = new JButton();
		bMoveTaskDown.setIcon(BootStrapResources.DOWN);
		bMoveTaskDown.setToolTipText("Move currently selected task down in the list");
		bMoveTaskDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.moveTaskDown();
			}
		});

		JButton bModifyTask = new JButton();
		bModifyTask.setIcon(BootStrapResources.EDIT);
		bModifyTask.setToolTipText("Override the current task with the most recently compiled task");
		bModifyTask.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.overrideTask();
			}
		});

		bTaskGroup = new JButton("Global");
		bTaskGroup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				taskGroup.setVisible(true);
			}
		});

		JLabel lblNewLabel_4 = new JLabel("Task group");

		JButton bMoveTask = new JButton();
		bMoveTask.setIcon(BootStrapResources.MOVE);
		bMoveTask.setToolTipText("Move this task to another task group.");
		bMoveTask.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backEnd.changeTaskGroup();
			}
		});

		JLabel lblNewLabel_5 = new JLabel("Execution time (s)");

		lSecondsSaved = new JLabel("0");

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(bReplay, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(bRecord, Alignment.LEADING))
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGap(10)
									.addComponent(lblNewLabel_3)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(tfMousePosition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
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
									.addComponent(lblNewLabel_2)))
							.addPreferredGap(ComponentPlacement.RELATED, 230, Short.MAX_VALUE)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblNewLabel_5)
									.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(lSecondsSaved)
									.addGap(48))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblNewLabel_4)
									.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(bTaskGroup, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED))))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.UNRELATED))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(bCompile)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(bRun)
									.addGap(32)))
							.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(bAddTask, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(bModifyTask, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(bRemoveTask, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(bMoveTaskUp, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(bMoveTaskDown, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(bMoveTask, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
									.addGap(94))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
										.addComponent(tabbedPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
										.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE))
									.addGap(1)))))
					.addGap(7))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(bRecord)
								.addComponent(lblNewLabel_3)
								.addComponent(tfMousePosition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(bTaskGroup)
								.addComponent(lblNewLabel_4))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
										.addComponent(bReplay)
										.addComponent(lblNewLabel)
										.addComponent(tfRepeatCount, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(tfRepeatDelay, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblNewLabel_1)
										.addComponent(lblNewLabel_2)
										.addComponent(lSecondsSaved)
										.addComponent(lblNewLabel_5))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
										.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
											.addComponent(bCompile)
											.addComponent(bRun)
											.addComponent(bAddTask))
										.addComponent(bModifyTask, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
										.addComponent(bRemoveTask)))
								.addComponent(bMoveTask)))
						.addComponent(bMoveTaskUp)
						.addComponent(bMoveTaskDown))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
							.addGap(11)
							.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE))
						.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE))
					.addContainerGap())
		);
		taStatus = new JTextArea();
		final LogPopupMenu mainLogPopupMenu = new LogPopupMenu(taStatus);
		taStatus.setComponentPopupMenu(mainLogPopupMenu);

		JScrollPane scrollPane_1 = new JScrollPane();
		tabbedPane.addTab("Main log", null, scrollPane_1, null);
		taStatus.setEditable(false);

		scrollPane_1.setViewportView(taStatus);

		tTasks = new JTable();
		tTasks.setColumnSelectionAllowed(false);
		tTasks.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Name", "Activation", "Enabled", "Use count", "Last used"
			}
		){
			@Override
		    public boolean isCellEditable(int row, int column) {
		        return column == 0;
		    }
		});

		DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
		centerRender.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0 ; i < tTasks.getColumnCount(); i++) {
			tTasks.getColumnModel().getColumn(i).setCellRenderer(centerRender);
		}

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

		scrollPane_2.setViewportView(tTasks);

		taSource.setTabSize(1);
		backEnd.promptSource();
		taSource.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (taSource.getText().trim().length() == 0) {
					backEnd.promptSource();
				}
			}
		});

		contentPane.setLayout(gl_contentPane);
	}
}
