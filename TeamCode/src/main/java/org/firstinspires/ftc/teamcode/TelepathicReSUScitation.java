package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

import org.checkerframework.checker.index.qual.NonNegative;

import java.text.DecimalFormat;
import java.util.Arrays;

@TeleOp(name = "Main Teleop", group="Main")
public class TelepathicReSUScitation extends LinearOpMode {
    HardwareController control;
    public final DecimalFormat fourDecimals = new DecimalFormat("#.0000");

    public final float slowSpeed = 0.3f;
    public final float fastSpeed = 0.6f;

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
        float y = -gamepad.left_stick_y * speedFactor;
        float x = gamepad.left_stick_x * speedFactor * 1.75f;
        float rx = gamepad.right_stick_x * speedFactor;
        float frontLeftPower = (y + x + rx);
        float backLeftPower = (y - x + rx) * 1.15f;
        float frontRightPower = (y - x - rx);
        float backRightPower = (y + x - rx) * 1.15f;
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
        int SLIDE_DOWN_VELO = (int)(-500 * (fastSlides ? 1:0.45));
        // trigger: continuous
        if (up>0 || down>0) {
            if(up>0 && slidePos>=HardwareController.SLIDETOP-20) control.setSlidePos(HardwareController.SLIDETOP);
            else if(down>0 && slidePos<=HardwareController.SLIDEBOTTOM+20) control.setSlidePos(HardwareController.SLIDEBOTTOM);
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
            if (gamepad.x) {setPos = HardwareController.TALLPOLE; movingSlide = true; slideDirection=0;}
            else if (gamepad.y) {setPos = HardwareController.MEDIUMPOLE; movingSlide = true; slideDirection=0;}
            else if (gamepad.b) {setPos = HardwareController.SHORTPOLE; movingSlide = true; slideDirection=0;}
            else if (gamepad.a) {setPos = HardwareController.GROUND; movingSlide = true; slideDirection=0;}
            //...
            setPos = Range.clip(setPos, HardwareController.SLIDEBOTTOM, HardwareController.SLIDETOP);
            control.setSlidePos(setPos);
        }
        return slidePos;
    }

    /**
     * Sets the claw to the open or closed position
     * @param gamepad Gamepad that controls the claw (gamepad1 or gamepad2)
     */
    public void setClaw(@NonNull Gamepad gamepad) {
        if (gamepad.right_bumper) control.clawClose();
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

        float speedFactor = slowSpeed;

        waitForStart();
        control = new HardwareController(hardwareMap, this, telemetry);

        while (opModeIsActive()) {
            double iterStart = System.nanoTime();

            if (gamepad1.dpad_down) speedFactor = slowSpeed;
            else if (gamepad1.dpad_up) speedFactor = fastSpeed;

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