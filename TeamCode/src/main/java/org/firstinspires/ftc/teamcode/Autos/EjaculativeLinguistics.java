package org.firstinspires.ftc.teamcode.Autos;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.CV.SleeveDetectPipeline;
import org.firstinspires.ftc.teamcode.Robots.BumbleBee;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

@Config
@Autonomous(name = "test auto2", group="Test")
public class EjaculativeLinguistics extends LinearOpMode {
    BumbleBee control;
    OpenCvWebcam webcam;
    public static int n_cycles = 1;
    public static int angle = 96;
    public static int angle2 = -10;
    public static int zone = 1;
    public static int dist1 = 12;
    public static int dist2 = 50;
    public static int dist3 = 24;
    public static int dist4 = 55;
    public static int dist5 = 55;
    public static int height1 = 110;
    public static boolean pushCone = true;
    public static boolean cycle = false;

    @Override
    public void runOpMode() {
        control = new BumbleBee(hardwareMap, this, telemetry);

        control.clawClose();
        sleep(500);
        control.setSwivel(0.666);
        control.setLift(1);

        waitForStart();
        telemetry.addData("Status", "running");
        telemetry.update();

        control.forward(13, 250);
        control.setSlidePos(BumbleBee.SHORTPOLE);
        sleep(500);
        control.setSwivel(0.4);
        control.setLift(0);
        sleep(800);
        control.clawOpen();
        sleep(200);
        control.setSwivel(0.66);
        control.setSlidePos(BumbleBee.GROUND);
        sleep(500);

        if (! pushCone) return;
        // grab signal cone and move it forward and move to stack
        control.forward(dist1, 500);
        sleep(600);
        control.forward(dist2, 500);
        sleep(600);
        control.setLift(1);
        control.rotate(angle);
        sleep(500);
        control.forward(dist3, 500);
        sleep(600);

        if (! cycle) return;
        // cycle
        for(int i=0; i<n_cycles; i++) {
            control.setSlidePos(height1);
            sleep(700);
            control.setLift(0);
            sleep(800);
            control.clawClose();
            sleep(200);
            control.setLift(1);
            sleep(200);
            control.backward(dist4, 500);
            sleep(1000);
            control.rotate(angle2);
            control.setSlidePos(BumbleBee.TALLPOLE);
            sleep(1000);
            control.setSwivel(0.4);
            sleep(1000);
            control.setLift(0);
            sleep(300);
            control.clawOpen();
            control.setLift(1);
            sleep(100);
            control.setSwivel(0.66);
            control.forward(dist5, 500);
            sleep(500);
        }

        // park
        //control.right(56*(zone-1), 200);
        switch (zone) {
            case 1:
                break;
            case 2:
                control.right(58, 200);
                break;
            case 3:
                control.right(140, 200);
                break;
        }

        telemetry.addData("Status", "completed");
        telemetry.update();
        sleep(2000);
    }

}