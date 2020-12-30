package io.quanserds.panel

import io.quanserds.fx.*
import io.quanserds.icon.fontIcon
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Tooltip
import org.kordamp.ikonli.materialdesign2.MaterialDesignI
import org.kordamp.ikonli.materialdesign2.MaterialDesignT

class TablePanel : ControlPanel {

    private val panel = vbox {
        style = "-fx-background-color: #1e2e4a"
        maxWidth = 360.0
        spacing = 8.0
        padding = Insets(8.0)

        add(hbox {
            align(Pos.CENTER_LEFT)
            spacing = 8.0
            add(label("Velocity"))
            add(slider {
                value = 50.0
                width(120.0)
            })
            add(textField {
                text = "0"
                width(60.0)
            })
        })

        vspace()

        add(hbox {
            align(Pos.CENTER_RIGHT)
            spacing = 8.0
            add(Button("", fontIcon(MaterialDesignT.TRANSFER_DOWN, 20)))
            hspace()
            add(Button("", fontIcon(MaterialDesignI.INFORMATION_OUTLINE, 20)).apply {
                tooltip = Tooltip("SRV03 Bottle Table Info")
            })
        })
    }

    override fun getNode() = panel
}