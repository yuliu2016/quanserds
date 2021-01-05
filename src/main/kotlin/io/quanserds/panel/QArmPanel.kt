package io.quanserds.panel

import io.quanserds.ControlPanel
import io.quanserds.DSManager
import io.quanserds.comm.api.CommAPI
import io.quanserds.comm.api.Container
import io.quanserds.comm.math.QArmMath
import io.quanserds.command.instantCommand
import io.quanserds.fx.*
import io.quanserds.icon.fontIcon
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Tooltip
import javafx.scene.layout.GridPane
import javafx.util.StringConverter
import org.kordamp.ikonli.materialdesign2.*

class QArmPanel : ControlPanel {

    override val name = "QArm"
    override val icon = MaterialDesignR.ROBOT_INDUSTRIAL

    override val mailFilter = listOf(CommAPI.ID_QARM)

    private lateinit var dsManager: DSManager

    override fun periodicRequestData() {
        val m = dsManager
        if (commandOnNextPeriod) {
            m.postMail(
                CommAPI.qarm_CommandAndRequestState(
                    0,
                    b.toFloat(),
                    s.toFloat(),
                    e.toFloat(),
                    w.toFloat(),
                    g.toFloat(),
                    0f,
                    0f,
                    1f,
                    1f
                )
            )
            commandOnNextPeriod = false
        }
    }

    override fun accept(manager: DSManager) {
        dsManager = manager
    }

    override fun periodicResponseData(containers: List<Container>) {
    }

    private var b = 0.0
    private var s = 0.0
    private var e = 0.0
    private var w = 0.0
    private var g = 0.0
    private var commandOnNextPeriod = false

    private val epx = textField {
        width(80.0)
        text = "0.4064"
    }

    private val epy = textField {
        width(80.0)
        text = "0.0000"
    }

    private val epz = textField {
        width(80.0)
        text = "0.4826"
    }

    private val panel = vbox {
        styleClass("modular-panel")
        maxWidth = 280.0
        spacing = 8.0
        padding = Insets(8.0)
        add(gridPane {
            hgap = 8.0
            vgap = 4.0
            makeSlider(0, "Base", -175, 175) {
                b = it * Math.PI / 180.0
                commandOnNextPeriod = true
            }
            makeSlider(1, "Shoulder", -90, 90) {
                s = it * Math.PI / 180.0
                commandOnNextPeriod = true
            }
            makeSlider(2, "Elbow", -80, 80) {
                e = it * Math.PI / 180.0
                commandOnNextPeriod = true
            }
            makeSlider(3, "Wrist", -170, 170) {
                w = it * Math.PI / 180.0
                commandOnNextPeriod = true
            }
            makeSlider(4, "Gripper", -55, 55) {
                g = it * Math.PI / 180.0
                commandOnNextPeriod = true
            }
        })
        add(gridPane {
            hgap = 8.0
            vgap = 4.0
            add(gridLabel("x"), 0, 0)
            add(gridLabel("y"), 1, 0)
            add(gridLabel("z"), 2, 0)
            add(epx, 0, 1)
            add(epy, 1, 1)
            add(epz, 2, 1)
        })

        vspace()

        add(gridPane {
            hgap = 10.0
            vgap = 4.0
            add(fingerPad(), 0, 0)
            add(fingerPad(), 1, 0)
            add(fingerPad(), 2, 0)
            add(fingerPad(), 3, 0)
        })

        add(textField {
            isEditable = false
            text = "No Gripped Object"
            width(256.0)
        })


        add(hbox {
            align(Pos.CENTER_RIGHT)
            padding = Insets(0.0, 4.0, 0.0, 4.0)
            spacing = 8.0
            add(Button("", fontIcon(MaterialDesignH.HOME, 20)).apply {
                tooltip = Tooltip("Move to Home")
            })
            add(Button("", fontIcon(MaterialDesignP.PALETTE_OUTLINE, 20)).apply {
                tooltip = Tooltip("Change Base Colour")
            })
            add(Button("", fontIcon(MaterialDesignC.CALCULATOR, 20)).apply {
                tooltip = Tooltip("QArm Math Calculator")
            })
            add(Button("", fontIcon(MaterialDesignI.INFORMATION_OUTLINE, 20)).apply {
                tooltip = Tooltip("QArm Info")
            })
        })
    }

    private fun fingerPad(): Node {
        return hbox {
            height(4.0)
            width(56.0)
            style = "-fx-background-color: orange;"
        }
    }

    private fun GridPane.makeSlider(index: Int, text: String, min: Int, max: Int, onChange: (Int) -> Unit) {
        val tooltip = Tooltip("Control the $text of the QArm between (-20°, 20°)")

        val s = slider {
            this.value = 0.0
            this.min = min.toDouble()
            this.max = max.toDouble()
            this.blockIncrement = 5.0
            width(120.0)
            this.tooltip = tooltip
        }

        val t = textField {
            this.text = "0"
            width(60.0)
            this.tooltip = tooltip
        }

        t.textProperty().bindBidirectional(s.valueProperty(), object : StringConverter<Number>() {
            override fun toString(ob: Number?): String {
                return ob?.toInt().toString() + "°"
            }

            override fun fromString(string: String?): Number {
                val i = string?.trim('°')?.toIntOrNull() ?: 0
                return i.coerceIn(min, max)
            }
        })

        add(label {
            this.text = text
            this.tooltip = tooltip
        }, 0, index)
        add(s, 1, index)
        add(t, 2, index)

        s.valueProperty().addListener { _, _, newValue ->
            dsManager.submit(instantCommand("A", "B", "C$newValue") {
                onChange(newValue.toInt())
                updateEndEffectors()
            })
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