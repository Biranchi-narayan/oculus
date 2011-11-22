package oculus;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import oculus.Settings;
import oculus.State;
import oculus.Util;

public class SystemWatchdog {
	
	// private static Logger log = Red5LoggerFactory.getLogger(SystemWatchdog.class, "oculus");
	private final Settings settings = new Settings();
	private final boolean reboot = settings.getBoolean(State.reboot);
	//private final boolean debug = settings.getBoolean(State.developer);

	// check every hour
	public static final long DELAY = State.TWO_MINUTES;

	// when is the system stale and need reboot
	public static final long STALE = State.ONE_DAY * 2; 
	
	// shared state variables
	private State state = State.getReference();
	
    /** Constructor */
	public SystemWatchdog() {
		if (reboot){
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new Task(), State.TEN_MINUTES, DELAY);
		}	
	}
	
	private class Task extends TimerTask {
		public void run() {
		
			// only reboot is idle 
			if ((state.getUpTime() > STALE) && !state.getBoolean(State.userisconnected)){ 
				
				String boot = new Date(state.getLong(State.boottime)).toString();				
				System.out.println("OCULUS: rebooting, last was: " + boot);
				System.out.println("OCULUS: user logged in for: " + state.getLoginSince() + " ms");
				
				// reboot  
				Util.systemCall("shutdown -r -f -t 01");				
			}
		}
	}
}
