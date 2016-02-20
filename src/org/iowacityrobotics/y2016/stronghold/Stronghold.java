package org.iowacityrobotics.y2016.stronghold;

import org.iowacityrobotics.lib167.RobotBase;
import org.iowacityrobotics.lib167.control.LogitechTankController;
import org.iowacityrobotics.lib167.control.auto.EncoderController;
import org.iowacityrobotics.lib167.drive.CANRobotDrive;
import org.iowacityrobotics.lib167.drive.DriveType;

public class Stronghold extends RobotBase<CANRobotDrive, EncoderController<CANRobotDrive>> {

	private BallBelt ballBelt;
	private ShootDrive shootDrive;
	private LogitechTankController secCont;
	private DefenseArm defArm;
	
	@Override
	protected void onInit() {
		mainCont = new ReversableController(2, false); //true if TANK_RAAF, false if TANK
		drive = new CANRobotDrive(1, 2, 8, 9, DriveType.TANK);
		autoCont = new EncoderController<>();
		secCont = new LogitechTankController(3, false);
		ballBelt = new BallBelt(4, 6);
		shootDrive = new ShootDrive(3, 7);
		defArm = new DefenseArm(0);
	}

	@Override
	protected void onAuto() {
		autoCont.clearQueue()
				.queueAction(d -> d.drive(1D, 0D), 5.2D)
				.queueAction(d -> d.stopMotor(), 0);
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
		defArm.setState(secCont.getAxis(7));
	}
	
}
