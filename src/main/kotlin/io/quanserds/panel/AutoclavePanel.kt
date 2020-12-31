package io.quanserds.panel

import io.quanserds.fx.*
import io.quanserds.icon.fontIcon
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.RadioButton
import javafx.scene.control.ToggleGroup
import javafx.scene.control.Tooltip
import javafx.scene.layout.GridPane
import org.kordamp.ikonli.materialdesign2.MaterialDesignC
import org.kordamp.ikonli.materialdesign2.MaterialDesignI
import org.kordamp.ikonli.materialdesign2.MaterialDesignT

class AutoclavePanel : ControlPanel {

    private val panel = vbox {
        style = "-fx-background-color: #1e2e4a"
        maxWidth = 300.0
        spacing = 8.0
        padding = Insets(8.0)

        add(gridPane {
            vgap = 8.0
            hgap = 8.0
            add(label("R"), 0, 0)
            add(label("G"), 0, 1)
            add(label("B"), 0, 2)
            openClose(0)
            openClose(1)
            openClose(2)
        })

        add(gridPane {
            vgap = 4.0
            hgap = 8.0
            add(label("EMG Left"), 0, 0)
            add(label("EMG Right"), 0, 1)
            add(label("EMG Equal"), 0, 2)
            add(tf("0.0"), 1, 0)
            add(tf("0.0"), 1, 1)
            add(tf("True"), 1, 2)
        })

        vspace()

        add(choiceBox<String> {
            items.addAll(
                "(1) Red Small",
                "(2) Red Large",
                "(3) Blue Small",
                "(4) Blue Large",
                "(5) Green Small",
                "(6) Green Large"
            )
            selectionModel.select(0)
        })

        add(hbox {
            align(Pos.CENTER_RIGHT)
            spacing = 8.0
            add(Button("", fontIcon(MaterialDesignC.CUBE, 20)).apply {
                tooltip = Tooltip("Spawn Container")
            })
            hspace()
            add(Button("", fontIcon(MaterialDesignI.INFORMATION_OUTLINE, 20)).apply {
                tooltip = Tooltip("Autoclave Info")
            })
        })
    }

    private fun tf(t: String) = textField {
        text = t
        isEditable = false
        width(60.0)
    }

    private fun GridPane.openClose(row: Int) {
        val a = RadioButton("Closed")
        val b = RadioButton("Open")
        val group = ToggleGroup()
        a.toggleGroup = group
        b.toggleGroup = group

        a.isSelected = true

        add(a, 1, row)
        add(b, 2, row)
    }

    override fun getNode() = panel
}