package CombatWaffle;

import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

class WindowClosingEvent extends WindowAdapter {

    public static boolean running = true;

    public void windowClosed(WindowEvent arg0) {
        WindowClosingEvent.running = false;
        System.exit(0);
    }

}