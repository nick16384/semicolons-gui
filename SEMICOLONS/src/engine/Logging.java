package engine;

import java.awt.Color;

import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import libraries.Global;
import main.Main;
import threads.ThreadAllocation;

public class Logging {
	protected static void shell_write(int priority, String auth, String message) {
		if (priority > 5) {
			sys.log("LOWLEVEL", InfoType.ERR, ": priority out of range (1 - 5)");
			return;
		}
		StyledDocument doc = Main.mainFrameAWT.getCmdLine().getStyledDocument();
		Style style = Main.mainFrameAWT.getCmdLine().addStyle("cmdLineStyle", null);

		if (auth.equalsIgnoreCase("HIDDEN")) { // If authority wants to be hidden (e.g. during init)
			if (priority == 1) {
				StyleConstants.setForeground(style, new Color(255, 255, 255));
				try {
					doc.insertString(doc.getLength(), message, style);
				} catch (BadLocationException e) {
					sys.log("LOWLEVEL", InfoType.ERR, "Cannot write to cmdLine: BadLocationException");
				}
			} else if (priority == 2) {
				StyleConstants.setForeground(style, new Color(128, 255, 0));
				try {
					doc.insertString(doc.getLength(), message, style);
				} catch (BadLocationException e) {
					sys.log("LOWLEVEL", InfoType.ERR, "Cannot write to cmdLine: BadLocationException");
				}
			} else if (priority == 3) {
				StyleConstants.setForeground(style, new Color(255, 255, 0));
				try {
					doc.insertString(doc.getLength(), message, style);
				} catch (BadLocationException e) {
					sys.log("LOWLEVEL", InfoType.ERR, "Cannot write to cmdLine: BadLocationException");
				}
			} else if (priority == 4) {
				StyleConstants.setForeground(style, new Color(255, 128, 0));
				try {
					doc.insertString(doc.getLength(), message, style);
				} catch (BadLocationException e) {
					sys.log("LOWLEVEL", InfoType.ERR, "Cannot write to cmdLine: BadLocationException");
				}
			} else if (priority == 5) {
				StyleConstants.setForeground(style, new Color(255, 0, 0));
				try {
					doc.insertString(doc.getLength(), message, style);
				} catch (BadLocationException e) {
					sys.log("LOWLEVEL", InfoType.ERR, "Cannot write to cmdLine: BadLocationException");
				}
			}
		} else {
			if (priority == 1) {
				StyleConstants.setForeground(style, new Color(255, 255, 255));
				try {
					doc.insertString(doc.getLength(), "[ " + Global.getDateTime(false) + ", " + auth + " ]: " + message,
							style);
				} catch (BadLocationException e) {
					sys.log("LOWLEVEL", InfoType.ERR, "Cannot write to cmdLine: BadLocationException");
				}
			} else if (priority == 2) {
				StyleConstants.setForeground(style, new Color(128, 255, 0));
				try {
					doc.insertString(doc.getLength(), "[ " + Global.getDateTime(false) + ", " + auth + " ]: " + message,
							style);
				} catch (BadLocationException e) {
					sys.log("LOWLEVEL", InfoType.ERR, "Cannot write to cmdLine: BadLocationException");
				}
			} else if (priority == 3) {
				StyleConstants.setForeground(style, new Color(255, 255, 0));
				try {
					doc.insertString(doc.getLength(), "[ " + Global.getDateTime(false) + ", " + auth + " ]: " + message,
							style);
				} catch (BadLocationException e) {
					sys.log("LOWLEVEL", InfoType.ERR, "Cannot write to cmdLine: BadLocationException");
				}
			} else if (priority == 4) {
				StyleConstants.setForeground(style, new Color(255, 128, 0));
				try {
					doc.insertString(doc.getLength(), "[ " + Global.getDateTime(false) + ", " + auth + " ]: " + message,
							style);
				} catch (BadLocationException e) {
					sys.log("LOWLEVEL", InfoType.ERR, "Cannot write to cmdLine: BadLocationException");
				}
			} else if (priority == 5) {
				StyleConstants.setForeground(style, new Color(255, 0, 0));
				try {
					doc.insertString(doc.getLength(), "[ " + Global.getDateTime(false) + ", " + auth + " ]: " + message,
							style);
				} catch (BadLocationException e) {
					sys.log("LOWLEVEL", InfoType.ERR, "Cannot write to cmdLine: BadLocationException");
				}
			}
		}
	}

