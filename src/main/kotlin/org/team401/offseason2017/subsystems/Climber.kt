package org.team401.offseason2017.subsystems

import edu.wpi.first.wpilibj.PowerDistributionPanel
import edu.wpi.first.wpilibj.VictorSP
import org.team401.offseason2017.Constants
import org.team401.offseason2017.LightBar
import org.snakeskin.component.LightLink
import org.snakeskin.component.MotorGroup
import org.snakeskin.dsl.buildSubsystem
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

object ClimberStates {
    const val OFF = "off"
    const val MANUAL_CLIMB = "climb"
    const val CLIMB = "auto_climb"
    const val MAINTAIN = "hold"
}

const val CLIMBER_MACHINE = "climber"

val Climber: Subsystem = buildSubsystem {
    val left = VictorSP(Constants.MotorControllers.CLIMBER_LEFT_PWM)
    val right = VictorSP(Constants.MotorControllers.CLIMBER_RIGHT_PWM)

    val pdp = PowerDistributionPanel()

    val motors = MotorGroup(left, right)

    setup {
        right.inverted = true
    }

    val climberMachine = stateMachine(CLIMBER_MACHINE) {
        state(ClimberStates.OFF) {
            action {
                motors.set(0.0)
            }
        }

        state("left") {
            action {
                left.set(0.5)
            }
        }

        state("right") {
            action {
                right.set(0.5)
            }
        }

        state(ClimberStates.MANUAL_CLIMB) {
            action {
                motors.set(1.0)
            }
        }

        state(ClimberStates.MAINTAIN) {
            action {
                motors.set(Constants.ClimberParameters.MAINTAIN_VOLTAGE / pdp.voltage)
            }
        }

        state(ClimberStates.CLIMB) {
            var leftCurrent = 0.0
            var rightCurrent = 0.0

            var counter = 0

            action {
                leftCurrent = pdp.getCurrent(Constants.MotorControllers.CLIMBER_LEFT_PDP_CHANNEL)
                rightCurrent = pdp.getCurrent(Constants.MotorControllers.CLIMBER_RIGHT_PDP_CHANNEL)
                println(leftCurrent)

                motors.set(1.0)


                if (leftCurrent > Constants.ClimberParameters.CLIMBER_LEFT_CLIMB_CURRENT
                        || rightCurrent > Constants.ClimberParameters.CLIMBER_RIGHT_CLIMB_CURRENT) {
                    counter++
                } else {
                    counter = 0
                }

                if (counter >= Constants.ClimberParameters.CLIMB_COUNTER_MAX) {
                    setState(ClimberStates.MAINTAIN)
                    LightBar.signal(LightLink.Color.GREEN)
                }
            }
        }

        default {
            entry {
                motors.set(0.0)
            }
        }
    }
}