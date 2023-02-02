package org.firstinspires.ftc.teamcode.Robots;

import androidx.annotation.NonNull;

import java.lang.Math;

import com.arcrobotics.ftclib.controller.PIDFController;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * 2022-2023 PowerPlay v1 Robot
 */
public class BumbleBee extends RobotBase {
    // interactors
    public Servo claw, lift, swivel;
    public DcMotorEx rightSlide, leftSlide;
    public DcMotorEx[] slides;
    // controllers
    public PIDFController slidepidfcontroller;
    // other constants
    public final static int SLIDEBOTTOM = 0;
    public final static int SLIDETOP = 960;
    public final static int TALLPOLE = 960; //ticks
    public final static int MEDIUMPOLE = 660;
    public final static int SHORTPOLE = 360;
    public final static int GROUND = 0;

    /**
     * Instantiate all variables related to the robot and the opmode, initialize imu and pid w/ parameters.
     * Before calling constructor, make sure
     *      * slides are at the bottom
     * @param hardwareMap The opmode's hardwareMap
     * @param opModeInstance The opmode (pass using "this" keyword)
     * @param telemetryInstance The opmode's telemetry
     */
    public BumbleBee(@NonNull HardwareMap hardwareMap, LinearOpMode opModeInstance, Telemetry telemetryInstance) {
        super(hardwareMap, opModeInstance, telemetryInstance, 145, 9.6);

        // arm
        claw = hardwareMap.get(Servo.class, "claw");
        lift = hardwareMap.get(Servo.class, "lift");
        swivel = hardwareMap.get(Servo.class, "swivel");

        //slides
        rightSlide = hardwareMap.get(DcMotorEx.class, "RightSlide");
        leftSlide = hardwareMap.get(DcMotorEx.class, "LeftSlide");
        slides = new DcMotorEx[]{rightSlide, leftSlide};
        for(DcMotorEx m: slides) {
            m.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
            m.setVelocityPIDFCoefficients(15.0, 2.0, 0.0, 0);
            m.setPositionPIDFCoefficients(10.0);
        }
        resetSlideEncoders();

        slidepidfcontroller = new PIDFController(50, 0.05, 0, 0);
        //slidepidfcontroller.setIntegrationBounds(-5, 5);
        slidepidfcontroller.setTolerance(3);
    }

    // ------------------------------------- MISC METHODS ------------------------------------------

    /**
     * Assigns the current position of both slide motors to tick (position) 0.
     * MAKE SURE THE SLIDES ARE AT THE BOTTOM WHEN DOING THIS.
     */
    public void resetSlideEncoders() {
        setRunMode(slides, DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        // behavior may vary between motor models, so after resetting set all slides back to RUN_TO_POSITION
        for (DcMotorEx motor: slides) {
            motor.setTargetPosition(motor.getCurrentPosition());
            motor.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        }
    }

    // --------------------------------- MOVEMENT METHODS ------------------------------------------

    // rotate using power
    public void rotateP(double degrees) {
        double motorSpeed = 500;
        if(degrees < 0) motorSpeed = -motorSpeed;
        float lastAngle = imu.getAngularOrientation().firstAngle;
        degrees += lastAngle;
        headingPIDFController.reset();
        headingPIDFController.setSetPoint(degrees);
        setRunMode(drivetrain, DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        do {
            lastAngle = imu.getAngularOrientation().firstAngle;
            motorSpeed = 0.01 * headingPIDFController.calculate(lastAngle);
            frontRight.setPower(-motorSpeed);
            frontLeft.setPower(motorSpeed);
            backRight.setPower(-motorSpeed);
            backLeft.setPower(motorSpeed);

        } while (!headingPIDFController.atSetPoint());
        //make sure motors stop
        for(DcMotorEx m : drivetrain) m.setPower(0);
        headingPIDFController.reset();
    }

    // ------------------------------------ INTERACTOR METHODS -------------------------------------

    /**
     * Returns the slides current position
     * @return Left slides current position ~(0-960)
     */
    public int getSlidePos() { return leftSlide.getCurrentPosition(); }

    /**
     * Sets the velocity of the slides
     * @param velocity
     */
    public void setSlideVelo(double velocity){
        double currentPos = getSlidePos();
        // prevents setting velocity if it will exceed the limits
        if ( (currentPos>=955 && velocity>0) || (currentPos<=0 && velocity<0) ) velocity = 0;
        setRunMode(slides, DcMotorEx.RunMode.RUN_USING_ENCODER);
        rightSlide.setVelocity(-velocity);
        leftSlide.setVelocity(velocity);
    }

    /**
     * Extend/retract slides to desired height using encoders
     * @param height - (ticks)
     */
    public void setSlidePos(int height) {
        height = Range.clip(height, SLIDEBOTTOM, SLIDETOP);
        int change = height - getSlidePos();
        rightSlide.setTargetPosition(-height);
        leftSlide.setTargetPosition(height);
        for(DcMotorEx slide:slides) {
            slide.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            slide.setVelocity( (change>0)? 1200:1000 );
        }
    }

    /**
     * Set arm lifter to pos, where 0 < pos < 1.0
     * @param pos
     */
    public void setLift(double pos) {
        double position = Range.clip(pos, 0, 1);
        lift.setPosition(position);
    }

    /**
     * Sets arm swivel to pos
     * @param pos 0.4 < pos < 0.9
     */
    public void setSwivel(double pos) {
        double position = Range.clip(pos, 0.4, 0.9);
        swivel.setPosition(position);
    }

    /**
     * Sets claw to open position
     */
    public void clawOpen() {
        this.claw.setPosition(0);
    }

    /**
     * Sets claw to closed position
     */
    public void clawClose() {
        this.claw.setPosition(0.35);
    }

    /**
     * Returns true if the claw is in the closed position
     * @return Whether or not the claw is closed
     */
    public boolean isClawClose() { return this.claw.getPosition() == 0.3; }

}