package main;

import core.cli.MainCli;
import frontEnd.MainFrontEnd;

public class Main {

	public static void main(String[] args) {
		if (args.length == 0) {
			MainFrontEnd.run();
		} else {
			MainCli cli = new MainCli();
			cli.process(args);
		}
	}
}
