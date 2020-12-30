package io.quanserds.panel

import io.quanserds.fx.*
import io.quanserds.icon.fontIcon
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.GridPane
import org.kordamp.ikonli.materialdesign2.MaterialDesignC
import org.kordamp.ikonli.materialdesign2.MaterialDesignH
import org.kordamp.ikonli.materialdesign2.MaterialDesignI
import org.kordamp.ikonli.materialdesign2.MaterialDesignP

class QArmPanel : ControlPanel {

    private val panel = vbox {
        style = "-fx-background-color: #1e2e4a"
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
                isEditable = false
            }, 0, 1)
            add(textField {
                width(80.0)
                text = "0.0000"
                isEditable = false
            }, 1, 1)
            add(textField {
                width(80.0)
                text = "0.4826"
                isEditable = false
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
            add(Button("", fontIcon(MaterialDesignC.CURSOR_TEXT, 20)).apply {
                tooltip = Tooltip("Move to Position")
            })
            add(Button("", fontIcon(MaterialDesignC.CALCULATOR, 20)).apply {
                tooltip = Tooltip("QArm Math Calculator")
            })
            add(Button("", fontIcon(MaterialDesignI.INFORMATION_OUTLINE, 20)).apply {
                tooltip = Tooltip("QArm Info")
            })
            add(Button("", fontIcon(MaterialDesignH.HISTORY, 20)).apply {
                tooltip = Tooltip("Recent Positions")
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

    private fun gridLabel(text: String): Label {
        return label {
            this.text = text
            GridPane.setHalignment(this, HPos.CENTER)
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