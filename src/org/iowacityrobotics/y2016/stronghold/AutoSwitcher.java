package org.iowacityrobotics.y2016.stronghold;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.iowacityrobotics.lib167.control.auto.EncoderController;
import org.iowacityrobotics.lib167.drive.CANRobotDrive;
import org.iowacityrobotics.lib167.i2c.MXPDigitBoard;

import edu.wpi.first.wpilibj.I2C.Port;

/**
 * 1 | Port<br>
 * 2 | Teeter<br>
 * 3 | Ramparts<br>
 * 4 | Moat<br>
 * 5 | Rough Terrain<br>
 * 6 | Rock Wall<br>
 * 7 | Low Bar
 * Platforms are 4' 2"
 */
public class AutoSwitcher {

	/**
	 * Diameter of the robot's turn, in meters.
	 */
	private static final double DIAM = 0.5842D;
	
	private final Stronghold sh;
	private List<AutoRoutine> routines = new ArrayList<>();
	private boolean prevA = false, prevB = false;
	private SelectionState selState;
	private int defInd = 0, posInd = 0;
	private int[] teamNumPos = new int[] {0, 1, 2};
	private long updateTick = 0L;
	private MXPDigitBoard cont = new MXPDigitBoard(Port.kMXP, 0x70);
	
	public static AutoRoutine generateRegistrator(double driveDist, int stPos) {
		return new AutoRoutine(c -> {
			c.queueAction(d -> d.tankDrive(0.75D, 0.75D), driveDist);
			double angOffset = 0D, arcLen = 0D; // TODO set this to something
			c.queueAction(d -> d.tankDrive(-0.6 + angOffset, 0.6 - angOffset), arcLen);
			c.queueAction(d -> {}, d -> false);
		});
	}
	
	public AutoSwitcher(Stronghold parent) {
		sh = parent;
		cont.setBrightness(12);
		cont.setLEDsEnabled(true);
	}
	
	public void addRoutine(AutoRoutine routine) {
		routines.add(routine);
	}
	
	public AutoRoutine getRoutine() {
		return routines.get(defInd * 5 + posInd);
	}
	
	public void update() {
		boolean a = false, b = false;
		if (cont.buttonAPressed()) {
			if (!prevA) {
				a = true;
				prevA = true;
			}
		}
		else
			prevA = false;
		if (cont.buttonBPressed()) {
			if (!prevB) {
				b = true;
				prevB = true;
			}
		}
		else
			prevB = false;
		
		SelectionState toSet = selState;
		switch (selState) {
		case OFF:
			cont.putChar(teamNumPos[0], MXPDigitBoard.CHAR_7);
			cont.putChar(teamNumPos[1], MXPDigitBoard.CHAR_6);
			cont.putChar(teamNumPos[2], MXPDigitBoard.CHAR_1);
			if (updateTick++ % 19L == 0) {
				for (int i = 0; i < teamNumPos.length; i++)
					teamNumPos[i] = (teamNumPos[i] + 1) % 4;
			}
			if (a || b)
				toSet = SelectionState.DEF;
			break;
		case DEF:
			cont.putChar(0, MXPDigitBoard.forChar(Integer.toString(defInd + 1).charAt(0)));
			cont.putChar(1, MXPDigitBoard.BLANK);
			cont.putChar(2, MXPDigitBoard.BLANK);
			if (a)
				defInd = (defInd + 1) % 7;
			else if (b) {
				if (defInd == 6)
					toSet = SelectionState.OFF;
				else
					toSet = SelectionState.POS;
			}
			break;
		case POS:
			cont.putChar(0, MXPDigitBoard.forChar(Integer.toString(posInd + 1).charAt(0)));
			if (a)
				posInd = (posInd + 1) % 5;
			else if (b)
				toSet = SelectionState.OFF;
			break;
		}
		selState = toSet;
	}
	
	public void stopSelection() {
		selState = SelectionState.OFF;
	}
	
	public static class AutoRoutine {
		
		private Consumer<EncoderController<CANRobotDrive>> autoActionReg;
		
		public AutoRoutine(Consumer<EncoderController<CANRobotDrive>> actionRegisterer) {
			autoActionReg = actionRegisterer;
		}

		public void registerActions(EncoderController<CANRobotDrive> autoCont) {
			autoCont.clearQueue();
			autoActionReg.accept(autoCont);
		}
		
	}
	
	private static enum SelectionState {
		
		OFF, DEF, POS;
		
	}
	
}
