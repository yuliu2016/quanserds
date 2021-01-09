package io.quanserds.panel

import io.quanserds.ControlPanel
import io.quanserds.DSManager
import io.quanserds.comm.api.CommAPI.*
import io.quanserds.comm.api.Container
import io.quanserds.comm.struct.QBot2eState
import io.quanserds.command.Command
import io.quanserds.fx.*
import io.quanserds.icon.fontIcon
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.paint.Color
import javafx.scene.shape.ArcType
import javafx.scene.shape.StrokeLineCap
import org.kordamp.ikonli.Ikon
import org.kordamp.ikonli.materialdesign2.*
import kotlin.math.cos
import kotlin.math.sin

class QBot2ePanel : ControlPanel {

    companion object {
        private const val QBOT_RADIUS = 0.235 / 2.0
    }

    override val name = "QBot2e"
    override val icon = MaterialDesignR.ROBOT_VACUUM

    override val mailFilter = listOf(ID_QBOT, ID_QBOT_BOX)

    private lateinit var dsManager: DSManager

    private var keyW = false
    private var keyS = false
    private var keyA = false
    private var keyD = false
    private var keyShift = false

    override fun accept(manager: DSManager) {
        dsManager = manager
    }

    override fun periodicRequestData(frame: Int) {
        val ds = dsManager
        var fSpeed = 0f
        var fTurn = 0f
        if (keyW || keyS || keyA || keyD) {
            if (keyW) fSpeed += 0.5f
            if (keyS) fSpeed -= 0.5f
            if (keyA) fTurn += 1f
            if (keyD) fTurn -= 1f
            // easier version of arcade drive
            if (fSpeed < 0) fTurn *= -1
        }
        if (keyShift) {
            fSpeed *= 4
            fTurn *= 4
        }
        if (!fastRadio.isSelected) {
            fSpeed /= 4
            fTurn /= 4
        }
        ds.postMail(qbot2e_CommandAndRequestState(0, fSpeed, fTurn))
        vl.text = (fSpeed - fTurn * QBOT_RADIUS).f
        vr.text = (fSpeed + fTurn * QBOT_RADIUS).f
        if (frame % 2 == 0) {
            ds.postMail(qbot2e_RequestRGB(0))
        } else {
            ds.postMail(qbot2e_RequestDepth(0))
        }
    }

    override fun onKeyPressed(e: KeyEvent) {
        when (e.code) {
            KeyCode.W -> {
                keyW = true
                wLabel.kActiveStyle()
            }
            KeyCode.S -> {
                keyS = true
                sLabel.kActiveStyle()
            }
            KeyCode.A -> {
                keyA = true
                aLabel.kActiveStyle()
            }
            KeyCode.D -> {
                keyD = true
                dLabel.kActiveStyle()
            }
            KeyCode.SHIFT -> keyShift = true
            else -> {
            }
        }
    }

    override fun onKeyReleased(e: KeyEvent) = when (e.code) {
        KeyCode.W -> {
            if (keyW) wLabel.kControlStyle()
            keyW = false
        }
        KeyCode.S -> {
            if (keyS) sLabel.kControlStyle()
            keyS = false
        }
        KeyCode.A -> {
            if (keyA) aLabel.kControlStyle()
            keyA = false
        }
        KeyCode.D -> {
            if (keyD) dLabel.kControlStyle()
            keyD = false
        }
        KeyCode.SHIFT -> keyShift = false
        else -> {
        }
    }

    override fun periodicResponseData(containers: List<Container>) {
        containers.forEach { it.parse() }
    }

    private fun Container.parse() = when (deviceFunction) {
        FCN_QBOT_RESPONSE_STATE -> {
            val state = qbot2e_ResponseState(this)
            updateQBotState(state)
            updateRobotDrawing(state)
        }
        FCN_QARM_RESPONSE_RGB -> {
            val img = Image(qbot2e_ResponseRGB(this).inputStream())
            val color = img.pixelReader.getColor(240, 320)
            co.text = "%02x%02x%02x".format(
                (color.red * 255).toInt(),
                (color.green * 255).toInt(),
                (color.blue * 255).toInt()
            )
            rgbImageView.image = img
            colorIndicator.background = Background(BackgroundFill(color, null, null))
        }
        FCN_QARM_RESPONSE_DEPTH -> {
            val img = Image(qbot2e_ResponseDepth(this).inputStream())
            de.text = img.pixelReader.getColor(240, 320).green.f
        }
        else -> {
        }
    }

