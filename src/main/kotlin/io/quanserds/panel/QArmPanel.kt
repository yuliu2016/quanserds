package io.quanserds.panel

import io.quanserds.ControlPanel
import io.quanserds.DSManager
import io.quanserds.comm.api.CommAPI
import io.quanserds.comm.api.Container
import io.quanserds.fx.*
import io.quanserds.icon.fontIcon
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Tooltip
import javafx.scene.layout.GridPane
import org.kordamp.ikonli.materialdesign2.*

class QArmPanel : ControlPanel {

    override val name = "QArm"
    override val icon = MaterialDesignR.ROBOT_INDUSTRIAL

    override val mailFilter = listOf(CommAPI.ID_QARM)

    override fun periodicRequestData() {
    }

    private lateinit var dsManager: DSManager

    override fun accept(manager: DSManager) {
        dsManager = manager
    }

    override fun periodicResponseData(containers: List<Container>) {
    }

    private val panel = vbox {
        styleClass("modular-panel")
        maxWidth = 280.0
        spacing = 8.0
        padding = Insets(8.0)
        add(gridPane {
            hgap = 8.0
            vgap = 4.0
            makeSlider(0, "Base")
            makeSlider(1, "Shoulder")
            makeSlider(2, "Elbow")
            makeSlider(3, "Wrist")
            makeSlider(4, "Gripper")
        })
        add(gridPane {
            hgap = 8.0
            vgap = 4.0
            add(gridLabel("x"), 0, 0)
            add(gridLabel("y"), 1, 0)
            add(gridLabel("z"), 2, 0)
            add(textField {
                width(80.0)
                text = "0.4064"
            }, 0, 1)
            add(textField {
                width(80.0)
                text = "0.0000"
            }, 1, 1)
            add(textField {
                width(80.0)
                text = "0.4826"
            }, 2, 1)
        })


        vspace()

        add(gridPane {
            hgap = 10.0
            vgap = 4.0
            add(fingerPad(0), 0, 0)
            add(fingerPad(0), 1, 0)
            add(fingerPad(0), 2, 0)
            add(fingerPad(0), 3, 0)
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

    private fun fingerPad(rotate: Int): Node {
        return hbox {
            height(4.0)
            width(56.0)
            style = "-fx-background-color: orange; -fx-rotate: $rotate;"
        }
    }

    private fun GridPane.makeSlider(index: Int, text: String) {
        val tooltip = Tooltip("Control the $text of the QArm between (-20°, 20°)")
        add(label {
            this.text = text
            this.tooltip = tooltip
        }, 0, index)
        add(slider {
            this.value = 50.0
            width(120.0)
            this.tooltip = tooltip
        }, 1, index)
        add(textField {
            this.text = "0"
            width(60.0)
            this.tooltip = tooltip
        }, 2, index)
    }

    override fun getNode() = panel
}