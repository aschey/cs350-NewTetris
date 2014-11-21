/**
 * CS 350
 * Project #6
 * Austin Schey
 * MinoTest.java: Creates a MinoPanel and displays it
 */

import javax.swing.JFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MinoTest {
	public static void main(String[] args) {
		final MinoServer ms = new MinoServer();
		final MinoClient mc = new MinoClient("127.0.0.1");

		Thread thread1 = new Thread(ms);
		JFrame app1 = new JFrame("Server");
		app1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app1.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				mc.closeConnection();
				ms.closeConnection();
			}
		});
		app1.add(ms);
		app1.pack();
		app1.setSize(600, 500);
		app1.setVisible(true);

		Thread thread2 = new Thread(mc);
		JFrame app2 = new JFrame("Client");
		app2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app2.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				mc.closeConnection();
				ms.closeConnection();
			}
		});
		app2.add(mc);
		app2.pack();
		app2.setSize(600, 500);
		app2.setLocation(600, 0);
		app2.setVisible(true);

		thread1.start();
		thread2.start();
	}
}
