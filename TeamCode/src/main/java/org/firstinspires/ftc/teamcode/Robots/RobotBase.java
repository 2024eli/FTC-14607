package org.firstinspires.ftc.teamcode.Robots;

import androidx.annotation.NonNull;

import java.lang.Math;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.controller.PIDFController;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Controller class to congregate code for manipulating physical hardware on the robot
 * (e.g. movement methods, motor instances, sensor inputs)
 *
 * RobotBase is to be extended by all hardware control classes for basic functionality
 * and so u write less
 */
@Config
public class RobotBase {
    // op mode
    public LinearOpMode opMode;
    public Telemetry telemetry;
    // drivetrain
    public DcMotorEx frontRight, frontLeft, backRight, backLeft;
    public DcMotorEx[] drivetrain;
//    public DcMotorEx odoRight, odoLeft, odoBack;
    // sensor/controllers
    public BNO055IMU imu;
    public static double headP = 3.5;
    public static double headI = 0.4;
    public static double headD = 0.2f;
    public static double headF = 0.3;
    public static PIDFController headingPIDFController = new PIDFController(headP,headI,headD,headF);
    public PIDFController[] drivetrainStrafePIDFControllers;
    public static PIDFController FRStrafePID =   new PIDFController(1.47,0.06,0.1,0.3);// FR
    public static PIDFController FLStrafePID = new PIDFController(1.6,0.06,0.1,0.3); // FL
    public static PIDFController BRStrafePID = new PIDFController(1.7,0.06,0.1,0.3); // BR
    public static PIDFController BLStrafePID = new PIDFController(1.9,0.06,0.1,0.3); // BL
    public PIDFController[] drivetrainPIDFControllers;
    public static PIDFController FRPID =   new PIDFController(1.1,0,0.1,0.2);// FR
    public static PIDFController FLPID = new PIDFController(1.1,0,0.1,0.2); // FL
    public static PIDFController BRPID = new PIDFController(1.2,0,0.13,0.2); // BR
    public static PIDFController BLPID = new PIDFController(1.22,0,0.13,0.2); // BL
    // hardware properties
    public final int motorTicks; //gobilda 5202 1150 rpm
    public final double wheelDiameter, wheelCircumference; //cm

    /**
     * Instantiate all variables related to the robot and the opmode, initialize imu and pid w/ parameters.
     * @param hardwareMap The opmode's hardwareMap
     * @param opModeInstance The opmode (pass using "this" keyword)
     * @param telemetryInstance The opmode's telemetry
     */
    protected RobotBase(@NonNull HardwareMap hardwareMap, LinearOpMode opModeInstance, Telemetry telemetryInstance,
                            int motorTicks, double wheelDiameter) {
        opMode = opModeInstance;
        telemetry = telemetryInstance;

        //drivetrain
        frontRight = hardwareMap.get(DcMotorEx.class, "FrontRight");
        frontLeft = hardwareMap.get(DcMotorEx.class, "FrontLeft");
        backRight = hardwareMap.get(DcMotorEx.class, "BackRight");
        backLeft = hardwareMap.get(DcMotorEx.class, "BackLeft");
        frontLeft.setDirection(DcMotorEx.Direction.REVERSE);
        backLeft.setDirection(DcMotorEx.Direction.REVERSE);
        drivetrain = new DcMotorEx[]{frontRight, frontLeft, backRight, backLeft};
        for (DcMotorEx motor : drivetrain) motor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        drivetrainStrafePIDFControllers = new PIDFController[]{FRStrafePID, FLStrafePID, BRStrafePID, BLStrafePID};
        for(PIDFController pid : drivetrainStrafePIDFControllers) pid.setTolerance(1);

        drivetrainPIDFControllers = new PIDFController[]{FRPID, FLPID, BRPID, BLPID};
        for(PIDFController pid : drivetrainPIDFControllers) pid.setTolerance(1);

        headingPIDFController.setTolerance(0.8);
//        // odo
//        odoRight = hardwareMap.get(DcMotorEx.class, "odoRight");
//        odoLeft = hardwareMap.get(DcMotorEx.class, "odoLeft");
//        odoBack = hardwareMap.get(DcMotorEx.class, "odoBack");

        //imu
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters imuparams = new BNO055IMU.Parameters();
        imuparams.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        imuparams.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        imuparams.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        imuparams.loggingEnabled = true;
        imuparams.loggingTag = "IMU";
        imu.initialize(imuparams);

        // init constants
        this.motorTicks = motorTicks;
        this.wheelDiameter = wheelDiameter;
        this.wheelCircumference = Math.PI * wheelDiameter;
    }

    // ------------------------------------- MISC METHODS ------------------------------------------
    /**
     * Assigns the current position of every motor on the drivetrain to tick (position) 0
     */
    public void resetDriveTrainEncoders() { setRunMode(drivetrain, DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);}

