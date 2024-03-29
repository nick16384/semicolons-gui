package jfx.windowManager;

import java.awt.Toolkit;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import engine.sys;
import libraries.Err;
import libraries.OpenLib;
import libraries.VarLib;
import main.Main;

/**
 * Separate class, specific to running actions that happen on key events ENTER and UP in JFxWinloader.
 * @author nick16384
 *
 */

public class KeyEventHandlers {
	/**
	 * This piece of code is responsible for doing the work,
	 * when enter has been pressed (meaning a new command has been submitted).
	 */
	
	protected static void actionOnEnter() {
		main.Main.tabCountInRow = 0;
		//UPDATE SHELL STREAM ==============================================================================
		Main.ThreadAllocMain.getSWT().updateShellStream();
		//END UPDATE SHELL STREAM ==========================================================================
		//Splitting WindowMain.cmdLine text into command
		String[] lines = Main.cmdLine.getText().split("\n");
		
		String lastLine = lines[lines.length - 1];
		
		System.err.println("VarLib prompt: " + VarLib.getPrompt());
		System.err.println("Last line length: " + lastLine.length());
		System.err.println("Last line content: " + lastLine);
		System.err.println("Prompt length: " + VarLib.getPrompt().length());
		
		//Extract full command from last line (Remove prompt)
		//Dev. note: VarLib.getPrompt() contains ANSI excapes, but cmdLine.getText() doesn't, so
		//all ANSI escape chars had to be cleared out by the regex shown.
		String fullCommand = lastLine.substring(
				VarLib.getPrompt().replaceAll("\u001B\\[[\\d;]*[^\\d;]","").length(), lastLine.length());
		
		//if (fullCommand.contains(VarLib.getPrompt())) { fullCommand = fullCommand.split("\\$ ")[1]; }
		if (!fullCommand.isBlank()) {
			if (fullCommand.contains(" && ")) {
				sys.log("MAIN", 2, "Info: Found multiple commands connected with '&&'.");
				sys.log("MAIN", 2, "This is still experimental: Expect errors.");
				sys.shellPrintln("Using experimental command interconnect: '&&'");
				for (String subCommand : fullCommand.split(" && ")) {
					sys.log("MAIN", 0, "Running '" + fullCommand + "'");
					sys.log("Subcommand: " + subCommand);
					try {
						components.Command cmd = new components.Command(subCommand);
						cmd.start();
						sys.log("New thread started (subCommand placed into cmdQueue)");
						//For returnVal, try:
						//CommandMain.executeCommand(new components.Command(fullCommand));
					} catch (Exception ex) {
						//Error information is printed to stdout and shell
						Err.shellPrintErr(ex, "FATAL ERROR", "Non-caught JVM exception in class CmdMain");
					}
				}
			} else {
				sys.log("MAIN", 0, "Sending '" + fullCommand + "' to Command Parser");
				try {
					new components.Command(fullCommand).start();
					//For returnVal, try:
					//CommandMain.executeCommand(new components.Command(fullCommand));
				} catch (Exception ex) {
					Err.shellPrintErr(ex, "FATAL ERROR", "Non-caught JVM exception in class CmdMain");
				}
			}
			//=========================ADD FULLCMD TO HISTORY===============================
			main.Main.commandHistory.add(fullCommand);
			try {
				String history = Files.readString(Paths.get(
						VarLib.getDataDir().getAbsolutePath() + VarLib.fsep + "cmd_history"));
				int max_history_size = Integer.parseInt(Files.readString(Paths.get(
						VarLib.getDataDir().getAbsolutePath() + VarLib.fsep + "cmd_history_max_length")).trim());
				//Remove first entry of history until size of entries is below count in cmd_history_max_length
				while (history.split("\n").length > max_history_size) {
					Files.writeString(Paths.get(
							VarLib.getDataDir().getAbsolutePath() + VarLib.fsep + "cmd_history"),
							history.replaceFirst(history.split("\n")[0], ""),
							StandardOpenOption.WRITE);
				}
				Files.writeString(Paths.get(
						VarLib.getDataDir().getAbsolutePath() + VarLib.fsep + "cmd_history"),
						fullCommand + "\n", StandardOpenOption.APPEND);
			} catch (IOException ioe) {
				sys.log("MAIN", 2, "IOException while writing to cmd history.");
			} catch (NumberFormatException nfe) {
				sys.log("MAIN", 2, "Parsing cmd_history_max_length failed. Check file exists" +
						" and contains a number below 2.147.483.647");
			}
			//============================END ADD FULLCMD TO HISTORY==============================
		} else {
			OpenLib.cmdLinePrepare();
		}
	}
	
	/**
	 * handleCommandRepeat() is responsible for adding the last-executed command
	 * into the shell.
	 */
	protected static void handleCommandRepeat() {
		//========================================COMMAND REPEAT============================================
		main.Main.tabCountInRow++;
		OpenLib.cmdLinePrepare();
		
		main.Main.commandHistory.clear();
		try {
			//Add all entries of cmd_history to LinkedList main.Main.commandHistory
			main.Main.commandHistory.addAll(Arrays.asList(Files.readString(Paths.get(
					VarLib.getDataDir().getAbsolutePath() + VarLib.fsep + "cmd_history")).split("\n")));
		} catch (IOException ioe) {
			//TODO edit command history and TAB repeating further
			sys.log("MAIN", 3, "CMD History read fail. main.Main.commandHistory<LinkedList> is empty now.");
		}
		
		if (main.Main.tabCountInRow > main.Main.commandHistory.size()) {
			Toolkit.getDefaultToolkit().beep();
			sys.log("MAIN", 1, "Command history end reached");
		} else if (main.Main.tabCountInRow == 1) {
			//Write out last command without it getting protected (..., true)
			sys.log("REPEAT", 0, "Command repeat: "
					+ main.Main.commandHistory.get(main.Main.commandHistory.size() - main.Main.tabCountInRow));
			sys.shellPrint(1, "HIDDEN", main.Main.commandHistory.get(main.Main.commandHistory.size() - main.Main.tabCountInRow), true);
		} else {
			//TODO Find some sort of replaceLast() \/ -------------------
			/*try {
				WindowMain.cmdLine.getStyledDocument().remove(
						WindowMain.cmdLine.getText().indexOf(main.Main.commandHistory.get(main.Main.commandHistory.size() - main.Main.tabCountInRow + 1)),
						main.Main.commandHistory.get(main.Main.commandHistory.size() - main.Main.tabCountInRow + 1).length());
			} catch (BadLocationException ble) {
				OpenLib.logWrite("MAIN", 3, "Command repeat error: Could not remove old command");
			}*/
			sys.log("REPEAT", 0, "Command repeat(" + main.Main.tabCountInRow + "): "
					+ main.Main.commandHistory.get(main.Main.commandHistory.size() - main.Main.tabCountInRow));
			sys.shellPrint(1, "HIDDEN", main.Main.commandHistory.get(main.Main.commandHistory.size() - main.Main.tabCountInRow), true);
		}
		//========================================COMMAND REPEAT END============================================
	}
}
