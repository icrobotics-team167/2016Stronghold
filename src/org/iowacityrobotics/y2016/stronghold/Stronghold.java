package org.iowacityrobotics.y2016.stronghold;

import org.iowacityrobotics.lib167.RobotBase;
import org.iowacityrobotics.lib167.control.LogitechTankController;
import org.iowacityrobotics.lib167.control.auto.TimedController;
import org.iowacityrobotics.lib167.drive.CANRobotDrive;
import org.iowacityrobotics.lib167.drive.DriveType;

public class Stronghold extends RobotBase<CANRobotDrive, TimedController<CANRobotDrive>> {

	private BallBelt ballBelt;
	private ShootDrive shootDrive;
	private LogitechTankController secCont;
	
	@Override
	protected void onInit() {
		mainCont = new LogitechTankController(2, false); //true if TANK_RAAF, false if TANK
		drive = new CANRobotDrive(1, 2, 8, 9, DriveType.TANK);
		autoCont = new TimedController<>();
		secCont = new LogitechTankController(3, false);
		ballBelt = new BallBelt(4, 6);
		shootDrive = new ShootDrive(3, 7);
	}

	@Override
	protected void onAuto() {
		// NO-OP
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
		double beltSpeed = -secCont.getAxis(1);
		if (beltSpeed < 0.1D && beltSpeed > -0.1D)
			beltSpeed = 0D;
		
		ballBelt.setState(beltSpeed);
		shootDrive.setState(secCont.getAxis(3));
	}
	
}
