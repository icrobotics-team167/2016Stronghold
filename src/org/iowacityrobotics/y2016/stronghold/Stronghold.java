package org.iowacityrobotics.y2016.stronghold;

import org.iowacityrobotics.lib167.RobotBase;
import org.iowacityrobotics.lib167.control.LogitechTankController;
import org.iowacityrobotics.lib167.control.auto.TimedController;
import org.iowacityrobotics.lib167.drive.CANRobotDrive;
import org.iowacityrobotics.lib167.drive.DriveType;

public class Stronghold extends RobotBase<CANRobotDrive, TimedController<CANRobotDrive>> {

	@Override
	protected void onInit() {
		mainCont = new LogitechTankController(2, true);
		drive = new CANRobotDrive(5, 2, 6, 3, DriveType.TANK_RAAF);
		autoCont = new TimedController<>();
	}

	@Override
	protected void onAuto() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void whileAuto() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onTeleop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void whileTeleop() {
		// TODO Auto-generated method stub
		
	}
	
}
