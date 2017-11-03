package org.team401.offseason2017.subsystems

import com.ctre.phoenix.MotorControl.CAN.TalonSRX
import com.ctre.phoenix.MotorControl.SmartMotorController
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.team401.offseason2017.Constants
import org.team401.offseason2017.LightBar
import org.snakeskin.component.LightLink
import org.snakeskin.dsl.buildSubsystem
import org.snakeskin.event.Events
import org.snakeskin.subsystem.Subsystem

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

var GEAR_PRESENT = true

val GearHolder: Subsystem = buildSubsystem {
    val arm = TalonSRX(Constants.MotorControllers.GEAR_ARM_CAN)
    val wheels = TalonSRX(Constants.MotorControllers.GEAR_INTAKE_CAN)

    setup {
        arm.setFeedbackDevice(SmartMotorController.FeedbackDevice.CtreMagEncoder_Absolute)
        arm.isSafetyEnabled = false
        arm.configMaxOutputVoltage(6.0)

        wheels.isSafetyEnabled = false
        wheels.inverted = true

        arm.setPosition(arm.pulseWidthPosition % 4096)
    }

    val wheelsMachine = stateMachine(GEAR_INTAKE_MACHINE) {
        fun voltage() = wheels.changeControlMode(SmartMotorController.TalonControlMode.Voltage)
        fun openLoop() = wheels.changeControlMode(SmartMotorController.TalonControlMode.PercentVbus)

        state(GearIntakeStates.COLLECT) {
            var counter = 0
            entry {
                GEAR_PRESENT = false
                voltage()
                wheels.set(Constants.IntakeParameters.COLLECT_VOLTAGE)
            }
            action {
                if (wheels.outputCurrent > Constants.IntakeParameters.HAVE_GEAR_CURRENT) {
                    counter++
                } else {
                    counter = 0
                }

                if (counter >= Constants.IntakeParameters.GEAR_COUNTER_MAX) {
                    GEAR_PRESENT = true
                    if (counter == Constants.IntakeParameters.GEAR_COUNTER_MAX) {
                        LightBar.signal(LightLink.Color.GREEN)
                        setState(GearIntakeStates.RETAIN)
                    }
                } else {
                    GEAR_PRESENT = false
                }
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
            arm.i = Constants.ArmParameters.I
            arm.d = Constants.ArmParameters.D
        }
        fun closedLoopScore() {
            arm.configMaxOutputVoltage(Constants.ArmParameters.SCORE_VOLTAGE)
            arm.changeControlMode(SmartMotorController.TalonControlMode.Position)
            arm.p = Constants.ArmParameters.SCORE_P
            arm.i = Constants.ArmParameters.SCORE_I
            arm.d = Constants.ArmParameters.SCORE_D
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
        }
    }

    on (Events.ENABLED) {
        armMachine.setState(GearArmStates.STOW)
    }
}