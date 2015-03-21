package cli;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import utilities.FileUtility;
import utilities.Function;
import utilities.NumberUtility;
import core.UserDefinedAction;
import core.controller.Core;
import core.languageHandler.DynamicJavaCompiler;
import core.languageHandler.JavaSourceGenerator;

public class CommandLineInterface {
	public static void main(String[] args) {
		final Core core = new Core();

		TerminalState mouseMove = new TerminalState() {
			@Override
			protected boolean execute(List<Object> parsed) {
				core.mouse().move((int) parsed.get(2), (int) parsed.get(3));
				return true;
			}
		};

		TerminalState mouseMoveBy = new TerminalState() {
			@Override
			protected boolean execute(List<Object> parsed) {
				core.mouse().moveBy((int) parsed.get(2), (int) parsed.get(3));
				return true;
			}
		};

		TerminalState mouseClick = new TerminalState() {
			@Override
			protected boolean execute(List<Object> parsed) {
				try {
					core.mouse().click((int) parsed.get(2));
				} catch (InterruptedException e) {
					e.printStackTrace();
					//Shouldn't be interrupted here though
				}
				return true;
			}
		};

		TerminalState mousePress = new TerminalState() {
			@Override
			protected boolean execute(List<Object> parsed) {
				core.mouse().press((int) parsed.get(2));
				return true;
			}
		};

		TerminalState mouseRelease = new TerminalState() {
			@Override
			protected boolean execute(List<Object> parsed) {
				core.mouse().release((int) parsed.get(2));
				return true;
			}
		};

		TerminalState keyType = new TerminalState() {
			@Override
			protected boolean execute(List<Object> parsed) {
				core.keyBoard().type((String) parsed.get(2));
				return true;
			}
		};

		TerminalState keyPress = new TerminalState() {
			@Override
			protected boolean execute(List<Object> parsed) {
				core.keyBoard().press((int) parsed.get(2));
				return true;
			}
		};

		TerminalState keyRelease = new TerminalState() {
			@Override
			protected boolean execute(List<Object> parsed) {
				core.keyBoard().release((int) parsed.get(2));
				return true;
			}
		};

		TerminalState wait = new TerminalState() {
			@Override
			protected boolean execute(List<Object> parsed) {
				int time = (int) parsed.get(1);
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}
		};

		TerminalState execFile = new TerminalState() {
			@Override
			protected boolean execute(List<Object> parsed) {
				File file = new File((String) parsed.get(1));
				final JavaSourceGenerator sourceGen = new JavaSourceGenerator();

				FileUtility.readFromFile(file, new Function<String, Boolean>() {
					@Override
					public Boolean apply(String r) {
						if (r.length() < 2) {
							return false;
						}

						String[] split = r.split(" ");
						if (split.length < 4) {
							System.out.println("Invalid command length < 4");
							System.out.println("Command is " + r);
							return false;
						}

						long time;
						if (NumberUtility.isInteger(split[0])) {
							time = Long.parseLong(split[0]);
						} else {
							System.out.println("Time (first var) must be integer");
							return false;
						}

						int[] params = new int[split.length - 3];
						for (int i = 0; i < params.length; i++) {
							if (NumberUtility.isInteger(split[i + 3])) {
								params[i] = Integer.parseInt(split[i + 3]);
							} else {
								System.out.println("Param " + i + " = " + split[i + 3] + " must be integer.");
								return false;
							}
						}

						if (!sourceGen.submitTask(time, split[1], split[2], params)) {
							System.out.println("Cannot submit task to interpreter");
							System.out.println("Full command is " + r);
							return false;
						}

						return true;
					}
				});

				DynamicJavaCompiler compiler = new DynamicJavaCompiler("CustomAction", new String[] { "core" }, new String[] {});
				UserDefinedAction action = compiler.compile(sourceGen.getSource());
				if (action != null) {
					try {
						action.action(new Core());
					} catch (InterruptedException e) {
						System.out.println("Executed ended prematurely...");
						return false;
					}
				} else {
					System.out.println("Error in compilation...");
					return false;
				}

				return true;
			}
		};

		/***********************************************************************************/
		/************************* Insert path ***********************************************/
		/***********************************************************************************/

		NonNegativeIntegerState a1 = new NonNegativeIntegerState(mouseMove);
		NonNegativeIntegerState a2 = new NonNegativeIntegerState(a1);

		NonNegativeIntegerState a3 = new NonNegativeIntegerState(mouseMoveBy);
		NonNegativeIntegerState a4 = new NonNegativeIntegerState(a3);

		NonNegativeIntegerState a5 = new NonNegativeIntegerState(mouseClick);
		NonNegativeIntegerState a6 = new NonNegativeIntegerState(mousePress);
		NonNegativeIntegerState a7 = new NonNegativeIntegerState(mouseRelease);

		HashMap<String, ParseState> mouseChoices = new HashMap<>();
		mouseChoices.put("move", a2);
		mouseChoices.put("moveBy", a4);
		mouseChoices.put("click", a5);
		mouseChoices.put("press", a6);
		mouseChoices.put("release", a7);
		ChoiceState mouse = new ChoiceState(mouseChoices);

		StringState a8 = new StringState(keyType);
		NonNegativeIntegerState a9 = new NonNegativeIntegerState(keyPress);
		NonNegativeIntegerState a10 = new NonNegativeIntegerState(keyRelease);

		HashMap<String, ParseState> keyChoices = new HashMap<>();
		mouseChoices.put("type", a8);
		mouseChoices.put("press", a9);
		mouseChoices.put("release", a10);
		ChoiceState key = new ChoiceState(keyChoices);

		StringState a11 = new StringState(execFile);

		HashMap<String, ParseState> startChoices = new HashMap<>();
		startChoices.put("mouse", mouse);
		startChoices.put("key", key);
		startChoices.put("wait", wait);
		startChoices.put("exec", a11);

		ChoiceState start = new ChoiceState(startChoices);

		/***********************************************************************************/
		/************************* Start parsing *********************************************/
		/***********************************************************************************/

		// String[] test = {"test.java", "mouse", "moveBy", "100", "0"};
		// args = test;

		ParseState current = start;
		List<Object> parsed = new LinkedList<>();

		int startIndex = 1;
		if (args.length > 0) {
			if (args[0].contains("Repeat")) {
				startIndex = 1;
			} else {
				startIndex = 0;
			}
		}

		for (int i = startIndex; i < args.length; i++) {
			ParseState next = current.parse(args[i], parsed);
			if (next == null) {
				if (current instanceof TerminalState) {
					((TerminalState) current).execute(parsed);
				} else {
					printHelp("Reached non-terminal state without any next state. Current token is " + args[i]);
					return;
				}
			} else {
				current = next;
			}
		}

		if (current instanceof TerminalState) {
			((TerminalState) current).execute(parsed);
		} else {
			printHelp("Reached non-terminal state without any input left.");
		}
	}

	private static final void printHelp(String additionalInfo) {
		if (additionalInfo != null) {
			System.out.println(additionalInfo);
		}
		System.out.println("Usage: java -jar RepeatCli.jar device command params");
		System.out.println("    mouse");
		System.out.println("          move x y <-- Move mouse to the coordinate (must be integers)");
		System.out.println("          moveBy x y <-- Move mouse by an amount (must be integer)");
		System.out.println("          click mouse_button <-- Click mouse button left=16, right=8");
		System.out.println("          press mouse_button");
		System.out.println("          release mouse_button");
		System.out.println("    key");
		System.out.println("          type string <-- Generate command for keyboard to type a string");
		System.out.println("          press key_code <-- Press a key on the keyboard");
		System.out.println("          release key_code");
		System.out.println("    wait time_milliseconds <-- blocking wait for an amount of time (must be integer)");
		System.out.println("    exec file_path <-- execute a file written in meta Repeat language)");
	}
}
