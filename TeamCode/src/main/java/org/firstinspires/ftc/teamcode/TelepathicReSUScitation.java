package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;


import org.checkerframework.checker.index.qual.NonNegative;

import java.text.DecimalFormat;
import java.util.Arrays;

@TeleOp(name = "Main Teleop")
public class TelepathicReSUScitation extends LinearOpMode {
    HardwareController control;
    public final float slowSpeed = 0.3f;
    public final float fastSpeed = 0.5f;
    public final DecimalFormat fourDecimals = new DecimalFormat("#.0000");
    private volatile double swivelPos = 0.666;
    private volatile double liftPos = 0;
    private volatile boolean movingSlide = false;
    private volatile byte slideDirection = 1; // direction the slides were just previously moving -1 is down, 1 is up, 0 is for set heights
    private volatile int lastSlidePos = 0;

    /**
     * Moves the drivetrain (the four wheels). Includes strafing and rotation
     * @param gamepad Gamepad that controls the drivetrain (gamepad1 or gamepad2)
     * @param speedFactor Factor which the inputs are multiplied
     * @return driveTrainPowers, an array of all the powers the drivetrain is set to (FL, BL, FR, BR)
     */
    public float[] moveDriveTrain(@NonNull Gamepad gamepad, @NonNegative float speedFactor) {
        if (gamepad.dpad_up) speedFactor = fastSpeed;
        else if (gamepad.dpad_down) speedFactor = slowSpeed;

        float y = -gamepad.left_stick_y * speedFactor;
        float x = gamepad.left_stick_x * speedFactor * 1.5f;
        float rx = gamepad.right_stick_x * speedFactor;
        float frontLeftPower = (y + x + rx);
        float backLeftPower = (y - x + rx) * 1.15f;
        float frontRightPower = (y - x - rx);
        float backRightPower = (y + x - rx) * 1.18f;
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
        if (gamepad.right_trigger>0 || gamepad.left_trigger>0) {
            if(gamepad.right_trigger>0 && slidePos>=HardwareController.SLIDETOP-20) control.setSlidePos(HardwareController.SLIDETOP);
            else if(gamepad.left_trigger>0 && slidePos<=HardwareController.SLIDEBOTTOM+20) control.setSlidePos(HardwareController.SLIDEBOTTOM);
            else {
                movingSlide = true;
                double velo = 0;
                if (gamepad.right_trigger > 0) { velo = 900; slideDirection = 1; }
                else if (gamepad.left_trigger > 0) { velo = -550; slideDirection = -1; }
                control.setSlideVelo(velo);
            }
        } else {
            if (movingSlide) {
                movingSlide = false;
                lastSlidePos = slidePos + 40*slideDirection;
            }
            int setPos = lastSlidePos;
            if (gamepad.x) {setPos = HardwareController.TALLPOLE; movingSlide = true; slideDirection=0;}
            else if (gamepad.y) {setPos = HardwareController.MEDIUMPOLE; movingSlide = true; slideDirection=0;}
            else if (gamepad.b) {setPos = HardwareController.SHORTPOLE; movingSlide = true; slideDirection=0;}
            else if (gamepad.a) {setPos = HardwareController.GROUND; movingSlide = true; slideDirection=0;}
            //...
            setPos = Math.max(HardwareController.SLIDEBOTTOM, Math.min(setPos, HardwareController.SLIDETOP));
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
        else if (gamepad.right_bumper) swivelPos += 0.0075;
        else if (gamepad.left_bumper) swivelPos -= 0.0075;
        swivelPos = Math.max(0.4, Math.min(swivelPos, 0.9));
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
            int slidePos = moveSlides(gamepad1);
            setClaw(gamepad1);

            // ----------------------------------- Gamepad 2----------------------------------------
            moveSwivel(gamepad2);
            moveLift(gamepad2);

            double iterEnd = System.nanoTime();
            telemetry.addData("Speed factor", speedFactor);
            telemetry.addData("Drivetrain powers", Arrays.toString(driveTrainPowers));
            telemetry.addData("Slide position", slidePos);
            telemetry.addData("Slide velocity", control.leftSlide.getVelocity());
            telemetry.addData("Swivel position", swivelPos);
            telemetry.addData("Lift position", liftPos);
            telemetry.addData("Iteration duration (ms)", fourDecimals.format((iterEnd-iterStart)/1000000));
            telemetry.update();
        }
    }
}