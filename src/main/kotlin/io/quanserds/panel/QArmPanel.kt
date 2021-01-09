package io.quanserds.panel

import io.quanserds.ControlPanel
import io.quanserds.DSManager
import io.quanserds.comm.api.CommAPI
import io.quanserds.comm.api.Container
import io.quanserds.comm.math.QArmMath
import io.quanserds.fx.*
import io.quanserds.icon.fontIcon
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ColorPicker
import javafx.scene.control.Slider
import javafx.scene.control.Tooltip
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.GridPane
import javafx.scene.paint.Color
import javafx.util.StringConverter
import org.kordamp.ikonli.materialdesign2.*

class QArmPanel : ControlPanel {

    // (S: 52deg, E: -52deg) for plastic

    override val name = "QArm"
    override val icon = MaterialDesignR.ROBOT_INDUSTRIAL

    override val mailFilter = listOf(CommAPI.ID_QARM)

    private lateinit var dsManager: DSManager

    private var b = 0.0
    private var s = 0.0
    private var e = 0.0
    private var w = 0.0
    private var g = 0.0

    private var cR = 1f
    private var cG = 0f
    private var cB = 0f

    override fun periodicRequestData(frame: Int) {
        val m = dsManager
        m.postMail(
            CommAPI.qarm_CommandAndRequestState(
                0,
                b.toFloat(),
                s.toFloat(),
                e.toFloat(),
                w.toFloat(),
                g.toFloat(),
                cR,
                cG,
                cB,
                1f
            )
        )
    }

    override fun accept(manager: DSManager) {
        dsManager = manager
    }

    override fun periodicResponseData(containers: List<Container>) {
    }


    private val epx = textField {
        width(60.0)
        text = "0.4064"
    }

    private val epy = textField {
        width(60.0)
        text = "0.0000"
    }

    private val epz = textField {
        width(60.0)
        text = "0.4826"
    }

    private val basePicker = ColorPicker().apply {
        width(60.0)
        this.value = Color.RED
        valueProperty().addListener { _, _, nv ->
            cR = nv.red.toFloat()
            cG = nv.green.toFloat()
            cB = nv.blue.toFloat()
        }
    }

    private fun makeSlider(min: Int, max: Int) = slider {
        this.value = 0.0
        this.min = min.toDouble()
        this.max = max.toDouble()
        this.blockIncrement = 5.0
        width(120.0)
        this.tooltip = tooltip
    }

    private val bSlider = makeSlider(-175, 175)
    private val sSlider = makeSlider(-90, 90)
    private val eSlider = makeSlider(-80, 80)
    private val wSlider = makeSlider(-170, 170)
    private val gSlider = makeSlider(-0, 55)

    override fun onKeyPressed(e: KeyEvent) {
        when (e.code) {
            KeyCode.R -> bSlider.value -= 1
            KeyCode.T -> bSlider.value += 1
            KeyCode.F -> sSlider.value -= 1
            KeyCode.G -> sSlider.value += 1
            KeyCode.V -> eSlider.value -= 1
            KeyCode.B -> eSlider.value += 1
            KeyCode.Y -> wSlider.value -= 1
            KeyCode.U -> wSlider.value += 1
            KeyCode.H -> gSlider.value -= 1
            KeyCode.J -> gSlider.value += 1
            else -> {
            }
        }
    }

    private val panel = vbox {
        styleClass("modular-panel")
        spacing = 8.0
        padding = Insets(8.0)
        add(gridPane {
            hgap = 8.0
            vgap = 4.0
            add(gridLabel("x"), 0, 0)
            add(gridLabel("y"), 1, 0)
            add(gridLabel("z"), 2, 0)
            add(gridLabel("Base"), 3, 0)
            add(epx, 0, 1)
            add(epy, 1, 1)
            add(epz, 2, 1)
            add(basePicker, 3, 1)
        })
        add(gridPane {
            hgap = 8.0
            vgap = 4.0
            makeSliderBar(0, "Base", bSlider, "R", "T") {
                b = it * Math.PI / 180.0
            }
            makeSliderBar(1, "Shoulder", sSlider, "F", "G") {
                s = it * Math.PI / 180.0
            }
            makeSliderBar(2, "Elbow", eSlider, "V", "B") {
                e = it * Math.PI / 180.0
            }
            makeSliderBar(3, "Wrist", wSlider, "Y", "U") {
                w = it * Math.PI / 180.0
            }
            makeSliderBar(4, "Gripper", gSlider, "H", "J") {
                g = it * Math.PI / 180.0
            }
        })

        add(textField {
            isEditable = false
            text = "No Gripped Object"
            width(256.0)
        })

        vspace()

        add(hbox {
            align(Pos.CENTER_RIGHT)
            padding = Insets(0.0, 4.0, 0.0, 4.0)
            spacing = 8.0
            add(Button("", fontIcon(MaterialDesignH.HOME, 20)).apply {
                setOnAction {
                    bSlider.value = 0.0
                    sSlider.value = 0.0
                    eSlider.value = 0.0
                    wSlider.value = 0.0
                }
                tooltip = Tooltip("Move to Home")
            })
            add(Button("", fontIcon(MaterialDesignI.INFORMATION_OUTLINE, 20)).apply {
                tooltip = Tooltip("QArm Info")
            })
        })
    }

    private fun GridPane.makeSliderBar(
        index: Int,
        kind: String,
        s: Slider,
        kL: String,
        kR: String,
        onChange: (Int) -> Unit
    ) {
        val tooltip = Tooltip("Control the $kind of the QArm between (-20°, 20°)")

        val t = textField {
            this.text = "0"
            width(36.0)
            this.tooltip = tooltip
        }

        t.textProperty().bindBidirectional(s.valueProperty(), object : StringConverter<Number>() {
            override fun toString(ob: Number?): String {
                return ob?.toInt().toString() + "°"
            }

            override fun fromString(string: String?): Number {
                val i = string?.trim('°')?.toIntOrNull() ?: 0
                return i.coerceIn(s.min.toInt(), s.max.toInt())
            }
        })

        add(label {
            this.text = kind
            this.tooltip = tooltip
        }, 0, index)
        add(gridLabel(kL).apply {
            styleClass("keyboard-control-label")
        }, 1, index)
        add(s, 2, index)
        add(gridLabel(kR).apply {
            styleClass("keyboard-control-label")
        }, 3, index)
        add(t, 4, index)

        s.valueProperty().addListener { _, _, newValue ->
            onChange(newValue.toInt())
            updateEndEffectors()
        }
    }

    private fun updateEndEffectors() {
        val res = QArmMath.forwardKinematics(b, s, e, w)
        epx.text = res[0].f
        epy.text = res[1].f
        epz.text = res[2].f
    }

    override fun getNode() = panel
}