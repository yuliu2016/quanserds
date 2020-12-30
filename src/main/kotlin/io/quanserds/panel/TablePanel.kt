package io.quanserds.panel

import io.quanserds.fx.*
import io.quanserds.icon.fontIcon
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import org.kordamp.ikonli.materialdesign2.MaterialDesignF
import org.kordamp.ikonli.materialdesign2.MaterialDesignI
import org.kordamp.ikonli.materialdesign2.MaterialDesignR
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
            spacing = 8.0
            val g = ToggleGroup()
            val a = RadioButton("Metal")
            val b = RadioButton("Plastic")
            a.toggleGroup = g
            b.toggleGroup = g
            a.isSelected = true
            add(a)
            add(b)
        })

        add(hbox {
            align(Pos.CENTER_LEFT)
            spacing = 8.0
            add(label("Alpha"))
            add(slider {
                value = 50.0
                width(120.0)
            })
            add(textField {
                text = "0"
                width(60.0)
            })
        })

        add(hbox {
            align(Pos.CENTER_LEFT)
            spacing = 16.0
            val g = ToggleGroup()
            val a = RadioButton("")
            val b = RadioButton("Random")
            a.toggleGroup = g
            b.toggleGroup = g
            a.isSelected = true
            a.graphic = ColorPicker().apply {
                this.width(120.0)
                this.translateX = 8.0
            }
            a.contentDisplay = ContentDisplay.GRAPHIC_ONLY
            add(a)
            add(b)
        })

        add(hbox {
            align(Pos.CENTER_RIGHT)
            spacing = 8.0
            add(Button("", fontIcon(MaterialDesignT.TRANSFER_DOWN, 20)))
            hspace()
            add(Button("", fontIcon(MaterialDesignR.REWIND_5, 20)).apply {
                tooltip = Tooltip("Turn 5 deg counter-clockwise")
            })
            add(Button("", fontIcon(MaterialDesignR.REWIND_30, 20)).apply {
                tooltip = Tooltip("Turn 30 deg counter-clockwise")
            })

            add(Button("", fontIcon(MaterialDesignF.FAST_FORWARD_30, 20)).apply {
                tooltip = Tooltip("Turn 30 deg clockwise")
            })

            add(Button("", fontIcon(MaterialDesignF.FAST_FORWARD_5, 20)).apply {
                tooltip = Tooltip("Turn 5 deg clockwise")
            })
            add(Button("", fontIcon(MaterialDesignI.INFORMATION_OUTLINE, 20)).apply {
                tooltip = Tooltip("SRV03 Bottle Table Info")
            })
        })
    }

    override fun getNode() = panel
}