	public static void logWrite(String auth, InfoType status, String message) {
		// Following values are available as status:
		// -1: Any(unknown) error
		// 0: Status / debugging messages
		// 1: Info (Shows information or hints for usage)
		// 2: Warning (might be no error but shows e.g. that an old version is used)
		// 3: Non-critical error (program can continue but there may be other errors)
		// 4: Critical error (program must stop e.g. security issue or no sufficient
		// permissions)
		// 5: Irreversible critical error(fatal) (Damages still persist after SEMICOLONS
		// shutdown)
		long runtime;
		try {
			runtime = System.currentTimeMillis() - ThreadAllocation.getWDT().getTimeStart();
		} catch (NullPointerException npe) {
			runtime = -1;
		}
		if (status.equals(InfoType.ERR) && status.isEnabled()) {
			System.out.println("[ " + runtime + ", -1/ERR, " + auth + " ]: " + message);
		} else if (status.asInt() == 0 && status.isEnabled()) {
			System.out.println("[ " + runtime + ", " + auth + " ]: " + message);
		} else if (status.equals(InfoType.INFO) && status.isEnabled()) {
			System.out.println("[ " + runtime + ", " + "1/INFO, " + auth + " ]: " + message);
		} else if (status.equals(InfoType.WARN) && status.isEnabled()) {
			System.err.println("[ " + runtime + ", " + "2/WARN, " + auth + " ]: " + message);
		} else if (status.equals(InfoType.NONCRIT) && status.isEnabled()) {
			System.err.println("[ " + runtime + ", " + "3/NONCRIT, " + auth + " ]: " + message);
		} else if (status.equals(InfoType.CRIT) && status.isEnabled()) {
			System.err.println("[ " + runtime + ", " + "4/CRIT, " + auth + " ]: " + message);
		} else if (status.equals(InfoType.FATAL) && status.isEnabled()) {
			System.err.println("[ " + runtime + ", " + "5/FATAL, " + auth + " ]: " + message.toUpperCase());
		}

		if (Global.consoleLogStream != null) {
			if (status.equals(InfoType.ERR) && status.isEnabled()) {
				Global.consoleLogStream
						.println("[ " + Global.getDateTime(false) + ", -1/ERR, " + auth + " ]: " + message);
			} else if (status.asInt() == 0 && status.isEnabled()) {
				Global.consoleLogStream.println("[ " + Global.getDateTime(false) + ", " + auth + " ]: " + message);
			} else if (status.equals(InfoType.INFO) && status.isEnabled()) {
				Global.consoleLogStream
						.println("[ " + Global.getDateTime(false) + ", " + "1/INFO, " + auth + " ]: " + message);
			} else if (status.equals(InfoType.WARN) && status.isEnabled()) {
				Global.consoleLogStream
						.println("[ " + Global.getDateTime(false) + ", " + "2/WARN, " + auth + " ]: " + message);
			} else if (status.equals(InfoType.NONCRIT) && status.isEnabled()) {
				Global.consoleLogStream
						.println("[ " + Global.getDateTime(false) + ", " + "3/NONCRIT, " + auth + " ]: " + message);
			} else if (status.equals(InfoType.CRIT) && status.isEnabled()) {
				Global.consoleLogStream.println(
						"[ " + Global.getDateTime(false) + ", " + "4/CRIT, " + auth + " ]: " + message.toUpperCase());
			} else if (status.equals(InfoType.FATAL) && status.isEnabled()) {
				Global.consoleLogStream.println(
						"[ " + Global.getDateTime(false) + ", " + "5/FATAL, " + auth + " ]: " + message.toUpperCase());
			}
		}
	}
}
