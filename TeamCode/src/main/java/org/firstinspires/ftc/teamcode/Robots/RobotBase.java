package org.firstinspires.ftc.teamcode.Robots;

import androidx.annotation.NonNull;

import java.lang.Math;

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
public abstract class RobotBase {
    // op mode
    public LinearOpMode opMode;
    public Telemetry telemetry;
    // drivetrain
    public DcMotorEx frontLeft, frontRight, backLeft, backRight;
    public DcMotorEx[] drivetrain;
//    public DcMotorEx odoRight, odoLeft, odoBack;
    // sensor/controllers
    public BNO055IMU imu;
    public PIDFController headingPIDFController;
    public PIDFController[] drivetrainPIDFControllers;
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

        drivetrainPIDFControllers = new PIDFController[]{
            new PIDFController(1,0,0,0), // FR
            new PIDFController(1,0,0,0), // FL
            new PIDFController(1,0,0,0), // BR
            new PIDFController(1,0,0,0), // BL
        };
        for(PIDFController pid : drivetrainPIDFControllers) pid.setTolerance(1);

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

        headingPIDFController = new PIDFController(0.005, 0, 0, 0);
        headingPIDFController.setIntegrationBounds(-0.15, 0.15);
        headingPIDFController.setTolerance(1);

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
    public void setRunMode(DcMotorEx[] motors, DcMotorEx.RunMode mode) {
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
            else if (startTime > 0 && (System.nanoTime() - startTime > 10000000000l)) {
                for(DcMotorEx m : drivetrain) m.setVelocity(0);
                break;
            }
        }
        resetDriveTrainEncoders();
    }

    // --------------------------------- MOVEMENT METHODS ------------------------------------------

    /**
     * Move the robot [distance] cm forwards using encoders
     * @param distance (centimeters)
     * @param speed (ticks per second)
     */
    public void forward(double distance, double speed) {
        resetDriveTrainEncoders();
        int ticksToTravel = distanceToTicks(distance, false);
        for (DcMotorEx motor : drivetrain) {
            motor.setTargetPosition(ticksToTravel);
            motor.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            motor.setVelocity(speed);
        }
        blockExecutionForRunToPosition(System.nanoTime());
    }

    /**
     * Move the robot [distance] cm backwards using encoders
     * @param distance (centimeters)
     * @param speed (ticks per second)
     */
    public void backward(double distance, double speed) {
        resetDriveTrainEncoders();
        for (DcMotorEx motor : drivetrain) {
            motor.setTargetPosition(-distanceToTicks(distance, false));
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

    public void rightExp(double distance, double power) {
        resetDriveTrainEncoders();
        int tickDistance = distanceToTicks(distance, true);
        for(PIDFController pid : drivetrainPIDFControllers) pid.reset();
        drivetrainPIDFControllers[0].setSetPoint(tickDistance);
        drivetrainPIDFControllers[1].setSetPoint(-tickDistance);
        drivetrainPIDFControllers[2].setTolerance(-tickDistance);
        drivetrainPIDFControllers[3].setSetPoint(tickDistance);
        double firstHeading = imu.getAngularOrientation().firstAngle;
        headingPIDFController.setSetPoint(firstHeading);
        do {
            double angleCorrection = headingPIDFController.calculate(imu.getAngularOrientation().firstAngle);
            frontRight.setVelocity(drivetrainPIDFControllers[0].calculate(frontRight.getCurrentPosition()) + angleCorrection);
            frontLeft.setVelocity(drivetrainPIDFControllers[1].calculate(frontLeft.getCurrentPosition()) - angleCorrection);
            backRight.setVelocity(drivetrainPIDFControllers[2].calculate(backRight.getCurrentPosition()) + angleCorrection);
            backLeft.setVelocity(drivetrainPIDFControllers[3].calculate(backLeft.getCurrentPosition()) - angleCorrection);
        } while(!drivetrainPIDFControllers[0].atSetPoint() && !drivetrainPIDFControllers[1].atSetPoint() &&
                !drivetrainPIDFControllers[2].atSetPoint() && !drivetrainPIDFControllers[3].atSetPoint());

        for(DcMotorEx m : drivetrain) m.setVelocity(0);

    }

    /**
     * Rotates the robot [degrees] clockwise using PIDFController with imu as input. Should not be used
     * in driver-controlled programs
     * @param degrees angle to rotate to in degrees, SHOULD BE BETWEEN -180 AND 180
     */
    public void rotate(double degrees) {
        double motorSpeed = 500;
        if(degrees < 0) motorSpeed = -motorSpeed;
        float lastAngle = imu.getAngularOrientation().firstAngle;
        degrees += lastAngle;
        headingPIDFController.reset();
        headingPIDFController.setSetPoint(degrees);
        do {
            lastAngle = imu.getAngularOrientation().firstAngle;
            motorSpeed = 4.8 * headingPIDFController.calculate(lastAngle);
            frontRight.setVelocity(-motorSpeed);
            frontLeft.setVelocity(motorSpeed);
            backRight.setVelocity(-motorSpeed);
            backLeft.setVelocity(motorSpeed);

        } while (!headingPIDFController.atSetPoint());
        //make sure motors stop
        for(DcMotorEx m : drivetrain) m.setVelocity(0);
        headingPIDFController.reset();
    }

    // ------------------------------------ INTERACTOR METHODS -------------------------------------

    // none

}