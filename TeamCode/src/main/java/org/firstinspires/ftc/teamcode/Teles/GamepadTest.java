package org.firstinspires.ftc.teamcode.Teles;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.Robots.BumbleBee;

@TeleOp(name = "GamePadTest", group = "Test")
public class GamepadTest extends OpMode {
    private DcMotor leftFrontDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor rightBackDrive = null;

    //Driving Variables
    final float driveMagnitude = 2; //scales the inputs from gamepad left joystick for drivemotors

    @Override
    public void init(){ //if "init" is pressed, this function runs once
        //Initializing DriveTrain (omni wheels)
        leftFrontDrive  = hardwareMap.get(DcMotor.class, "left_front_drive");
        leftBackDrive  = hardwareMap.get(DcMotor.class, "left_back_drive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "right_front_drive");
        rightBackDrive = hardwareMap.get(DcMotor.class, "right_back_drive");

        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);

        telemetry.addData("Status", "Initialized DriveTrain");
        telemetry.update();
    }

    public void logGamepadInfo(){
        //Left Joy Stick Log (float)
        telemetry.addData("Left Joy Stick X", gamepad1.left_stick_x);
        telemetry.addData("Left Joy Stick Y", gamepad1.left_stick_y);
        telemetry.addLine();
        //Right Joy Stick Log (float)
        telemetry.addData("Right Joy Stick X", gamepad1.right_stick_x);
        telemetry.addData("Left Joy Stick Y", gamepad1.right_stick_y);
        telemetry.addLine();

        //Directional Pads (booleans)
        telemetry.addData("dpad_up", gamepad1.dpad_up);
        telemetry.addData("dpad_down", gamepad1.dpad_down);
        telemetry.addData("dpad_left", gamepad1.dpad_left);
        telemetry.addData("dpad_right", gamepad1.dpad_right);
        telemetry.addLine();

        //X, A, B, Y Buttons
        telemetry.addData("X Button", gamepad1.x);
        telemetry.addData("A Button", gamepad1.a);
        telemetry.addData("B Button", gamepad1.b);
        telemetry.addData("Y Button", gamepad1.y);
        telemetry.addLine();

        //Bumpers (buttons infront) & Triggers (buttons in the back)
        telemetry.addData("Left Bumper", gamepad1.left_bumper);
        telemetry.addData("Left Trigger", gamepad1.left_trigger);
        telemetry.addData("Right Bumper", gamepad1.right_bumper);
        telemetry.addData("Right Trigger", gamepad1.right_trigger);

        telemetry.update();
    }

    @Override
    public void init_loop(){ //this function continuously runs until "start" is pressed
        telemetry.addLine("Test out the GamePad Buttons");
        telemetry.addLine();
        logGamepadInfo();
    }

    @Override
    public void start(){ //if "start" is pressed, this code is ran once

    }



    @Override
    public void loop(){ //this code runs after start() continuously until "Stop" is pressed
        double axial = -gamepad1.left_stick_y; //Power for y
        double lateral = gamepad1.left_stick_x; //Power for x
        //Axial and lateral can be thought of as a unit coordinate plane
        double yaw = gamepad1.right_stick_x; //Essentially, the angular direction of drivetrain

        //Ask someone to teach me math of omni wheels and implement here
    }

    @Override
    public void stop(){
        telemetry.addLine("Programming Ending...");
        telemetry.update();
    }
}
