package frontEnd;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import core.Core;

public class Main extends JFrame {

	private static final long serialVersionUID = -5327694524482632347L;

	private ScheduledThreadPoolExecutor sc;

	private JTextField tf;
	private JButton b;
	private Runnable t;
	private Core c;

	public static void main(String[] args) {
		new Main().setVisible(true);
	}

	public Main() {
		sc = new ScheduledThreadPoolExecutor(10);
		b = new JButton("Go");
		c = new Core();

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(300, 200);
		setLocation(300, 300);
		tf = new JTextField(10);

		JPanel panel = new JPanel();
		panel.add(tf);
		panel.add(b);
		add(panel);

		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				c.mouse().moveBy(-50, 0);
				c.mouse().leftClick();
				c.keyBoard().type(KeyEvent.VK_BACK_SPACE);
			}
		});
	}
}
