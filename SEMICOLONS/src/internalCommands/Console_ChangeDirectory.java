package internalCommands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import awtcomponents.AWTANSI;
import components.Shell;
import engine.InfoType;
import engine.sys;
import libraries.VariableInitializion;
import libraries.Env;
import libraries.Global;

public class Console_ChangeDirectory {
	public static String changeDirectory(ArrayList<String> params, Map<String, String> paramsWithValues) {
		if (params == null || params.size() == 0) {
			sys.log("CHDIR", InfoType.INFO, "No parameters provided, changing to root directory.");
			Global.setCurrentDir((new File(Global.getFSRoot())).getAbsolutePath());
		} else if (params.get(0).equals("..")) {
			sys.log("CHDIR", InfoType.INFO, "Going up one layer.");
			Global.setCurrentDir(
					(new File(Global.getCurrentDir().replace(Global.getCurrentDir().split(Global.fsep)[1], "")))
							.getAbsolutePath());

		} else if ((new File(Global.getCurrentDir() + Global.fsep + params.get(0))).isDirectory()) {
			sys.log("CHDIR", InfoType.INFO, "CD'ing into directory of current directory.");
			Global.setCurrentDir(
					(new File(Global.getCurrentDir() + Global.fsep + (String) params.get(0))).getAbsolutePath());
		} else if ((new File(params.get(0))).isDirectory()) {
			sys.log("CHDIR", InfoType.INFO, "Changing directory to absolute path.");
			Global.setCurrentDir((new File(params.get(0))).getAbsolutePath());
		} else {
			try {
				
				if (params.size() == 1
						&& !(new File(params.get(0)).exists())
						|| !(new File(params.get(0)).getCanonicalFile().exists())) {
					
					if (!(new File(Global.getCurrentDir() + Global.fsep + params.get(0)).isDirectory())
							|| !(new File(Global.getCurrentDir() + Global.fsep + params.get(0)).getCanonicalFile().isDirectory())) {
						if (Global.getCurrentDir().equals(Global.fsep))
							sys.log("LSDIR", InfoType.DEBUG, "Found a path break inside current dir: \n"
									+ checkPathBreak(Global.getCurrentDir() + params.get(0)) + " does not exist.");
						else
							sys.log("LSDIR", InfoType.DEBUG, "Found a path break inside current dir: \n"
									+ checkPathBreak(Global.getCurrentDir() + Global.fsep + params.get(0)) + " does not exist.");
					} else {
						sys.log("LSDIR", InfoType.DEBUG, "Found a path break: " + checkPathBreak(params.get(0)) + " does not exist.");
					}
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			return "FileErr_NotFound";
		}

		sys.log("CHDIR", InfoType.DEBUG, "Updating the $PATH variable.");
		Env.updateEnv("$PATH");
		return null;
	}
	
	/**
	 * This method tries to find a break (non-existent folder) in a longer path:
	 * e.g. /home/user/doesnotexist/folder1/nodocument.txt will find, that "/home/user/doesnotexist" does not exist,
	 * instead of the entire path just being treated as "does not exist". More useful to the end-user.
	 * @param location The path of some folder, that may not exist
	 * @return Location, at which the path does not exist anymore.
	 */
	private static String checkPathBreak(String location) {
		try {
			sys.log("LSDIR", InfoType.WARN, "Provided directory doesn't exist. Trying to figure out, where the tree brakes.");
			String addedBrackets = Global.getFSRoot();
			for (String bracket : location.split(Global.fsep)) {
				
				//TODO Check if this folder is really a folder and only then print path breaks
				if ((new File(addedBrackets).exists() || new File(addedBrackets).getCanonicalFile().exists())
						&& new File(addedBrackets).isDirectory() || new File(addedBrackets).getCanonicalFile().isDirectory()) {
					if (!bracket.equals(Global.getFSRoot()))
						addedBrackets = addedBrackets.concat(bracket + Global.fsep);
				} else {
					Shell.println(AWTANSI.B_Yellow, "Can't change into the folder:");
					Shell.println(AWTANSI.B_Green, location);
					Shell.println(AWTANSI.B_Yellow, "because the following folder does not exist:");
					Shell.println(AWTANSI.B_Cyan, addedBrackets + "\n");
					return addedBrackets;
				}
			}
		} catch (IOException ioe) {
			sys.log("LSDIR", InfoType.ERR, "IOException while checking for tree break.");
			ioe.printStackTrace();
		}
		return null;
	}
}