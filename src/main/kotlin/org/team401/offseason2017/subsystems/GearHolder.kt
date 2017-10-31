package org.team401.offseason2017.subsystems

import com.ctre.phoenix.MotorControl.CAN.TalonSRX
import com.ctre.phoenix.MotorControl.SmartMotorController
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.team401.offseason2017.Constants
import org.team401.offseason2017.LightBar
import org.team401.snakeskin.component.LightLink
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

object GearArmStates {
    const val DOWN = "down"
    const val DOWN_SCORE = "down_score"
    const val UP = "up"
    const val STOW = "stow"
}
const val GEAR_ARM_MACHINE = "gear_arm"

object GearIntakeStates {
    const val COLLECT = "full_intake"
    const val RETAIN = "reduced_intake"
    const val EJECT = "eject"
    const val OFF = "idle"
}
const val GEAR_INTAKE_MACHINE = "gear_intake"

val GearHolder: Subsystem = buildSubsystem {
    val arm = TalonSRX(Constants.MotorControllers.GEAR_ARM_CAN)
    val wheels = TalonSRX(Constants.MotorControllers.GEAR_INTAKE_CAN)

    setup {
        arm.setFeedbackDevice(SmartMotorController.FeedbackDevice.CtreMagEncoder_Absolute)
        arm.isSafetyEnabled = false
        arm.configMaxOutputVoltage(6.0)

        wheels.isSafetyEnabled = false

        arm.setPosition(arm.pulseWidthPosition % 4096)
    }

    val wheelsMachine = stateMachine(GEAR_INTAKE_MACHINE) {
        fun voltage() = wheels.changeControlMode(SmartMotorController.TalonControlMode.Voltage)
        fun openLoop() = wheels.changeControlMode(SmartMotorController.TalonControlMode.PercentVbus)

        state(GearIntakeStates.COLLECT) {
            entry {
                voltage()
                wheels.set(Constants.IntakeParameters.COLLECT_VOLTAGE)
            }
        }

        state(GearIntakeStates.EJECT) {
            entry {
                voltage()
                wheels.set(Constants.IntakeParameters.EJECT_VOLTAGE)
            }
        }

        state(GearIntakeStates.RETAIN) {
            entry {
                voltage()
                wheels.set(Constants.IntakeParameters.RETAIN_VOLTAGE)
            }
        }

        default {
            entry {
                openLoop()
                wheels.set(0.0)
            }
        }
    }

    val armMachine = stateMachine(GEAR_ARM_MACHINE) {
        fun closedLoop() {
            arm.configMaxOutputVoltage(Constants.ArmParameters.NORM_VOLTAGE)
            arm.changeControlMode(SmartMotorController.TalonControlMode.Position)
            arm.p = Constants.ArmParameters.P
        }
        fun closedLoopScore() {
            arm.configMaxOutputVoltage(Constants.ArmParameters.SCORE_VOLTAGE)
            arm.changeControlMode(SmartMotorController.TalonControlMode.Position)
            arm.p = Constants.ArmParameters.SCORE_P
        }
        fun openLoop() = arm.changeControlMode(SmartMotorController.TalonControlMode.PercentVbus)

        state(GearArmStates.DOWN) {
            entry {
                closedLoop()
                arm.setpoint = Constants.ArmParameters.DOWN_POS
                LightBar.signal(Constants.SignalColors.WANT_GEAR)
            }
        }

        state(GearArmStates.DOWN_SCORE) {
            entry {
                closedLoopScore()
                arm.setpoint = Constants.ArmParameters.DOWN_POS
                LightBar.signal(Constants.SignalColors.SCORED_GEAR)
            }
        }

        state(GearArmStates.UP) {
            entry {
                closedLoop()
                arm.setpoint = Constants.ArmParameters.UP_POS
            }
        }

        state(GearArmStates.STOW) {
            entry {
                closedLoop()
                arm.setpoint = Constants.ArmParameters.STOW_POS
            }
        }

        default {
            entry {
                openLoop()
                arm.set(0.0)
            }
            action {
                SmartDashboard.putNumber("pos", arm.position)
                SmartDashboard.putNumber("encPos", arm.encPosition.toDouble())
                SmartDashboard.putNumber("pwPos", arm.pulseWidthPosition.toDouble())
            }
        }
    }

    on (Events.ENABLED) {
        armMachine.setState(GearArmStates.STOW)
    }
}