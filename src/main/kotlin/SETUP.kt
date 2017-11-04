//import org.team401.offseason2017.DriveStick
import com.ctre.phoenix.ILoopable
import com.ctre.phoenix.Motion.ServoGoStraightWithImu
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.snakeskin.component.LightLink
import org.snakeskin.dsl.machine
//import org.team401.offseason2017.MashStick
//import org.team401.offseason2017.Wheel
import org.snakeskin.dsl.on
import org.snakeskin.event.Events
import org.snakeskin.registry.Controllers
import org.snakeskin.registry.Sensors
import org.snakeskin.registry.Subsystems
import org.team401.offseason2017.*
import org.team401.offseason2017.subsystems.*

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

enum class AutoOptions {
    CENTER_GEAR,
    RIGHT_GEAR,
    LEFT_GEAR,
    NO_AUTO
}

val AUTO_CHOOSER = SendableChooser<AutoOptions>()

fun setup() {
    Subsystems.add(GearHolder, Drivetrain, Climber)
    Controllers.add(Gamepad)
    Sensors.add(Last30Sensor)
    LightBar

    on (Events.DISABLED) {
        LightBar.rainbow()
    }

    on (Events.ENABLED) {
        LightBar.off()
    }

    AUTO_CHOOSER.addDefault("Center Gear", AutoOptions.CENTER_GEAR)
    AUTO_CHOOSER.addObject("Left Gear", AutoOptions.LEFT_GEAR)
    AUTO_CHOOSER.addObject("Right Gear", AutoOptions.RIGHT_GEAR)
    AUTO_CHOOSER.addObject("No Auto", AutoOptions.NO_AUTO)

    SmartDashboard.putData("Auto Mode", AUTO_CHOOSER)
}

fun auto() {
    AutoSequences.zero()
    Sequences.prepareScore()
    val selectedAuto = AUTO_CHOOSER.selected

    when (selectedAuto) {
        AutoOptions.CENTER_GEAR -> AutoSequences.scoreCenterGear()
        AutoOptions.LEFT_GEAR -> AutoSequences.scoreLeftGear()
        AutoOptions.RIGHT_GEAR -> AutoSequences.scoreRightGear()
        else -> {}
    }

    Drivetrain.machine(DRIVETRAIN_MACHINE).setState(DrivetrainStates.AUTO_SEQUENCE)
}