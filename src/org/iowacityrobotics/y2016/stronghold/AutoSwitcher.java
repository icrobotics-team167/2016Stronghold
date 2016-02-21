package org.iowacityrobotics.y2016.stronghold;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.iowacityrobotics.lib167.control.auto.EncoderController;
import org.iowacityrobotics.lib167.drive.CANRobotDrive;
import org.iowacityrobotics.lib167.i2c.MXPDigitBoard;
import org.iowacityrobotics.lib167.util.MathUtils;

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
	private static final double DIAM = 0.6858D;
	private static final double CIRC = DIAM * MathUtils.TWO_PI;
	private static final double LOW_BAR_ANG = Math.atan2(172D, 80D), LOW_BAR_DIST = Math.hypot(80D, 154D);
	private static final double SHOOT_CONST = 0.8D * Math.hypot(121D, 22D);
	private static final double DIST_TO_PT = 4.572D, DIST_FROM_BAR = 6.8326D;	
	private final Stronghold sh;
	private List<AutoRoutine> routines = new ArrayList<>();
	private boolean prevA = false, prevB = false;
	private SelectionState selState;
	private int defInd = 0, posInd = 0;
	private int[] teamNumPos = new int[] {0, 1, 2};
	private long updateTick = 0L;
	private MXPDigitBoard cont = new MXPDigitBoard(Port.kMXP, 0x70);
	
	public static AutoRoutine generateRoutine(double driveDist, int stPos, Stronghold sh) {
		return new AutoRoutine(c -> {
			double centDist = 172D - 50D;
			double angle = stPos != 0 ? Math.atan2(centDist * (double)stPos, 121) : LOW_BAR_ANG;
			double sign = Math.signum(angle), arcLen = (angle / MathUtils.TWO_PI) * CIRC;
			double shootPower = SHOOT_CONST / Math.hypot(stPos != 0 ? centDist : LOW_BAR_DIST, 121);
			c.queueAction(d -> d.tankDrive(0.75D, 0.75D), driveDist)
					.queueAction(d -> d.tankDrive(0.4D * sign, -0.4D * sign), arcLen)
					.queueAction(d -> {
						sh.autoTime = System.currentTimeMillis();
						sh.ballBelt.setState(0.2D);
						sh.shootDrive.setState(-0.35D);
					}, d -> System.currentTimeMillis() - sh.autoTime > 64L)
					.queueAction(d -> {
						sh.autoTime = System.currentTimeMillis();
						sh.ballBelt.setState(1D);
						sh.shootDrive.setState(shootPower);
					}, d -> System.currentTimeMillis() - sh.autoTime > 768L);
		});
	}
	
	public AutoSwitcher(Stronghold parent) {
		sh = parent;
		cont.setBrightness(12);
		cont.setLEDsEnabled(true); // TODO portucullis and teeter totter autonomous
		for (int i = 0; i < 5; i++) {
			// routines.add(generateRoutine(DIST_TO_PT, i, sh)); // Portucullis
			routines.add(new AutoRoutine(c -> {}));
			// routines.add(generateRoutine(DIST_TO_PT, i, sh)); // Teeter-totter
			routines.add(new AutoRoutine(c -> {}));
			routines.add(generateRoutine(DIST_TO_PT * 1.6D, i, sh)); // Ramparts
			routines.add(generateRoutine(DIST_TO_PT * 1.3D, i, sh)); // Moat
			routines.add(generateRoutine(DIST_TO_PT * 1.6D, i, sh)); // Rough terrain
			routines.add(generateRoutine(DIST_TO_PT * 2.3D, i, sh)); // Rock wall
			routines.add(generateRoutine(DIST_FROM_BAR * 1.5D, i, sh)); // Low bar
		}
	}
	
	public AutoRoutine getRoutine() {
		return routines.get(posInd * 7 + defInd);
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
