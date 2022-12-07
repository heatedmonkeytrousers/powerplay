package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.Claw.MAX_POS;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.opencv.core.Mat;

public class Elevator extends Thread {

    public static int LOW_POSITION = 3941;
    public static int MIDDLE_POSITION = 6647;
    public static int HIGH_POSITION = 9100;

    private DcMotor elevatorDrive;
    private int totalCounts;
    private Gamepad gamepad;

    private Claw claw;

    static final double MAX_POS     =  0.25;
    static final double MIN_POS     =  0.1;

    public Elevator(DcMotor elevatorDrive, Gamepad gamepad, Claw claw) {
        this.elevatorDrive = elevatorDrive;
        this.gamepad = gamepad;
        this.claw = claw;
    }
    public int getTotalCounts() {
        return totalCounts;
    }
    public boolean isUp() {
        return elevatorDrive.getCurrentPosition() > LOW_POSITION/2;
    }

    public void setPosition(double power, int position) {
        power = Range.clip(power, -1.0, 1.5);
        elevatorDrive.setPower(power);
        if (position < 0) {
            position = 0;
        }
        if (position == 0) {
            elevatorDrive.setPower(-1);
            while (totalCounts > 0) {
                totalCounts = elevatorDrive.getCurrentPosition();
                if (gamepad.start) {
                    elevatorDrive.setPower(0);
                    break;
                }
            }
            elevatorDrive.setPower(0.1);

            while (totalCounts < 0) {
                totalCounts = elevatorDrive.getCurrentPosition();
            }
        } else {

            while (totalCounts < position) {
                totalCounts = elevatorDrive.getCurrentPosition();
                if (gamepad.start) {
                    elevatorDrive.setPower(0);
                    break;
                }
            }
        }
    }

    public void drop () {
        elevatorDrive.setPower(1);
        elevatorDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        elevatorDrive.setTargetPosition(totalCounts - 500);
        while (elevatorDrive.isBusy()) {
            if (gamepad.start) {
                elevatorDrive.setPower(0);
                elevatorDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                break;
            }
        }
        elevatorDrive.setPower(0);
        totalCounts = elevatorDrive.getCurrentPosition();
        claw.openClaw();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {

        }

        elevatorDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            double elevatorPower = gamepad.left_trigger - gamepad.right_trigger;
            elevatorPower = Range.clip(elevatorPower, -1.0, 1.5);
            elevatorDrive.setPower(elevatorPower);
            totalCounts = elevatorDrive.getCurrentPosition();

            // The set buttons for the elevators highs
            if (gamepad.a) {
                setPosition(-1, 0);

            } else if (gamepad.b) {

                setPosition(1, LOW_POSITION);

            } else if (gamepad.x) {

                setPosition(1, MIDDLE_POSITION);

            } else if (gamepad.y) {
                    setPosition(1, HIGH_POSITION);

            } else if (gamepad.back) {
                drop();
            }
            else {
                elevatorDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }
        }
    }
}
