//import org.team401.offseason2017.DriveStick
import com.ctre.phoenix.ILoopable
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
import java.nio.file.Paths

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

class LoopableTask(private val task: () -> Unit): ILoopable {
    override fun OnStart() {
        task()
    }

    override fun IsDone() = true
    override fun OnLoop() {}
    override fun OnStop() {}
}

enum class paths {
    CENTER_TO_AIRSHIP, RIGHT_TO_AIRSHIP, LEFT_TO_AIRSHIP
}

fun setup() {
    Subsystems.add(GearHolder, Drivetrain, Climber)
    Controllers.add(Gamepad)
    Sensors.add(Last30Sensor)
    LightBar
    GearSensor

    on (Events.DISABLED) {
        LightBar.rainbow()
    }

    on (Events.ENABLED) {
        LightBar.set(LightLink.Color.WHITE, LightLink.Action.SOLID, LightLink.Speed.SLOW)
    }
}

fun auto() {

    val choice = SendableChooser<Enum<paths>>()
    choice.addDefault("Center to airship", paths.CENTER_TO_AIRSHIP)
    choice.addObject("Right to airship", paths.RIGHT_TO_AIRSHIP)
    choice.addObject("Left to airship", paths.LEFT_TO_AIRSHIP)

    SmartDashboard.putData("Auto sequence", choice)

    when(choice.selected){
        paths.CENTER_TO_AIRSHIP -> {
            AutoSequences.firstMoveForward()
        }
        paths.RIGHT_TO_AIRSHIP -> {
            AutoSequences.firstMoveForward()
            AutoSequences.turn(60f)
            AutoSequences.secondMove(60f)
        }
        paths.LEFT_TO_AIRSHIP -> {
            AutoSequences.firstMoveForward()
            AutoSequences.turn(-60f)
            AutoSequences.secondMove(-60f)
        }
    }
    
    Drivetrain.machine(DRIVETRAIN_MACHINE).setState(DrivetrainStates.AUTO_SEQUENCE)
}