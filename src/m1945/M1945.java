
package m1945;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
//import java.util.*;

public class M1945 extends MIDlet implements CommandListener {

 private Command exitCommand, playCommand;//, endCommand;
 private Display display;
 private SSCanvas screen;

 public M1945() {
	display=Display.getDisplay(this);
	exitCommand = new Command("Salir",Command.SCREEN,2);
	playCommand = new Command("Jugar",Command.CANCEL,2);
//	endCommand  = new Command("Salir",Command.SCREEN,2);

	screen=new SSCanvas();

	screen.addCommand(playCommand);
	screen.addCommand(exitCommand);
	screen.setCommandListener(this);
 }

 public void startApp() throws MIDletStateChangeException {
	display.setCurrent(screen);
 }

 public void pauseApp() {}

 public void destroyApp(boolean unconditional) {}

 public void commandAction(Command c, Displayable s) {

	if (c == exitCommand) {
		if (screen.isPlaying()) {
			screen.quitGame();
		} else {
			destroyApp(false);
			notifyDestroyed();
		}
	}

	if (c == playCommand && !screen.isPlaying()) {
		// Play!!!
		new Thread(screen).start();
	}
 }

}

