package io.quanserds.panel

import io.quanserds.fx.*
import io.quanserds.icon.fontIcon
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import org.kordamp.ikonli.materialdesign2.*

class QBot2ePanel : ControlPanel {

    private val panel = vbox {
        style = "-fx-background-color: #1e2e4a"
        maxWidth = 300.0
        spacing = 8.0
        padding = Insets(8.0)

        add(gridPane {
            hgap = 8.0
            vgap = 4.0
            add(Label("(W)", fontIcon(MaterialDesignA.ARROW_UP, 16)).apply {
                width(50.0)
            }, 0, 0)
            add(slider { width(120.0); value = 50.0 }, 1, 0)
            add(Label("(S)", fontIcon(MaterialDesignA.ARROW_DOWN, 16)).apply {
                width(50.0)
            }, 2, 0)
            add(textField {
                text = "0"
                width(60.0)
            }, 3, 0)

            add(Label("(A)", fontIcon(MaterialDesignR.ROTATE_LEFT, 16)).apply {
                width(50.0)
            }, 0, 1)
            add(slider { width(120.0); value = 50.0 }, 1, 1)
            add(Label("(D)", fontIcon(MaterialDesignR.ROTATE_RIGHT, 16)).apply {
                width(50.0)
            }, 2, 1)
            add(textField {
                text = "0"
                width(60.0)
            }, 3, 1)
        })

        add(gridPane {
            hgap = 8.0
            vgap = 4.0
            add(label("Left"), 1, 0)
            add(label("Right"), 2, 0)
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
            add(label("x"), 1, 0)
            add(label("y"), 2, 0)
            add(label("z"), 3, 0)
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

        add(hbox {
            align(Pos.CENTER_RIGHT)
            padding = Insets(0.0, 4.0, 0.0, 4.0)
            spacing = 8.0
            add(Button("", fontIcon(MaterialDesignS.STOP, 20)).apply {
                tooltip = Tooltip("Move to Home")
            })
            add(Button("", fontIcon(MaterialDesignC.CALCULATOR, 20)).apply {
                tooltip = Tooltip("QBot2e Math Calculator")
            })
            add(Button("", fontIcon(MaterialDesignC.CONTENT_COPY, 20)).apply {
                tooltip = Tooltip("Copy QBot2e Info")
            })
        })
    }

    private fun tf() = textField {
        text = "0"
        isEditable = false
        width(60.0)
    }

    override fun getNode() = panel
}