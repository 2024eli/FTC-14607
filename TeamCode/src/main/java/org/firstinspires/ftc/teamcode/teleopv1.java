package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="Robot: Simple POV", group="Robot")
public class teleopv1 extends LinearOpMode {

    /* Declare OpMode members. */
    public DcMotor frontLeft  = null;
    public DcMotor  frontRight  = null;
    public DcMotor  backLeft  = null;
    public DcMotor backRight = null;
//    public DcMotor leftSlide = null;
//    public DcMotor rightSlide = null;
//    public Servo leftClaw    = null;
//    public Servo rightClaw   = null;


    double clawOffset = 0;
    double clawoffset = 0;

    public static final double MID_SERVO   =  0.5 ;
    public static final double CLAW_SPEED  = 0.0003 ;                 // sets rate to move servo
    public static final double ARM_UP_POWER    =  0.45 ;
    public static final double ARM_DOWN_POWER  = -0.45 ;

    @Override
    public void runOpMode() {
        double left;
        double right;
        double drive;
        double turn;
        double max;

        final double dampslides = 0.6;
        final double dampSpeedRatio = 0.35;
        final double dampTurnRatio = 0.3;
        final double backdampspeedRatio = 0.45;
        final double backdampturnRatio = 0.3;

        // Define and Initialize Motors
//        frontLeft  = hardwareMap.get(DcMotor.class, "frontLeft");
//        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
//        backLeft    = hardwareMap.get(DcMotor.class, "backLeft");
//        backRight = hardwareMap.get(DcMotor.class, "backRight");
        frontLeft  = hardwareMap.get(DcMotor.class, "FrontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "FrontRight");
        backLeft    = hardwareMap.get(DcMotor.class, "BackLeft");
        backRight = hardwareMap.get(DcMotor.class, "BackRight");
//        leftSlide = hardwareMap.get(DcMotor.class, "LeftSlide");
//        rightSlide= hardwareMap.get(DcMotor.class, "RightSlide");

        /*frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);*/

        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // Pushing the left stick forward MUST make robot go forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips

        frontLeft.setDirection(DcMotor.Direction.REVERSE);

        backLeft.setDirection(DcMotor.Direction.REVERSE);

//        leftSlide.setDirection(DcMotor.Direction.REVERSE);
//
//        rightSlide.setDirection(DcMotor.Direction.REVERSE);
//
//        // Define and initialize ALL installed servos.
//        leftClaw  = hardwareMap.get(Servo.class, "servo1");
//        rightClaw = hardwareMap.get(Servo.class, "servo2");
//        leftClaw.setPosition(MID_SERVO);
//        rightClaw.setPosition(MID_SERVO);

        // Send telemetry message to signify robot waiting;
        telemetry.addData(">", "Robot Ready.  Press Play.");    //
        telemetry.update();

        // set motors to 0's
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backRight.setPower(0);
        backLeft.setPower(0);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            //reversing the left stick y value
            double y = Range.clip(-gamepad1.left_stick_y, -1,1);
            //left stick x value
            double x = Range.clip(-gamepad1.left_stick_x,-1,1);
            //right stick x value
            double rx = Range.clip(gamepad1.right_stick_x,-1,1);



            double flPower = (y - x)*dampSpeedRatio + dampTurnRatio*rx;
            double frPower = (y + x)*dampSpeedRatio - dampTurnRatio*rx;
            double blPower = (y + x)*backdampspeedRatio + backdampturnRatio*rx;
            double brPower = (y - x)*backdampspeedRatio - backdampturnRatio*rx;

            double maxFront = Math.max(flPower, frPower);
            double maxBack = Math.max(blPower,brPower);
            double maxPower = Math.max(maxFront, maxBack);

            if(maxPower > 1.0) {
                flPower /= maxPower;
                frPower /= maxPower;
                blPower /= maxPower;
                brPower /= maxPower;
            }
            //finally moving the motors
            frontLeft.setPower(flPower);
            backLeft.setPower(blPower);
            frontRight.setPower(frPower);
            backRight.setPower(brPower);


            // Use gamepad left & right Bumpers to open and close the claw
            if (gamepad1.right_bumper)
                clawOffset += CLAW_SPEED;
            else if (gamepad1.left_bumper)
                clawOffset -= CLAW_SPEED;

            if (gamepad1.a)
                clawoffset += CLAW_SPEED;
            else if (gamepad1.b)
                clawoffset -= CLAW_SPEED;


//            // Move both servos to new position.  Assume servos are mirror image of each other.
//            clawOffset = Range.clip(clawOffset, -0.5, 0.5);
//            leftClaw.setPosition(MID_SERVO + clawOffset);
//            rightClaw.setPosition(MID_SERVO - clawoffset);

        }
    }
}