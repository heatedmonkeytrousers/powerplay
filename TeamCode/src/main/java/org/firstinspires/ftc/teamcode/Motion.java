package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

public class Motion extends Thread{
    private static int LOW_POSITION = 3941;
    private static int MIDDLE_POSITION = 6647;
    private static int HIGH_POSITION = 9700;

    private DcMotor frontLeftDrive;
    private DcMotor frontRightDrive;
    private DcMotor rearLeftDrive;
    private DcMotor rearRightDrive;
    private Gamepad gamepad;

    private Elevator elevator;
    private int totalCounts;

    public Motion (DcMotor frontLeftDrive, DcMotor frontRightDrive, DcMotor rearLeftDrive, DcMotor rearRightDrive, Gamepad gamepad, Elevator elevator) {
        this.frontLeftDrive = frontLeftDrive;
        this.frontRightDrive = frontRightDrive;
        this.rearLeftDrive = rearLeftDrive;
        this.rearRightDrive = rearRightDrive;
        this.elevator = elevator;
        this.gamepad = gamepad;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            // Setup a variable for each drive wheel to save power level for telemetry
            double frontLeftPower;
            double frontRightPower;
            double rearLeftPower;
            double rearRightPower;

            // POV Mode uses left stick to go forward and strafe, and right stick to turn.
            // - This uses basic math to combine motions and is easier to drive straight.
            double drive = gamepad.left_stick_y;
            double strafe = gamepad.left_stick_x;
            double turn = -gamepad.right_stick_x;

            frontLeftPower = Range.clip(drive + turn - strafe, -1.0, 1.0);
            rearLeftPower = Range.clip(drive + turn + strafe, -1.0, 1.0);
            frontRightPower = Range.clip(drive - turn + strafe, -1.0, 1.0);
            rearRightPower = Range.clip(drive - turn - strafe, -1.0, 1.0);

            if (elevator.isUp()) {
                frontLeftPower *= 0.2;
                rearLeftPower *= 0.2;
                frontRightPower *= 0.2;
                rearRightPower *=0.2;
            }
            // Send calculated power to wheels
            frontLeftDrive.setPower(frontLeftPower);
            rearLeftDrive.setPower(rearLeftPower);
            frontRightDrive.setPower(frontRightPower);
            rearRightDrive.setPower(rearRightPower);
        }
    }
}
