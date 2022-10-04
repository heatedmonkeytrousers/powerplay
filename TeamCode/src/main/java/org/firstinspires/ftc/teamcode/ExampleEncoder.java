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

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
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

@TeleOp(name="Encoder: Linear OpMode", group="Linear Opmode")
//@Disabled
public class ExampleEncoder extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor frontLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor rearLeftDrive = null;
    private DcMotor rearRightDrive = null;
    public static double MOTOR_PPR = 384.5;
    private int     totalCounts = 0;

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

        // This code configures the left front motor to work with the encoder.  The first command
        // indicates the relative direction the motor runs versus our robot.  The second command
        // zeros the encoder.  The third command enables the motor to be used with the encoder.
        // NOTE:  the encoder is part of the DC Motor class.
        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        frontLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // These are extraneous lines of code, left over from the basic op mode.
        // Can you extend the use of the encoders to use these three motors as well?
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
        rearLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        rearRightDrive.setDirection(DcMotor.Direction.REVERSE);

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
            double totalDistance;

            // POV Mode uses left stick to go forward and strafe, and right stick to turn.
            // - This uses basic math to combine motions and is easier to drive straight.
            double drive = gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double turn  = -gamepad1.right_stick_x;
            frontLeftPower    = Range.clip(drive, -1.0, 1.0);
            rearLeftPower    = Range.clip(drive + turn + strafe, -1.0, 1.0);
            frontRightPower   = Range.clip(drive - turn + strafe, -1.0, 1.0);
            rearRightPower   = Range.clip(drive - turn - strafe, -1.0, 1.0);

            // Send calculated power to wheels
            frontLeftDrive.setPower(frontLeftPower);

            // Since our focus is on the front left motor, I've disabled the others for safety.
            //rearLeftDrive.setPower(rearLeftPower);
            //frontRightDrive.setPower(frontRightPower);
            //rearRightDrive.setPower(rearRightPower);

            //This code reads from the encoder and translates the counts into numbers of
            //motor rotations.
            //NOTE:  getCurrentPosition measures cumulatively--you get the total travel distance
            // every time you read.  You'll have to use your math if you want to measure relative
            // distances...you could also translate the # of rotations into actual distance by
            // incorporating the rest of your knowledge of your system (gear ratios, lead screw
            // pitch, etc.)
            totalCounts = frontLeftDrive.getCurrentPosition();
            totalDistance = totalCounts / MOTOR_PPR;

            // Show the elapsed game time.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            //Show the encoder counts and total rotations
            telemetry.addData("Encoder Count", "(%7d)", totalCounts);
            telemetry.addData("Num Rotations", "(%.2f)", totalDistance);
            telemetry.update();
        }
    }
}

