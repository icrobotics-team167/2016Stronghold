package org.iowacityrobotics.y2015.recyclerush;

import org.iowacityrobotics.lib167.component.ReverseSpeedController;
import org.iowacityrobotics.lib167.drive.DriveType;
import org.iowacityrobotics.lib167.drive.RobotDriveWrapper;

public class WeirdDrive extends RobotDriveWrapper {

	public WeirdDrive(int fl, int fr, int bl, int br, DriveType type) {
		super(fl, fr, bl, br, type);
		this.m_frontRightMotor = new ReverseSpeedController<>(this.m_frontRightMotor, fr);
		this.m_rearRightMotor = new ReverseSpeedController<>(this.m_rearRightMotor, br);
	}
	
}
