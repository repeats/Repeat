package frontEnd;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jnativehook.NativeHookException;

import core.Core;
import core.Recorder;

public class Main extends JFrame {

	private static final long serialVersionUID = -5327694524482632347L;

	private ScheduledThreadPoolExecutor sc;

	private JTextField tf;
	private JButton b, b1, b2;
	private Runnable t;
	private Core c;

	public static void main(String[] args) {
		new Main().setVisible(true);
	}

	public Main() {
		sc = new ScheduledThreadPoolExecutor(10);
		b = new JButton("Go");
		b1 = new JButton("Stop");
		b2 = new JButton("Rep");
		c = new Core();

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(300, 200);
		setLocation(300, 300);
		tf = new JTextField(10);

		JPanel main = new JPanel();
		JPanel panel = new JPanel();
		panel.add(tf);

		JPanel bPanel = new JPanel();
		bPanel.setLayout(new BoxLayout(bPanel, BoxLayout.Y_AXIS));
		bPanel.add(b);
		bPanel.add(b1);
		bPanel.add(b2);

		main.add(panel);
		main.add(bPanel);
		add(main);

		final Recorder r = new Recorder(c);

		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					r.clear();
					r.record();
				} catch (NativeHookException e) {
					e.printStackTrace();
				}
			}
		});

		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					r.stopRecord();
				} catch (NativeHookException e) {
					e.printStackTrace();
				}
			}
		});

		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				r.replay();
			}
		});
	}
}
