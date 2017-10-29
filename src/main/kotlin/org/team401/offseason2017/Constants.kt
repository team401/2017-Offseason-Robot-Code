package org.team401.offseason2017

object Constants {
    object Events {
        const val ZERO_SENSORS = "zero_sensors"
    }
    object MotorControllers {
        const val CLIMBER_LEFT_PWM = 0
        const val DRIVE_LEFT_REAR_CAN = 1
        const val DRIVE_LEFT_MIDR_CAN = 2
        const val DRIVE_LEFT_MIDF_CAN = 3
        const val DRIVE_LEFT_FRONT_CAN = 4
        const val GEAR_INTAKE_CAN = 5
        const val GEAR_ARM_CAN = 6
        const val DRIVE_RIGHT_FRONT_CAN = 7
        const val DRIVE_RIGHT_MIDF_CAN = 8
        const val DRIVE_RIGHT_MIDR_CAN = 9
        const val DRIVE_RIGHT_REAR_CAN = 10
        const val CLIMBER_RIGHT_PWM = 1

        const val CLIMBER_LEFT_PDP_CHANNEL = 0
        const val CLIMBER_RIGHT_PDP_CHANNEL = 0

        const val GEAR_ARM = 1
        const val INTAKE = 2
    }

    object Pneumatics {
        const val SHIFTER_SOLENOID = 0
    }

    object ArmParameters {
        const val HOME_POS = 1000.0

        const val DOWN_POS = HOME_POS + 190.0
        const val UP_POS = HOME_POS + 267.0
        const val STOW_POS = HOME_POS + 0.0

        const val P = 1.5
        const val SCORE_P = 1.0

        const val NORM_VOLTAGE = 6.0
        const val SCORE_VOLTAGE = 2.0
    }

    object IntakeParameters {
        const val COLLECT_VOLTAGE = 12.0
        const val RETAIN_VOLTAGE = 2.0
        const val EJECT_VOLTAGE = -6.0
    }

    object ClimberParameters {
        const val CLIMB_COUNTER_MAX = 25

        const val CLIMBER_LEFT_CLIMB_CURRENT = 0.0
        const val CLIMBER_RIGHT_CLIMB_CURRENT = 0.0
    }

    object DrivetrainParameters {
        const val WHEEL_RADIUS = 2f
        const val WHEEL_DIST = 5.5f
    }
}