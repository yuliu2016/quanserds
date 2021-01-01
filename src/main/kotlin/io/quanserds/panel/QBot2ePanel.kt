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
import org.kordamp.ikonli.materialdesign2.*

class QBot2ePanel : ControlPanel {

    private val mainPanel = vbox {
        spacing = 8.0

        add(gridPane {
            hgap = 8.0
            vgap = 4.0
            add(Label("W", fontIcon(MaterialDesignA.ARROW_UP, 20)).apply {
                style = "-fx-text-fill: #0f0"
                width(38.0)
            }, 0, 0)
            add(slider { width(120.0); value = 50.0 }, 1, 0)
            add(Label("S", fontIcon(MaterialDesignA.ARROW_DOWN, 20)).apply {
                style = "-fx-text-fill: #0f0"
                width(38.0)
            }, 2, 0)
            add(textField {
                text = "0"
                width(60.0)
            }, 3, 0)

            add(Label("A", fontIcon(MaterialDesignR.ROTATE_LEFT, 20)).apply {
                style = "-fx-text-fill: #0f0"
                width(38.0)
            }, 0, 1)
            add(slider { width(120.0); value = 50.0 }, 1, 1)
            add(Label("D", fontIcon(MaterialDesignR.ROTATE_RIGHT, 20)).apply {
                style = "-fx-text-fill: #0f0"
                width(38.0)
            }, 2, 1)
            add(textField {
                text = "0"
                width(60.0)
            }, 3, 1)
        })

        add(gridPane {
            hgap = 8.0
            vgap = 4.0
            add(label("Left").gridCenter(), 1, 0)
            add(label("Right").gridCenter(), 2, 0)
            add(label("Velocity"), 0, 1)
            add(tf(), 1, 1)
            add(tf(), 2, 1)
            add(label("Encoder"), 0, 2)
            add(tf(), 1, 2)
            add(tf(), 2, 2)
        })

        add(gridPane {
            hgap = 8.0
            vgap = 4.0
            add(label("x").gridCenter(), 1, 0)
            add(label("y").gridCenter(), 2, 0)
            add(label("z").gridCenter(), 3, 0)
            add(label("World"), 0, 1)
            add(tf(), 1, 1)
            add(tf(), 2, 1)
            add(tf(), 3, 1)
            add(label("Forward"), 0, 2)
            add(tf(), 1, 2)
            add(tf(), 2, 2)
            add(tf(), 3, 2)
            add(label("Up"), 0, 3)
            add(tf(), 1, 3)
            add(tf(), 2, 3)
            add(tf(), 3, 3)
        })

        vspace()
    }

    private fun Node.gridCenter() = apply {
        GridPane.setHalignment(this, HPos.CENTER)
    }

    private fun tf() = textField {
        text = "0"
        isEditable = false
        width(60.0)
    }

    private val panel2 = vbox {
        spacing = 4.0
        align(Pos.TOP_CENTER)
        add(vertBox("Colour", hbox {
            height(4.0)
            style = "-fx-background-color: #ffdc0c"
        }, tf()))
        add(vertBox("Depth", tf()))
        add(vertBox("Heading", tf()))
        add(vertBox("Gyro", tf()))
    }

    private fun vertBox(title: String, vararg node: Node) = vbox {
        spacing = 4.0
        align(Pos.TOP_CENTER)
        add(label(title))
        node.forEach { add(it) }
    }

    private val megaPanel = vbox {

        styleClass("modular-panel")
        maxWidth = 460.0
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
            add(Button("", fontIcon(MaterialDesignC.CALCULATOR, 20)).apply {
                tooltip = Tooltip("QBot2e Math Calculator")
            })
            add(Button("", fontIcon(MaterialDesignI.INFORMATION_OUTLINE, 20)).apply {
                tooltip = Tooltip("QBot2e Info")
            })
        })
    }

    override fun getNode() = megaPanel
}