    private val cv = canvas {
        this.height = 60.0
        this.width = 60.0
        graphicsContext2D.drawRobot(45.0, bL = false, bF = false, bR = false)
    }

    private fun updateRobotDrawing(s: QBot2eState) {
        cv.graphicsContext2D.drawRobot(
            s.heading.toDouble() * -1 * 180 / Math.PI,
            s.bumper_left != 0, s.bumper_front != 0, s.bumper_right != 0
        )
    }

    private fun GraphicsContext.drawRobot(
        heading: Double,
        bL: Boolean,
        bF: Boolean,
        bR: Boolean
    ) {
        clearRect(0.0, 0.0, 60.0, 60.0)

        val startAngle = heading - 90.0
        this.stroke = Color.ORANGE
        lineWidth = 2.0
        this.strokeOval(10.0, 10.0, 40.0, 40.0)
        this.strokeOval(4.0, 4.0, 52.0, 52.0)

        lineWidth = 6.0
        lineCap = StrokeLineCap.BUTT

        stroke = if (bR) Color.RED else Color.LIME
        this.strokeArc(
            4.0, 4.0, 52.0, 52.0,
            startAngle % 360, 55.0, ArcType.OPEN
        )

        stroke = if (bF) Color.RED else Color.LIME
        this.strokeArc(
            4.0, 4.0, 52.0, 52.0,
            (startAngle + 65.0) % 360, 50.0, ArcType.OPEN
        )

        stroke = if (bL) Color.RED else Color.LIME
        this.strokeArc(
            4.0, 4.0, 52.0, 52.0,
            (startAngle + 125.0) % 360, 55.0, ArcType.OPEN
        )
    }

    private val colorIndicator = hbox {
        height(4.0)
        background = Background(BackgroundFill(Color.BLACK, null, null))
    }

    private val wx = tf()
    private val wy = tf()
    private val wz = tf()
    private val fx = tf()
    private val fy = tf()
    private val fz = tf()
    private val ux = tf()
    private val uy = tf()
    private val uz = tf()

    private val vl = tf()
    private val vr = tf()
    private val el = tf()
    private val er = tf()

    private val co = tf()
    private val de = tf()
    private val he = tf()
    private val gy = tf()

    private fun updateQBotState(s: QBot2eState) {
        wx.text = s.world_x.f
        wy.text = s.world_y.f
        wz.text = s.world_z.f
        fx.text = s.forward_x.f
        fy.text = s.forward_y.f
        fz.text = s.forward_z.f
        ux.text = s.up_x.f
        uy.text = s.up_y.f
        uz.text = s.up_z.f

        el.text = (s.encoder_left / 4096.0).f
        er.text = (s.encoder_right / 4096.0).f

        he.text = s.heading.f
        gy.text = s.gyro.f
    }

    private fun makeLetterLabel(t: String, ic: Ikon) = Label(t, fontIcon(ic, 20)).apply {
        styleClass.add("keyboard-control-label")
        padding = Insets(1.0, 6.0, 1.0, 2.0)
    }

    private val fastRadio = RadioButton("Fast")
    private val slowRadio = RadioButton("Slow")

    init {
        val group = ToggleGroup()
        fastRadio.toggleGroup = group
        slowRadio.toggleGroup = group
        fastRadio.isSelected = true
    }

    private val wLabel = makeLetterLabel("W", MaterialDesignA.ARROW_UP)
    private val sLabel = makeLetterLabel("S", MaterialDesignA.ARROW_DOWN)
    private val aLabel = makeLetterLabel("A", MaterialDesignR.ROTATE_LEFT)
    private val dLabel = makeLetterLabel("D", MaterialDesignR.ROTATE_RIGHT)

