import org.team401.offseason2017.DriveStick
import org.team401.offseason2017.DriveWheel
import org.team401.offseason2017.MashGamepad
import org.team401.offseason2017.subsystems.Climber
import org.team401.offseason2017.subsystems.Drivetrain
import org.team401.offseason2017.subsystems.GearHolder
import org.team401.snakeskin.registry.Controllers
import org.team401.snakeskin.registry.Sensors
import org.team401.snakeskin.registry.Subsystems

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

fun setup() {
    Subsystems.add(Climber, Drivetrain, GearHolder)
    Controllers.add(DriveWheel, DriveStick, MashGamepad)
    Sensors.add()
}