package org.team401.offseason2017.subsystems

import com.ctre.MotorControl.CANTalon
import com.ctre.MotorControl.SmartMotorController
import edu.wpi.first.wpilibj.Solenoid
import org.team401.offseason2017.Constants
import org.team401.snakeskin.component.MotorGroup
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
    val leftFront = CANTalon(Constants.MotorControllers.DRIVE_LEFT_FRONT_CAN)
    val leftMidF = CANTalon(Constants.MotorControllers.DRIVE_LEFT_MIDF_CAN)
    val leftMidR = CANTalon(Constants.MotorControllers.DRIVE_LEFT_MIDR_CAN)
    val leftRear = CANTalon(Constants.MotorControllers.DRIVE_LEFT_REAR_CAN)

    val rightFront = CANTalon(Constants.MotorControllers.DRIVE_RIGHT_FRONT_CAN)
    val rightMidF = CANTalon(Constants.MotorControllers.DRIVE_RIGHT_MIDF_CAN)
    val rightMidR = CANTalon(Constants.MotorControllers.DRIVE_RIGHT_MIDR_CAN)
    val rightRear = CANTalon(Constants.MotorControllers.DRIVE_RIGHT_REAR_CAN)

    val left = MotorGroup(leftFront, leftMidF, leftMidR, leftRear)
    val right = MotorGroup(rightFront, rightMidF, rightMidR, rightRear)

    val shifter = Solenoid(Constants.Pneumatics.SHIFTER_SOLENOID)

    fun fuse(master: Int, vararg slaves: CANTalon) {
        slaves.forEach {
            it.changeControlMode(SmartMotorController.TalonControlMode.Follower)
            it.set(master.toDouble())
        }
    }

    setup {
        right.inverted = true
        fuse(Constants.MotorControllers.DRIVE_LEFT_FRONT_CAN, leftMidF, leftMidR, leftRear)
        fuse(Constants.MotorControllers.DRIVE_RIGHT_FRONT_CAN, rightMidF, rightMidR, rightRear)
    }

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
        
    }

    on (Events.AUTO_ENABLED) {
        shiftMachine.setState(ShifterStates.LOW)
    }

    on (Events.TELEOP_ENABLED) {
        shiftMachine.setState(ShifterStates.AUTO)
    }


}