    /**
     * Sets all the motors in motors[] to the passed run mode
     * @param motors drivetrain or slides
     * @param mode DcMotorEx.RunMode
     */
    public void setRunMode(@NonNull DcMotorEx[] motors, DcMotorEx.RunMode mode) {
        for (DcMotorEx motor : motors) motor.setMode(mode);
    }

    /**
     * Converts a distance (IN CENTIMETERS) on the field to a number of ticks for the motor to rotate about
     * @param distance (centimeters)
     * @return Ticks for the drivetrain motors to turn
     */
    public int distanceToTicks(double distance, boolean strafe) {
        return (int) Math.round( (distance/this.wheelCircumference * motorTicks) * (strafe ? 1.2:1)  );
    }

    /**
     * Pauses op mode and interactors while drivetrain motors are running using RUN_TO_POSITION
     * @param startTime Pass System.nanoTime() or opmode.time here, or -1 if you dont want to break after 10s
     */
    public void blockExecutionForRunToPosition(long startTime) {
        while (opMode.opModeIsActive()) {
            if (!drivetrain[0].isBusy()) break;
                // stop motors after 10 seconds
            else if (startTime > 0 && (System.nanoTime() - startTime > 10000000000L)) {
                for(DcMotorEx m : drivetrain) m.setVelocity(0);
                break;
            }
        }
        resetDriveTrainEncoders();
    }

    // --------------------------------- MOVEMENT METHODS ------------------------------------------

    public void brake() {
        for (DcMotorEx motor : drivetrain) motor.setVelocity(0);
    }

    /**
     * Move the robot [distance] cm forwards using encoders
     * @param distance (centimeters)
     * @param speed (ticks per second)
     */
    public void forward(double distance, double speed) {
        resetDriveTrainEncoders();
        int calculatedTicks = distanceToTicks(distance, true);
        frontRight.setTargetPosition(calculatedTicks);
        frontLeft.setTargetPosition((int)(calculatedTicks*1.05));
        backRight.setTargetPosition(calculatedTicks);
        backLeft.setTargetPosition((int)(calculatedTicks*1.05));
        for (DcMotorEx motor : drivetrain) {
            motor.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            motor.setVelocity(speed);
        }
        blockExecutionForRunToPosition(-1);
    }

    /**
     * Move the robot [distance] cm backwards using encoders
     * @param distance (centimeters)
     * @param speed (ticks per second)
     */
    public void backward(double distance, double speed) {
        resetDriveTrainEncoders();
        int calculatedTicks = distanceToTicks(distance, true);
        frontRight.setTargetPosition(-calculatedTicks);
        frontLeft.setTargetPosition((int)(-calculatedTicks*1.05));
        backRight.setTargetPosition(-calculatedTicks);
        backLeft.setTargetPosition((int)(-calculatedTicks*1.05));
        for (DcMotorEx motor : drivetrain) {
            motor.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            motor.setVelocity(speed);
        }
        blockExecutionForRunToPosition(-1);
    }

    /**
     * Move the robot [distance] cm to the right using encoders (may not be exact)
     * @param distance (centimeters)
     * @param speed (ticks per second)
     */
    public void right(double distance, double speed) {
        resetDriveTrainEncoders();
        int calculatedTicks = distanceToTicks(distance, true);
        frontRight.setTargetPosition(-calculatedTicks);
        frontLeft.setTargetPosition(calculatedTicks);
        backRight.setTargetPosition((int)(calculatedTicks*1.1));
        backLeft.setTargetPosition(-calculatedTicks);
        for (DcMotorEx motor : drivetrain) {
            motor.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            motor.setVelocity(speed);
        }
        blockExecutionForRunToPosition(System.nanoTime());
    }

    /**
     * Move the robot [distance] cm to the left using encoders (may not be exact)
     * @param distance (centimeters)
     * @param speed (ticks per second)
     */
    public void left(double distance, double speed) {
        resetDriveTrainEncoders();
        int calculatedTicks = distanceToTicks(distance, true);
        frontRight.setTargetPosition(calculatedTicks);
        frontLeft.setTargetPosition(-calculatedTicks);
        backRight.setTargetPosition(-calculatedTicks);
        backLeft.setTargetPosition(calculatedTicks);
        for (DcMotorEx motor : drivetrain) {
            motor.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            motor.setVelocity(speed);
        }
        blockExecutionForRunToPosition(System.nanoTime());
    }

