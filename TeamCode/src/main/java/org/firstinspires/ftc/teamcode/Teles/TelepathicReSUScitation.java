package org.firstinspires.ftc.teamcode.Teles;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

import org.checkerframework.checker.index.qual.NonNegative;
import org.firstinspires.ftc.teamcode.Robots.BumbleBee;

import java.text.DecimalFormat;
import java.util.Arrays;

@TeleOp(name = "Main Teleop", group="Main")
public class TelepathicReSUScitation extends LinearOpMode {
    BumbleBee control;
    public final DecimalFormat fourDecimals = new DecimalFormat("#.0000");

    public final float slowSpeed = 0.3f;
    public final float fastSpeed = 0.54f;
    public boolean reverseDrivetrain = false;
    public boolean movingDrivetrain = false;
    public float lastY = 0;
    public float lastX = 0;
    public float lastRX = 0;

    private double swivelPos = 0.666;
    private double liftPos = 0;

    private boolean fastSlides = true;
    private boolean movingSlide = false;
    private byte slideDirection = 1; // direction the slides were just previously moving -1 is down, 1 is up, 0 is for set heights
    private int lastSlidePos = 0;

    /**
     * Moves the drivetrain (the four wheels). Includes strafing and rotation
     * @param gamepad Gamepad that controls the drivetrain (gamepad1 or gamepad2)
     * @param speedFactor Factor which the inputs are multiplied
     * @return driveTrainPowers, an array of all the powers the drivetrain is set to (FL, BL, FR, BR)
     */
    public float[] moveDriveTrain(@NonNull Gamepad gamepad, @NonNegative float speedFactor) {
        float fbinput = gamepad.left_stick_y;
        if(gamepad.dpad_up) fbinput = 1;
        else if(gamepad.dpad_down) fbinput = -1;

        float y = -fbinput * speedFactor;
        float x = gamepad.left_stick_x * speedFactor * 1.75f;
        float rx = gamepad.right_stick_x * speedFactor * 0.6f;

        float frontLeftPower = (y + x + rx);
        float backLeftPower = (y - x + rx) * 1.17f;
        float frontRightPower = (y - x - rx);
        float backRightPower = (y + x - rx) * 1.15f;
        boolean braking = false;
//        if(y!=0 || x!=0 || rx!=0) {
//            movingDrivetrain = true;
//            lastY = y;
//            lastX = x;
//            lastRX = rx;
//        } else if(movingDrivetrain) {
//            movingDrivetrain = false;
//            braking = true;
//            frontLeftPower = -1*(lastY + lastX + lastRX);
//            backLeftPower = -1*(lastY - lastX + lastRX) * 1.17f;
//            frontRightPower = -1*(lastY - lastX - lastRX);
//            backRightPower = -1*(lastY + lastX - lastRX) * 1.15f;
//        }

        float[] driveTrainPowers = new float[] {
                Float.parseFloat(fourDecimals.format((double)frontLeftPower)),
                Float.parseFloat(fourDecimals.format((double)backLeftPower)),
                Float.parseFloat(fourDecimals.format((double)frontRightPower)),
                Float.parseFloat(fourDecimals.format((double)backRightPower)),
        };
        control.frontLeft.setPower(frontLeftPower);
        control.backLeft.setPower(backLeftPower);
        control.frontRight.setPower(frontRightPower);
        control.backRight.setPower(backRightPower);
        // if(braking) sleep(100);

        return driveTrainPowers;
    }