    private val rgbImageView = imageView {
        this.isPreserveRatio = true
        this.fitWidth = 60.0
    }

    private val mainPanel = vbox {
        spacing = 8.0

        add(gridPane {
            hgap = 8.0
            vgap = 4.0

            add(fastRadio, 0, 0)
            add(slowRadio, 0, 1)

            add(wLabel, 1, 0)
            add(sLabel, 2, 0)

            add(aLabel, 1, 1)
            add(dLabel, 2, 1)

            add(rgbImageView, 3, 0, 1, 2)
        })

        add(gridPane {
            hgap = 8.0
            vgap = 4.0
            add(label("Left").gridCenter(), 1, 0)
            add(label("Right").gridCenter(), 2, 0)
            add(label("Velocity"), 0, 1)
            add(vl, 1, 1)
            add(vr, 2, 1)
            add(label("Encoder"), 0, 2)
            add(el, 1, 2)
            add(er, 2, 2)
            add(cv, 3, 0, 1, 3)
        })

        add(gridPane {
            hgap = 8.0
            vgap = 4.0
            add(label("x").gridCenter(), 1, 0)
            add(label("y").gridCenter(), 2, 0)
            add(label("z").gridCenter(), 3, 0)
            add(label("World"), 0, 1)
            add(wx, 1, 1)
            add(wy, 2, 1)
            add(wz, 3, 1)
            add(label("Forward"), 0, 2)
            add(fx, 1, 2)
            add(fy, 2, 2)
            add(fz, 3, 2)
            add(label("Up"), 0, 3)
            add(ux, 1, 3)
            add(uy, 2, 3)
            add(uz, 3, 3)
        })

        vspace()
    }

    private val panel2 = vbox {
        spacing = 4.0
        align(Pos.TOP_CENTER)
        add(vertBox("Colour", colorIndicator, co))
        add(vertBox("Depth", de))
        add(vertBox("Heading", he))
        add(vertBox("Gyro", gy))
    }

    @Suppress("SameParameterValue")
    private fun setBoxAttribute(
        x: Double, y: Double, z: Double, pitch: Double, roll: Double, yaw: Double
    ) {
        dsManager.postMail(
            qbot2eBox_Command(
                0,
                x.toFloat(), y.toFloat(), z.toFloat(),
                pitch.toFloat(), roll.toFloat(), yaw.toFloat()
            )
        )
    }

    private fun setBoxAngle(theta: Double) {
        setBoxAttribute(
            0.0, 0.15 * (1 - cos(theta)), 0.15 * sin(theta),
            theta, 0.0, 0.0
        )
    }

    private var dumpCommandRunning = false

    private fun dump() {
        if (!dumpCommandRunning) {
            dumpCommandRunning = true
            dsManager.submit(DumpCommand())
        }
    }

    private inner class DumpCommand : Command {
        override fun start() {
        }

        private var dumpCount = 0

        override fun isFinished(): Boolean {
            return dumpCount >= 100
        }

        override fun execute() {
            val j = (dumpCount / 100.0) * (2 * Math.PI)
            val theta = 1 - cos(j)

            setBoxAngle(theta)
            dumpCount += 1
        }

        override fun stop() {
            setBoxAngle(0.0)
            dumpCommandRunning = false
        }
    }

    private val megaPanel = vbox {

        styleClass("modular-panel")
        spacing = 8.0
        padding = Insets(8.0)

        add(hbox {
            spacing = 8.0
            add(mainPanel)
            add(panel2)
            vgrow()
        })

        add(hbox {
            align(Pos.CENTER_RIGHT)
            padding = Insets(0.0, 4.0, 0.0, 4.0)
            spacing = 8.0
            add(Button("", fontIcon(MaterialDesignS.SLOPE_DOWNHILL, 20)).apply {
                tooltip = Tooltip("Dump the Box")
                setOnAction { dump() }
            })
            hspace()
            add(Button("", fontIcon(MaterialDesignI.INFORMATION_OUTLINE, 20)).apply {
                tooltip = Tooltip("QBot2e Info")
            })
        })
    }

    override fun getNode() = megaPanel
}