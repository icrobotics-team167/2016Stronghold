package org.iowacityrobotics.y2016.stronghold;

import org.iowacityrobotics.lib167.RobotBase;
import org.iowacityrobotics.lib167.control.TankJoys;
import org.iowacityrobotics.lib167.control.auto.TimedController;
import org.iowacityrobotics.lib167.drive.CANRobotDrive;
import org.iowacityrobotics.lib167.drive.DriveType;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Stronghold extends RobotBase<CANRobotDrive, TimedController<CANRobotDrive>> {

	@Override
	protected void onInit() {
		mainCont = new TankJoys(0, 1, false);
		drive = new CANRobotDrive(5, 2, 6, 3, DriveType.TANK);
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
