package jfxcomponents;

import engine.InfoType;
import engine.sys;
import main.Main;
import threads.InternalThread;
import threads.ThreadAllocation;

public class JFxGUIThread implements InternalThread {
	private Thread jfxGUIThread;
	private boolean isGUIActive;
	
	public JFxGUIThread() {
		jfxGUIThread = new Thread(null, () -> {
			sys.log("JFXT", InfoType.INFO, "Starting JFx GUI thread.");
			while (Main.jfxWinloader == null)
				try { Thread.sleep(50); } catch (InterruptedException ie) { ie.printStackTrace(); }
			
			sys.log("JFXT", InfoType.INFO, "Launching JavaFX GUI...");
			isGUIActive = true;
			Main.jfxWinloader.loadGUI(Main.argsMain);
			//loadGUI() will not return until window is closed or Platform.exit() is called.
			isGUIActive = false;
			sys.log("JFXT", InfoType.INFO, "JavaFX window was closed. Stopping SEMICOLONS.");
			ThreadAllocation.shutdownVexus(0);
		}, "JFXT");
	}
	
	@Override
	public void start() {
		if (!jfxGUIThread.isAlive())
			jfxGUIThread.start();
	}
	@Override
	public void suspend() {
		Main.jfxWinloader.stop();
	}
	@Override
	public boolean isRunning() {
		return jfxGUIThread.isAlive();
	}
	
	public boolean isGUIActive() {
		return isGUIActive;
	}
}