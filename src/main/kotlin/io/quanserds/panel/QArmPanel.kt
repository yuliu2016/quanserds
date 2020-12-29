package io.quanserds.panel

import io.quanserds.fx.*
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.GridPane

class QArmPanel : ControlPanel {

    private val panel = vbox {
        style = "-fx-background-color: #1e2e4a"
        maxWidth = 280.0
        spacing = 8.0
        padding = Insets(8.0)
        add(gridPane {
            hgap = 8.0
            vgap = 8.0
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
                isDisable = true
            }, 0, 1)
            add(textField {
                width(80.0)
                text = "0.0000"
                isDisable = true
            }, 1, 1)
            add(textField {
                width(80.0)
                text = "0.4826"
                isDisable = true
            }, 2, 1)
        })
    }

    private fun gridLabel(text: String): Label {
        return label {
            this.text = text
            GridPane.setHalignment(this, HPos.CENTER)
        }
    }

    private fun GridPane.makeSlider(index: Int, text: String) {
        add(label(text), 0, index)
        add(slider {
            width(120.0)
        }, 1, index)
        add(textField {
            this.text = "0"
            width(60.0)
        }, 2, index)
    }

    override fun getNode() = panel
}