package org.team401.offseason2017.subsystems

import com.ctre.phoenix.Drive.SensoredTank
import com.ctre.phoenix.Drive.Styles
import com.ctre.phoenix.Mechanical.SensoredGearbox
import com.ctre.phoenix.MotorControl.CAN.TalonSRX
import com.ctre.phoenix.MotorControl.SmartMotorController
import edu.wpi.first.wpilibj.Solenoid
import org.team401.offseason2017.Constants
import org.team401.offseason2017.DriveStick
import org.team401.offseason2017.Wheel
import org.team401.snakeskin.dsl.buildSubsystem
import org.team401.snakeskin.event.Events
import org.team401.snakeskin.subsystem.Subsystem

/*
 * 2017-Offseason-Robot-Code - Created on 9/26/17
 * Author: Cameron Earle
 * 
 * This code is licensed under the GNU GPL v3
 * You can find more info in the LICENSE file at project root
 */

/**
 * @author Cameron Earle
 * @version 9/26/17
 */

object ShifterStates {
    const val HIGH = "high"
    const val LOW = "low"
    const val AUTO = "auto"
}
const val SHIFTER_MACHINE = "shifting"

object DrivetrainStates {

}
const val DRIVETRAIN_MACHINE = "drive"

val Drivetrain: Subsystem = buildSubsystem {
    val leftFront = TalonSRX(Constants.MotorControllers.DRIVE_LEFT_FRONT_CAN)
    val leftMidF = TalonSRX(Constants.MotorControllers.DRIVE_LEFT_MIDF_CAN)
    val leftMidR = TalonSRX(Constants.MotorControllers.DRIVE_LEFT_MIDR_CAN)
    val leftRear = TalonSRX(Constants.MotorControllers.DRIVE_LEFT_REAR_CAN)

    val rightFront = TalonSRX(Constants.MotorControllers.DRIVE_RIGHT_FRONT_CAN)
    val rightMidF = TalonSRX(Constants.MotorControllers.DRIVE_RIGHT_MIDF_CAN)
    val rightMidR = TalonSRX(Constants.MotorControllers.DRIVE_RIGHT_MIDR_CAN)
    val rightRear = TalonSRX(Constants.MotorControllers.DRIVE_RIGHT_REAR_CAN)

    val left = SensoredGearbox(4096f, leftFront, leftMidF, leftMidR, leftRear, SmartMotorController.FeedbackDevice.CtreMagEncoder_Relative)
    val right = SensoredGearbox(4096f, rightFront, rightMidF, rightMidR, rightRear, SmartMotorController.FeedbackDevice.CtreMagEncoder_Relative)

    val tank = SensoredTank(left, right, false, true, Constants.DrivetrainParameters.WHEEL_RADIUS, Constants.DrivetrainParameters.WHEEL_DIST)

    val shifter = Solenoid(Constants.Pneumatics.SHIFTER_SOLENOID)

    val shiftMachine = stateMachine(SHIFTER_MACHINE) {
        fun shiftHigh() = shifter.set(true)
        fun shiftLow() = shifter.set(false)

        state (ShifterStates.HIGH) {
            entry {
                shiftHigh()
            }
        }

        state (ShifterStates.LOW) {
            entry {
                shiftLow()
            }
        }

        state (ShifterStates.AUTO) {
            entry {
                shiftHigh()
            }
            action {

            }
        }

        default {
            entry {
                shifter.set(false)
            }
        }
    }

    val driveMachine = stateMachine(DRIVETRAIN_MACHINE) {

        var forward = DriveStick.readAxis { PITCH }
        var turn = Wheel.readAxis { WHEEL }

        //theoretical drivecode
        default {
            forward = DriveStick.readAxis { PITCH }
            turn = Wheel.readAxis { WHEEL }

            tank.set(Styles.Basic.PercentOutput,forward.toFloat(), turn.toFloat())
        }
    }

    on (Events.AUTO_ENABLED) {
        shiftMachine.setState(ShifterStates.LOW)
    }

    on (Events.TELEOP_ENABLED) {
        shiftMachine.setState(ShifterStates.HIGH)
    }


}