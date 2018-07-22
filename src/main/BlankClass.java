package main;

import utilities.logging.LogHolder;

public class BlankClass {
	public static void main(String[] args) throws InterruptedException {
		long time = System.currentTimeMillis();

		LogHolder h = new LogHolder();
		insert(h, '0');
		insert(h, '1');
		insert(h, '\n');
		insert(h, '2');
		insert(h, '3');
		insert(h, '4');
		insert(h, '5');
		insert(h, '6');
		insert(h, '7');
		insert(h, '\n');
		insert(h, '8');
		insert(h, '9');
		insert(h, '0');

		System.out.println("__________________________");
		System.out.println(h.getContentSince(time).replaceAll("\n", "X"));
	}

	private static void insert(LogHolder h, char c) throws InterruptedException {
		h.write(c);
		Thread.sleep(50);
	}
}
