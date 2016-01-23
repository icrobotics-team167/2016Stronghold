package org.iowacityrobotics.y2015.recyclerush;

import org.iowacityrobotics.lib167.RobotBase;
import org.iowacityrobotics.lib167.control.IController;
import org.iowacityrobotics.lib167.control.SingleJoy;
import org.iowacityrobotics.lib167.control.auto.TimedController;
import org.iowacityrobotics.lib167.drive.IDriveTrain;
import org.iowacityrobotics.lib167.util.MathUtils;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tInstances;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tResourceType;
import edu.wpi.first.wpilibj.communication.UsageReporting;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RecycleRush extends RobotBase<IDriveTrain, TimedController<IDriveTrain>> {

	private RobotDrive dr;
	private Joystick joy;
	
	@Override
	protected void onInit() {
		mainCont = new SingleJoy(0);
		//drive = new RobotDriveWrapper(7, 1, 9, 2, DriveType.MECANUM);
		drive = new IDriveTrain() {

			@Override
			public void update(IController c) {
				// NO-OP
			}
			
		};
		dr = new RobotDrive(7, 9, 1, 2) {
			@Override
			public void mecanumDrive_Cartesian(double x, double y, double rot, double gyro) {
				if (!kMecanumCartesian_Reported) {
			      UsageReporting.report(tResourceType.kResourceType_RobotDrive, getNumMotors(),
			          tInstances.kRobotDrive_MecanumCartesian);
			      kMecanumCartesian_Reported = true;
			    }
			    double xIn = x;
			    double yIn = y;
			    // Negate y for the joystick.
			    yIn = -yIn;
			    // Compenstate for gyro angle.
			    double rotated[] = rotateVector(xIn, yIn, gyro);
			    xIn = rotated[0];
			    yIn = rotated[1];

			    double wheelSpeeds[] = new double[kMaxNumberOfMotors];
			    wheelSpeeds[MotorType.kFrontLeft.value] = xIn + yIn + rot;
			    wheelSpeeds[MotorType.kFrontRight.value] = -xIn + yIn - rot;
			    wheelSpeeds[MotorType.kRearLeft.value] = -xIn + yIn + rot;
			    wheelSpeeds[MotorType.kRearRight.value] = xIn + yIn - rot;

			    normalize(wheelSpeeds);
			    m_frontLeftMotor.set(wheelSpeeds[MotorType.kFrontLeft.value] * m_maxOutput, m_syncGroup);
			    m_frontRightMotor.set(-wheelSpeeds[MotorType.kFrontRight.value] * m_maxOutput, m_syncGroup);
			    m_rearLeftMotor.set(wheelSpeeds[MotorType.kRearLeft.value] * m_maxOutput, m_syncGroup);
			    m_rearRightMotor.set(-wheelSpeeds[MotorType.kRearRight.value] * m_maxOutput, m_syncGroup);

			    if (m_syncGroup != 0) {
			      CANJaguar.updateSyncGroup(m_syncGroup);
			    }

			    if (m_safetyHelper != null)
			      m_safetyHelper.feed();
			}
		};
		autoCont = new TimedController<>();
		joy = new Joystick(0);
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
		double th = MathUtils.clamp((1D - joy.getThrottle()) / 2D, 0.1, 0.9);
		dr.mecanumDrive_Cartesian(joy.getX()* th, joy.getY() * th, joy.getTwist() * th, 0);
	}
	
}
