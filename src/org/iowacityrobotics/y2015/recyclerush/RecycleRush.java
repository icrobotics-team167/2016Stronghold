package org.iowacityrobotics.y2015.recyclerush;

import org.iowacityrobotics.lib167.RobotBase;
import org.iowacityrobotics.lib167.control.SingleJoy;
import org.iowacityrobotics.lib167.control.auto.TimedController;
import org.iowacityrobotics.lib167.drive.DriveType;
import org.iowacityrobotics.lib167.drive.IDriveTrain;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RecycleRush extends RobotBase<IDriveTrain, TimedController<IDriveTrain>> {
	
	@Override
	protected void onInit() {
		mainCont = new SingleJoy(0);
		drive = new WeirdDrive(7, 1, 9, 2, DriveType.MECANUM);
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
		// NO-OP
	}
	
}
