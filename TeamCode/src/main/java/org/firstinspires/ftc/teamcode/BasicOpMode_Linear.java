/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;


/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When a selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="Basic: Linear OpMode", group="Linear Opmode")
//@Disabled
public class BasicOpMode_Linear extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor frontLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor rearLeftDrive = null;
    private DcMotor rearRightDrive = null;

    public static double MOTOR_PPR = 384.5;
    private int     frontLeftTotalCounts = 0;
    private int     frontRightTotalCounts = 0;
    private int     rearLeftTotalCounts = 0;
    private int     rearRightTotalCounts = 0;

    private DcMotor elevatorDrive = null;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        frontLeftDrive  = hardwareMap.get(DcMotor.class, "frontLeftDrive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "frontRightDrive");
        rearLeftDrive  = hardwareMap.get(DcMotor.class, "rearLeftDrive");
        rearRightDrive = hardwareMap.get(DcMotor.class, "rearRightDrive");
        elevatorDrive = hardwareMap.get(DcMotor.class, "elevatorDrive");

        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // Pushing the left stick forward MUST make robot go forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
        rearLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        rearRightDrive.setDirection(DcMotor.Direction.REVERSE);
        elevatorDrive.setDirection(DcMotorSimple.Direction.FORWARD);

        //new
        frontLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //new2
        frontRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        rearLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rearLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        rearRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rearRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // Setup a variable for each drive wheel to save power level for telemetry
            double frontLeftPower;
            double frontRightPower;
            double rearLeftPower;
            double rearRightPower;
            double elevatorPower = gamepad1.left_trigger - gamepad1. right_trigger;

            double totalDistance;

            // POV Mode uses left stick to go forward and strafe, and right stick to turn.
            // - This uses basic math to combine motions and is easier to drive straight.
            double drive = gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double turn  = -gamepad1.right_stick_x;
            frontLeftPower    = Range.clip(drive + turn - strafe, -1.0, 1.0);
            rearLeftPower    = Range.clip(drive + turn + strafe, -1.0, 1.0);
            frontRightPower   = Range.clip(drive - turn + strafe, -1.0, 1.0);
            rearRightPower   = Range.clip(drive - turn - strafe, -1.0, 1.0);
            elevatorPower  = Range.clip(elevatorPower, -0.1, 0.1);

            // Send calculated power to wheels
            frontLeftDrive.setPower(frontLeftPower);
            rearLeftDrive.setPower(rearLeftPower);
            frontRightDrive.setPower(frontRightPower);
            rearRightDrive.setPower(rearRightPower);
            elevatorDrive.setPower(elevatorPower);

            //new
            frontLeftDrive.setPower(frontLeftPower);
            //new2
            frontRightDrive.setPower(frontRightPower);
            rearLeftDrive.setPower(rearLeftPower);
            rearRightDrive.setPower(rearRightPower);

            //new
            frontLeftTotalCounts = frontLeftDrive.getCurrentPosition();
            totalDistance = frontLeftTotalCounts / MOTOR_PPR;
            //new2
            frontRightTotalCounts = frontRightDrive.getCurrentPosition();
            rearLeftTotalCounts = rearLeftDrive.getCurrentPosition();
            rearRightTotalCounts = rearRightDrive.getCurrentPosition();

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Front Motors", "left (%.2f), right (%.2f)", frontLeftPower, frontRightPower);
            telemetry.addData("Rear Motors", "left (%.2f), right (%.2f)", rearLeftPower, rearRightPower);
            telemetry.addData("Left Stick", "x (%.2f), y (%.2f)", gamepad1.left_stick_x, gamepad1.left_stick_y);
            telemetry.addData("Right Stick", "x (%.2f), y (%.2f)", gamepad1.right_stick_x, gamepad1.right_stick_y);
            telemetry.addData("D-PAD", "l (%b), r (%b)", gamepad1.dpad_left, gamepad1.dpad_right);
            telemetry.addData("elevatorPower","(power %.2f)", elevatorPower);
            //new
            telemetry.addData("Encoder Count", "(%7d)", frontLeftTotalCounts);
            telemetry.addData("Num Rotations", "(%.2f)", totalDistance);
            //
            //new2
            telemetry.addData("Encoder Count", "(%7d)", frontRightTotalCounts);
            telemetry.addData("Encoder Count", "(%7d)", rearLeftTotalCounts);
            telemetry.addData("Encoder Count", "(%7d)", rearRightTotalCounts);
            telemetry.update();
        }
    }
}

