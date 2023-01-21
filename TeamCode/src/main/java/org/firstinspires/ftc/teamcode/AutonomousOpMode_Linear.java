package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="Robot: Generic Automation", group="Robot")
@Disabled
public class AutonomousOpMode_Linear extends StandardSetupOpMode {
    private boolean isLeft;
    private double direction;

    public AutonomousOpMode_Linear( boolean isLeft ){
        this.isLeft = isLeft;
        if(isLeft)
            direction = -1.0;
        else
            direction = 1.0;
    }

    @Override
    public void runOpMode() throws InterruptedException {
        // Call standard setup
        super.runOpMode();

        // Determine parking spot
        Motion.PARKING_SPOT parkingSpot = position;

        // Autonomous time!!!!!
        // This code is written from the right side of the field
        // We use direction to mirror the left side of the field
        claw.closeClaw();
        sleep(500);
        elevator.setPosition(-1, Elevator.ELEVATOR_HEIGHT.LOW);
        motion.translate (Motion.Direction.FORWARD, 2.7, 0.5);
        elevator.setPosition(-1, Elevator.ELEVATOR_HEIGHT.HIGH);
        motion.translate(Motion.Direction.BACKWARD, 0.50, 0.5);
        motion.rotation (Motion.Direction.LEFT,direction * 45,0.5);
        motion.translate (Motion.Direction.FORWARD, 0.65,0.5);
        elevator.drop();
        motion.translate (Motion.Direction.BACKWARD, 0.6,0.5);
        elevator.setPosition(-1, Elevator.ELEVATOR_HEIGHT.CONE_5);
        motion.rotation(Motion.Direction.RIGHT, direction * 135,0.5);
        claw.openClaw();
        motion.translate (Motion.Direction.FORWARD,1,0.5);
        claw.closeClaw();
        sleep(300);
        elevator.setPosition(-1, Elevator.ELEVATOR_HEIGHT.LOW);
        sleep(400);
        motion.translate(Motion.Direction.BACKWARD, 1,0.5);
        motion.rotation(Motion.Direction.RIGHT, direction * 45,0.5);
        motion.translate (Motion.Direction.FORWARD, 0.7,0.4);
        motion.translate (Motion.Direction.BACKWARD, 0.2,0.4);
        elevator.drop();
        motion.translate (Motion.Direction.BACKWARD, 0.30,0.4);
        motion.rotation(Motion.Direction.LEFT, direction * 45,0.5);
        elevator.setPosition(-1, Elevator.ELEVATOR_HEIGHT.GROUND);
        switch(parkingSpot){
            case PARK_ONE:
                if(isLeft)
                    motion.translate(Motion.Direction.FORWARD, 1.0, 0.75);
                else
                    motion.translate(Motion.Direction.BACKWARD, 1.1, 0.75);
                break;
            case PARK_TWO:
                break;
            case PARK_THREE:
                if(isLeft)
                    motion.translate(Motion.Direction.BACKWARD, 1.1, 0.75);
                else
                    motion.translate(Motion.Direction.FORWARD, 1.0, 0.75);
                break;
        }
    }
}