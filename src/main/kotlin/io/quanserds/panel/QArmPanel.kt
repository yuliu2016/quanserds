package io.quanserds.panel

import io.quanserds.fx.*
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node

class QArmPanel : ControlPanel {

    private val panel = vbox {
        style = "-fx-background-color: #1e2e4a"
        maxWidth = 400.0
        spacing = 8.0
        padding = Insets(8.0)
        add(makeSlider("Base"))
        add(makeSlider("Shoulder"))
        add(makeSlider("Elbow"))
        add(makeSlider("Wrist"))
        add(makeSlider("Gripper"))
    }

    private fun makeSlider(text: String) : Node {
        return hbox {
            spacing = 8.0
            align(Pos.CENTER_LEFT)
            add(label(text))
            add(slider {
                width(100.0)
            })
            add(textField {
                width(50.0)
            })
        }
    }

    override fun getNode() = panel
}