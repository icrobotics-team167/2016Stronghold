	package org.iowacityrobotics.y2016.stronghold;

import org.iowacityrobotics.lib167.RobotBase;
import org.iowacityrobotics.lib167.control.LogitechTankController;
import org.iowacityrobotics.lib167.control.auto.EncoderController;
import org.iowacityrobotics.lib167.drive.CANRobotDrive;
import org.iowacityrobotics.lib167.drive.DriveType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Stronghold extends RobotBase<CANRobotDrive, EncoderController<CANRobotDrive>> {

	private static final double DEF_ARM_SAFE_PERCENT = 0.5D;
	
	private BallBelt ballBelt;
	private ShootDrive shootDrive;
	private LogitechTankController secCont;
	private DefenseArm defArm;
	private long backingUp = 0L, buTimer = -1L;
	private boolean yPressed = false;
	private AutoSwitcher aSwitch;
	
	@Override
	protected void onInit() {
		mainCont = new ReversableController(2, false); //true if TANK_RAAF, false if TANK
		drive = new CANRobotDrive(1, 2, 8, 9, DriveType.TANK);
		autoCont = new EncoderController<>();
		secCont = new LogitechTankController(3, false);
		ballBelt = new BallBelt(4, 6);
		shootDrive = new ShootDrive(3, 7);
		defArm = new DefenseArm(0, 0, 1);
		aSwitch = new AutoSwitcher(this);
	}

	@Override
	protected void onAuto() {
		aSwitch.getRoutine().registerActions(autoCont);
	}

	@Override
	protected void whileAuto() {
		// NO-OP
	}

	@Override
	protected void onTeleop() {
		// NO-OP
	}

	@Override
	protected void whileTeleop() {
		defArm.setState(secCont.getAxis(5) * (DEF_ARM_SAFE_PERCENT + secCont.getAxis(2) * (0.98D - DEF_ARM_SAFE_PERCENT)));
		if (backingUp < 1L) {
			double beltSpeed = -secCont.getAxis(1);
			if (Math.abs(beltSpeed) < 0.08D)
				beltSpeed = 0D;
			ballBelt.setState(beltSpeed);
			if (secCont.isPressed(2))
				shootDrive.setState(-0.7D);
			else {
				double shootSpeed = secCont.getAxis(3);
				if (Math.abs(shootSpeed) < 0.08D)
					shootSpeed = 0D;
				shootDrive.setState(shootSpeed);
			}
			buTimer = -1L;
		} else {
			long currentTime = System.currentTimeMillis();
			if (buTimer != -1L)
				backingUp -= currentTime - buTimer;
			buTimer = currentTime;
			ballBelt.setState(0.2D);
			shootDrive.setState(-0.35D);
		}
		
		if (secCont.isPressed(4)) {
			if (!yPressed) {
				backingUp = 64L;
				yPressed = true;
			}
		}
		else
			yPressed = false;
	}
	
	@Override
	public void disabledPeriodic() {
		aSwitch.update();
	}
	
	@Override
	public void testPeriodic() {
		SmartDashboard.putNumber("Enc Count", drive.getEncoderEngine().getTotalCount());
	}
	
}
