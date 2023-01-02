package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@Autonomous(name = "test auto")
public class PenetratingPerpetrator extends LinearOpMode {
    HardwareController control;

    @Override
    public void runOpMode() {
        control = new HardwareController(hardwareMap, this, telemetry);

        waitForStart();
        while(opModeIsActive()) {

            telemetry.addLine("no control");
            telemetry.update();
            for(DcMotorEx m : control.drivetrain) {
                m.setTargetPosition(145);
                m.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                m.setVelocity(300);
            }

            telemetry.addLine("waiting");
            telemetry.update();
            sleep(2000);

            control.forward(30, 400);
            sleep(1000);



            telemetry.addLine("Opmode Running");
            telemetry.update();
        }
    }
}