    public void forwardExp(double distance) {
        resetDriveTrainEncoders();
        int tickDistance = distanceToTicks(distance, false);
        for(PIDFController pid : drivetrainPIDFControllers) {
            pid.reset();
            pid.setSetPoint(tickDistance);
        }
        double firstHeading = imu.getAngularOrientation().firstAngle;
        boolean flip = firstHeading < -90 || firstHeading > 90;
        headingPIDFController.setSetPoint(flip && firstHeading<0 ? -firstHeading:firstHeading);
        do {
            //float angleCorrection = 0;
            float heading = imu.getAngularOrientation().firstAngle;
            // account for the fact that the angle goes 179, 180, then -180, -179
            double angleCorrection = headingPIDFController.calculate(heading + (flip && heading<0 ? 360:0));
            frontRight.setVelocity(FRPID.calculate(frontRight.getCurrentPosition()) + angleCorrection);
            frontLeft.setVelocity(FLPID.calculate(frontLeft.getCurrentPosition()) - angleCorrection);
            backRight.setVelocity(BRPID.calculate(backRight.getCurrentPosition()) + 1.1*angleCorrection);
            backLeft.setVelocity(BLPID.calculate(backLeft.getCurrentPosition()) - 1.1*angleCorrection);
        } while(!drivetrainPIDFControllers[0].atSetPoint() && !drivetrainPIDFControllers[1].atSetPoint() &&
                !drivetrainPIDFControllers[2].atSetPoint() && !drivetrainPIDFControllers[3].atSetPoint());

        brake();
    }

    /**
     * Experimental strafe that uses imu to maintain orientation
     * @param distance (cm) positive is right, negative is left
     */
    public void strafeExp(double distance) {
        resetDriveTrainEncoders();
        int tickDistance = distanceToTicks(distance, false);
        for(PIDFController pid : drivetrainStrafePIDFControllers) pid.reset();
        FRStrafePID.setSetPoint(-tickDistance); //fr
        FLStrafePID.setSetPoint(tickDistance);
        BRStrafePID.setSetPoint(tickDistance); //br
        BLStrafePID.setSetPoint(-tickDistance);
        double firstHeading = imu.getAngularOrientation().firstAngle;
        boolean flip = firstHeading < -90 || firstHeading > 90;
        headingPIDFController.setSetPoint(flip && firstHeading<0 ? -firstHeading:firstHeading);
        do {
            float angleCorrection = 0;
            float heading = imu.getAngularOrientation().firstAngle;
            // account for the fact that the angle goes 179, 180, then -180, -179
            //float angleCorrection = headingPIDFController.calculate(heading + (flip && heading<0 ? 360:0));
            frontRight.setVelocity(FRStrafePID.calculate(frontRight.getCurrentPosition()) + angleCorrection);
            frontLeft.setVelocity(FLStrafePID.calculate(frontLeft.getCurrentPosition()) - angleCorrection);
            backRight.setVelocity(BRStrafePID.calculate(backRight.getCurrentPosition()) + 1.1*angleCorrection);
            backLeft.setVelocity(BLStrafePID.calculate(backLeft.getCurrentPosition()) - 1.1*angleCorrection);
        } while(!drivetrainStrafePIDFControllers[0].atSetPoint() && !drivetrainStrafePIDFControllers[1].atSetPoint() &&
                !drivetrainStrafePIDFControllers[2].atSetPoint() && !drivetrainStrafePIDFControllers[3].atSetPoint());

        for(DcMotorEx m : drivetrain) m.setVelocity(0);

    }

    /**
     * Rotates the robot [degrees] clockwise using PIDFController with imu as input. Should not be used
     * in driver-controlled programs
     * @param degrees angle to rotate to in degrees, SHOULD BE BETWEEN -180 AND 180
     */
    public void rotate(double degrees) {
        double motorSpeed = 0;
        float lastAngle = -imu.getAngularOrientation().firstAngle;
        degrees += lastAngle;
        boolean flip = degrees > 179 || degrees < -179;
        boolean sign = lastAngle >= 0; // true if angle positive, false if negative
        headingPIDFController.reset();
        headingPIDFController.setSetPoint(degrees);
        do {
            lastAngle = -imu.getAngularOrientation().firstAngle;
            if (flip) {
                // if angle was originally positive but the current angle is negative
                // add 360 to flip the sign
                if (sign && lastAngle < 0) lastAngle += 360;
                else if (!sign && lastAngle > 0) lastAngle -= 360;
            }
            motorSpeed = headingPIDFController.calculate(lastAngle);
            telemetry.addData("Motor Speed", motorSpeed);
            telemetry.addData("degrees", degrees);
            telemetry.addData("current angle", lastAngle);
            telemetry.addData("flip", flip);
            telemetry.addData("sign", sign);
            telemetry.update();
            frontRight.setVelocity(-motorSpeed);
            frontLeft.setVelocity(motorSpeed);
            backRight.setVelocity(-motorSpeed);
            backLeft.setVelocity(motorSpeed);

        } while (!headingPIDFController.atSetPoint());
        //make sure motors stop
        for(DcMotorEx m : drivetrain) m.setVelocity(0);
        headingPIDFController.reset();
    }


}