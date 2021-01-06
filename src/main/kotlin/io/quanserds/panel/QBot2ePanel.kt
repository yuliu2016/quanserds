package io.quanserds.panel

import io.quanserds.ControlPanel
import io.quanserds.DSManager
import io.quanserds.comm.api.CommAPI.*
import io.quanserds.comm.api.Container
import io.quanserds.comm.struct.QBot2eState
import io.quanserds.fx.*
import io.quanserds.icon.fontIcon
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.paint.Color
import javafx.scene.shape.ArcType
import javafx.scene.shape.StrokeLineCap
import org.kordamp.ikonli.Ikon
import org.kordamp.ikonli.materialdesign2.*

class QBot2ePanel : ControlPanel {

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

    override fun periodicRequestData() {
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
            fTurn *= 8
        }
        ds.postMail(qbot2e_CommandAndRequestState(0, fSpeed, fTurn))
    }

    override fun onKeyPressed(e: KeyEvent) {
        if (e.isControlDown) return
        when (e.code) {
            KeyCode.W -> keyW = true
            KeyCode.S -> keyS = true
            KeyCode.A -> keyA = true
            KeyCode.D -> keyD = true
            KeyCode.SHIFT -> keyShift = true
            else -> {
            }
        }
    }

    override fun onKeyReleased(e: KeyEvent) {
        when (e.code) {
            KeyCode.W -> keyW = false
            KeyCode.S -> keyS = false
            KeyCode.A -> keyA = false
            KeyCode.D -> keyD = false
            KeyCode.SHIFT -> keyShift = false
            else -> {
            }
        }
    }

    override fun periodicResponseData(containers: List<Container>) {
        containers.forEach { it.parse() }
    }

    private fun Container.parse() {
        when (deviceFunction) {
            FCN_QBOT_RESPONSE_STATE -> {
                val state = qbot2e_ResponseState(this)
                updateQBotState(state)
                updateRobotDrawing(state)
            }
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

        el.text = s.encoder_left.toString()
        er.text = s.encoder_right.toString()

        he.text = s.heading.f
        gy.text = s.gyro.f
    }

    private fun makeLetterLabel(t: String, ic: Ikon) = Label(t, fontIcon(ic, 20)).apply {
        styleClass("keyboard-control-label")
        width(38.0)
    }

    private val mainPanel = vbox {
        spacing = 8.0

        add(gridPane {
            hgap = 8.0
            vgap = 4.0

            val group = ToggleGroup()

            add(RadioButton("Fast").apply { toggleGroup = group; isSelected = true }, 0, 0)
            add(RadioButton("Slow").apply { toggleGroup = group }, 0, 1)

            add(makeLetterLabel("W", MaterialDesignA.ARROW_UP), 1, 0)
            add(makeLetterLabel("S", MaterialDesignA.ARROW_DOWN), 2, 0)
            add(label("Speed"), 3, 0)
            add(textField {
                text = "0"
                width(60.0)
            }, 4, 0)

            add(makeLetterLabel("A", MaterialDesignR.ROTATE_LEFT), 1, 1)
            add(makeLetterLabel("D", MaterialDesignR.ROTATE_RIGHT), 2, 1)
            add(label("Turn"), 3, 1)
            add(textField {
                text = "0"
                width(60.0)
            }, 4, 1)
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
        add(vertBox("Colour", hbox {
            height(4.0)
            style = "-fx-background-color: #ffdc0c"
        }, co))
        add(vertBox("Depth", de))
        add(vertBox("Heading", he))
        add(vertBox("Gyro", gy))
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
            })
            hspace()
            add(Button("", fontIcon(MaterialDesignS.STOP, 20)).apply {
                tooltip = Tooltip("Move to Home")
            })
            add(Button("", fontIcon(MaterialDesignI.INFORMATION_OUTLINE, 20)).apply {
                tooltip = Tooltip("QBot2e Info")
            })
        })
    }

    override fun getNode() = megaPanel
}