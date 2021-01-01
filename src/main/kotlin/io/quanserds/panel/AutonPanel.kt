package io.quanserds.panel

import io.quanserds.fx.add
import io.quanserds.fx.choiceBox
import io.quanserds.fx.styleClass
import io.quanserds.fx.vbox
import io.quanserds.icon.fontIcon
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.Button
import org.kordamp.ikonli.materialdesign2.MaterialDesignL
import org.kordamp.ikonli.materialdesign2.MaterialDesignP
import org.kordamp.ikonli.materialdesign2.MaterialDesignR

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

        add(Button("Record", fontIcon(MaterialDesignR.RECORD, 20)))
        add(Button("Generate Code", fontIcon(MaterialDesignL.LANGUAGE_PYTHON, 20)))
        add(Button("Replay", fontIcon(MaterialDesignP.PLAY, 20)))
    }

    override fun getNode() = panel
}