    /**
     * Moves the slides
     * @param gamepad Gamepad that controls the slides (gamepad1 or gamepad2)
     * @return The position of the slide in ticks ~[0,960]
     */
    public int moveSlides(@NonNull Gamepad gamepad) {
        int slidePos = control.getSlidePos();
        double up = gamepad.right_stick_y;
        double down = -gamepad.right_stick_y;

        if(gamepad.dpad_up) fastSlides = true;
        else if(gamepad.dpad_down) fastSlides = false;
        int SLIDE_UP_VELO = (int)(900 * (fastSlides ? 1:0.6));
        int SLIDE_DOWN_VELO = (int)(-650 * (fastSlides ? 1:0.45));
        // trigger: continuous
        if (up>0 || down>0) {
            if(up>0 && slidePos>= BumbleBee.SLIDETOP-20) control.setSlidePos(BumbleBee.SLIDETOP);
            else if(down>0 && slidePos<= BumbleBee.SLIDEBOTTOM+20) control.setSlidePos(BumbleBee.SLIDEBOTTOM);
            else {
                movingSlide = true;
                double velo = 0;
                if (up > 0) { // ticks/s
                    velo = SLIDE_UP_VELO; slideDirection = 1; }
                else {
                    velo = SLIDE_DOWN_VELO; slideDirection = -1; }
                control.setSlideVelo(velo);
            }
        // a,b,x,y, preset positions
        } else {
            if (movingSlide) {
                movingSlide = false;
                lastSlidePos = slidePos + 10*slideDirection;
            }
            int setPos = lastSlidePos;
            if (gamepad.x) {setPos = BumbleBee.TALLPOLE; movingSlide = true; slideDirection=0;}
            else if (gamepad.y) {setPos = BumbleBee.MEDIUMPOLE; movingSlide = true; slideDirection=0;}
            else if (gamepad.b) {setPos = BumbleBee.SHORTPOLE; movingSlide = true; slideDirection=0;}
            else if (gamepad.a) {setPos = BumbleBee.GROUND; movingSlide = true; slideDirection=0;}
            //...
            setPos = Range.clip(setPos, BumbleBee.SLIDEBOTTOM, BumbleBee.SLIDETOP);
            control.setSlidePos(setPos);
        }
        return slidePos;
    }

    /**
     * Sets the claw to the open or closed position
     * @param gamepad Gamepad that controls the claw (gamepad1 or gamepad2)
     */
    public void setClaw(@NonNull Gamepad gamepad) {
        if (gamepad.right_bumper) {
            control.clawClose();
            sleep(300);
            control.setLift(1);
        }
        else if (gamepad.left_bumper) control.clawOpen();
    }

    /**
     * Moves the servo that rotates the arm
     * @param gamepad Gamepad that controls the swivel (gamepad1 or gamepad2)
     */
    public void moveSwivel(@NonNull Gamepad gamepad) {
        if (gamepad.left_bumper && gamepad.right_bumper) swivelPos = 0.666; // straighten arm
        else if (gamepad.right_bumper) swivelPos += 0.01;
        else if (gamepad.left_bumper) swivelPos -= 0.01;
        swivelPos = Range.clip(swivelPos, 0.4, 0.9);
        control.setSwivel(swivelPos);
    }

    /**
     * Moves the servo that raises the claw
     * @param gamepad Gamepad that controls the lift (gamepad1 or gamepad2)
     */
    public void moveLift(@NonNull Gamepad gamepad) {
        liftPos = control.lift.getPosition();
        if (gamepad.right_trigger > 0) control.setLift(liftPos + 0.02);
        else if (gamepad.left_trigger > 0) control.setLift(liftPos - 0.02);
    }

    @Override
    public void runOpMode() {
        telemetry.addLine("Before starting, make sure the slides are at the bottom");
        telemetry.update();

        float speedFactor = fastSpeed;
        waitForStart();
        control = new BumbleBee(hardwareMap, this, telemetry);
        for(DcMotorEx m:control.drivetrain) m.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        while (opModeIsActive()) {
            double iterStart = System.nanoTime();

//            if (gamepad1.dpad_down) speedFactor = slowSpeed;
//            else if (gamepad1.dpad_up) speedFactor = fastSpeed;

            // ----------------------------------- Gamepad 1----------------------------------------
            float[] driveTrainPowers = moveDriveTrain(gamepad1, speedFactor);
            setClaw(gamepad1);

            // ----------------------------------- Gamepad 2----------------------------------------
            int slidePos = moveSlides(gamepad2);
            moveSwivel(gamepad2);
            moveLift(gamepad2);

            double iterEnd = System.nanoTime();
            telemetry.addData("Speed factor", speedFactor);
            telemetry.addData("Drivetrain powers", Arrays.toString(driveTrainPowers));
            telemetry.addData("Slide position", slidePos);
            telemetry.addData("Slide speed", fastSlides);
            telemetry.addData("Slide velocity", control.leftSlide.getVelocity());
            telemetry.addData("Swivel position", swivelPos);
            telemetry.addData("Lift position", liftPos);
            telemetry.addData("Iteration duration (ms)", fourDecimals.format((iterEnd-iterStart)/1000000));
            telemetry.update();
        }
    }
}