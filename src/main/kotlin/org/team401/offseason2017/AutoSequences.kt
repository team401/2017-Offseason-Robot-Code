package org.team401.offseason2017

import com.ctre.phoenix.Drive.SensoredTank
import com.ctre.phoenix.ILoopable
import com.ctre.phoenix.Motion.ServoZeroTurn
import com.ctre.phoenix.Motion.ServoZeroTurnWithImu
import com.ctre.phoenix.Sensors.PigeonImu

object AutoSequences {
    private lateinit var tankDrive: SensoredTank
    private lateinit var imu: PigeonImu

    fun setDrivetrain(drivetrain: SensoredTank) {
        tankDrive = drivetrain
    }

    fun setImu(imu: PigeonImu) {
        this.imu = imu
    }

    val ActiveAutoSequence = arrayListOf<ILoopable>()
}

