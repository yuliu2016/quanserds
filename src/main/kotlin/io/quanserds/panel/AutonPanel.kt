package io.quanserds.panel

import io.quanserds.fx.*
import io.quanserds.icon.fontIcon
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import org.kordamp.ikonli.materialdesign2.MaterialDesignL
import org.kordamp.ikonli.materialdesign2.MaterialDesignP
import org.kordamp.ikonli.materialdesign2.MaterialDesignR
import kotlin.math.round

class AutonPanel : ControlPanel {

    val panel = vbox {
        styleClass("modular-panel")
        maxWidth = 300.0
        spacing = 8.0
        padding = Insets(8.0)

        add(choiceBox<String> {
            items.addAll("Project 3 Example", "Project 2 Example", "Project 0 Example")
            selectionModel.select(0)
        })

        add(listView<String> {
            for (i in 0..15) {
                val a = round(Math.random() * 100) / 100
                val b = round(Math.random() * 100) / 100
                val c = round(Math.random() * 100) / 100
                items.add("QArm Move ($a, $b, $c)")
            }
            items.addAll("QArm Move (1,2,3)")
            width(140.0)
        })

        vspace()
        add(hbox {
            spacing = 8.0
            align(Pos.CENTER_RIGHT)
            add(Button("", fontIcon(MaterialDesignP.PLUS, 20)))
            add(Button("", fontIcon(MaterialDesignP.PLAY, 20)))
            add(Button("", fontIcon(MaterialDesignL.LANGUAGE_PYTHON, 20)))
        })
    }

    override fun getNode() = panel
}