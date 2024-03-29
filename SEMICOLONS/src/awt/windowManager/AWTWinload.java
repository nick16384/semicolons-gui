package awt.windowManager;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import engine.sys;
import libraries.VarLib;
import main.Main;

public class AWTWinload {
	public static void awtWinload() {
		sys.log("MAIN", 0, "Loading main AWT window...");
		Main.initAWTWindow();
		//try { Thread.sleep(2000); } catch (InterruptedException ie) { ie.printStackTrace(); }
		sys.log("MAIN", 1, "Done.");
		sys.log("MAIN", 1, "Setting parameters for mainFrame (icon image, title)...");
		//Set icon image for mainFrame
		try {
			Main.mainFrameAWT.setIconImage(ImageIO.read(
					new File(VarLib.fsep + "etc" + VarLib.fsep +
							"venus" + VarLib.fsep + "data" + VarLib.fsep + "venus-icon.png")));
		} catch (IOException e) {
			sys.log("MAIN", 3, "Could not set icon image. The file probably doesn't exist or is not a supported image file.");
			sys.log("MAIN", 3, "Icon path: " +
					VarLib.fsep + "etc" + VarLib.fsep +
					"venus" + VarLib.fsep + "data" + VarLib.fsep + "venus-icon.png");
		}
		Main.mainFrameAWT.setName("J-Vexus " + VarLib.getVersion());
		sys.log("MAIN", 0, "Done.");
	}
}
