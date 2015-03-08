package frontEnd;

import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.PrintStream;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import org.jnativehook.NativeHookException;

import utilities.ExceptionUtility;
import utilities.FileUtility;
import utilities.NumberUtility;
import utilities.OutStream;

import commonTools.AreaClickerTool;
import commonTools.ClickerTool;

import core.Recorder;
import core.UserDefinedAction;

public class Main extends JFrame {

	private static final long serialVersionUID = 2804146302677040692L;

	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

	static {
		LOGGER.setLevel(Level.ALL);
	}

	private final BackEndHolder backEnd;

	protected HotkeySetting hotkey;
	private final JPanel contentPane;
	protected final JTextField tfRepeatCount;
	protected final JTextField tfRepeatDelay;
	protected JButton bRecord, bReplay, bCompile, bRun;
	protected JTextArea taSource, taStatus;
	protected JRadioButtonMenuItem rbmiCompileJava, rbmiCompilePython;
	private JTextField tfMousePosition;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, ExceptionUtility.getStackTrace(e));
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws NativeHookException
	 */
	public Main() throws NativeHookException {
		backEnd = new BackEndHolder(this);
		hotkey = new HotkeySetting();
		/*************************************************************************************/
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 590, 327);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);

		JMenuItem miExit = new JMenuItem("Exit");
		miExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_MASK));
		miExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
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

		JMenuItem miLoadSource = new JMenuItem("Load Source");
		miLoadSource.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK));
		miLoadSource.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnVal = chooser.showOpenDialog(Main.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					taSource.setText(FileUtility.readFromFile(file).toString());
				}
			}
		});

		JMenuItem miSaveSource = new JMenuItem("Save Source");
		miSaveSource.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		miSaveSource.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnVal = chooser.showOpenDialog(Main.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					FileUtility.writeToFile(new StringBuffer(taSource.getText()), file, false);
				}
			}
		});

		mnNewMenu.add(miLoadSource);
		mnNewMenu.add(miSaveSource);
		mnNewMenu.add(miExit);

		ButtonGroup group = new ButtonGroup();

		JMenu mnNewMenu_2 = new JMenu("Tool");
		menuBar.add(mnNewMenu_2);

		JMenuItem mntmNewMenuItem = new JMenuItem("Generate source (Java only)");
		mntmNewMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK));
		mntmNewMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (rbmiCompileJava.isSelected()) {
					taSource.setText(backEnd.recorder.getGeneratedCode(Recorder.JAVA_LANGUAGE));
				} else if (rbmiCompilePython.isSelected()) {

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
//		mnNewMenu_3.add(rbmiCompilePython);
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

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		tfMousePosition = new JTextField();
		tfMousePosition.setColumns(10);
		tfMousePosition.setEditable(false);

		backEnd.mouseTracker = backEnd.executor.scheduleWithFixedDelay(new Runnable(){
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
				if (!backEnd.isRecording) {//Start record
					try {
						backEnd.recorder.clear();
						backEnd.recorder.record();
						backEnd.isRecording = true;
						bRecord.setText("Stop");

						setEnableReplay(false);
					} catch (NativeHookException e1) {
						LOGGER.log(Level.WARNING, e1.getMessage());
					}
				} else {//Stop record
					try {
						backEnd.recorder.stopRecord();
						backEnd.isRecording = false;
						bRecord.setText("Record");

						setEnableReplay(true);
					} catch (NativeHookException e1) {
						LOGGER.log(Level.WARNING, e1.getMessage());
					}
				}
			}
		});

		bReplay = new JButton("Replay");
		bReplay.setEnabled(false);
		bReplay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!backEnd.isReplaying) {//Start replay
					if (!NumberUtility.isInteger(tfRepeatCount.getText()) || Integer.parseInt(tfRepeatCount.getText()) < 1) {
						JOptionPane.showMessageDialog(null, "Repeat count is not positive integer", "Error", JOptionPane.OK_OPTION);
						return;
					} else if (!NumberUtility.isInteger(tfRepeatDelay.getText()) || Integer.parseInt(tfRepeatDelay.getText()) < 0) {
						JOptionPane.showMessageDialog(null, "Repeat delay is not positive integer", "Error", JOptionPane.OK_OPTION);
						return;
					}

					backEnd.isReplaying = true;
					bReplay.setText("Stop");

					setEnableRecord(false);
					final int count = Integer.parseInt(tfRepeatCount.getText());

					backEnd.executor.schedule(new Runnable() {
						@Override
						public void run() {
							for (int i = 0; i < count; i++) {
								backEnd.recorder.replay();
							}

							bReplay.doClick();
						}
					}, 0, TimeUnit.MILLISECONDS);
				} else {//Stop replay
					backEnd.isReplaying = false;
					bReplay.setText("Replay");

					setEnableRecord(true);
					backEnd.recorder.stopReplay();
				}
			}
		});

		bCompile = new JButton("Compile source");
		bCompile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String source = taSource.getText();

				UserDefinedAction createdInstance = backEnd.getCompiler().compile(source);

				if (createdInstance != null) {
					backEnd.customFunction = createdInstance;
				}
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
				if (backEnd.isRunning) {//Stop it
					backEnd.isRunning = false;
					if (backEnd.compiledExecutor != null) {
						while (backEnd.compiledExecutor.isAlive()) {
							backEnd.compiledExecutor.interrupt();
						}
					}
					bRun.setText("Run Compiled Action");
				} else {//Run it
					if (backEnd.customFunction != null) {
						backEnd.isRunning = true;

						backEnd.compiledExecutor = new Thread(new Runnable() {
						    @Override
							public void run() {
						    	backEnd.customFunction.executeAction(backEnd.core);
						    }
						});
						backEnd.compiledExecutor.start();
					}
				}
			}
		});

		JLabel lblNewLabel_3 = new JLabel("Mouse position");

		backEnd.startGlobalHotkey();

		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(bCompile)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(bRun))
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
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)))
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
						.addComponent(bRun))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
							.addGap(1)))
					.addContainerGap())
		);

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
		PrintStream printStream = new PrintStream(new OutStream(taStatus));
		System.setOut(printStream);
		System.setErr(printStream);
	}

	private void setEnableRecord(boolean state) {
		bRecord.setEnabled(state);
	}

	private void setEnableReplay(boolean state) {
		bReplay.setEnabled(state);
		tfRepeatCount.setEnabled(state);
		tfRepeatDelay.setEnabled(state);

		if (state) {
			tfRepeatCount.setText("1");
			tfRepeatDelay.setText("0");
		}
	}

	private void refreshCompilingLanguage() {
		backEnd.customFunction = null;
		if (rbmiCompileJava.isSelected()) {
			bCompile.setText("Compile source");
		} else if (rbmiCompilePython.isSelected()) {
			bCompile.setText("Load source");
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			if (chooser.showDialog(Main.this, "Set Python Interpreter") == JFileChooser.APPROVE_OPTION) {
				File chosen = chooser.getSelectedFile();
				backEnd.pythonCompiler.setInterpreter(chosen);
			} else {
				JOptionPane.showMessageDialog(Main.this, "Using python interpreter at " + backEnd.pythonCompiler.getInterpreter().getAbsolutePath(),
						"Python interpreter not chosen", JOptionPane.OK_OPTION);
			}
		}

		backEnd.promptSource();
	}
}
