package io.quanserds.panel

import io.quanserds.fx.*
import io.quanserds.icon.fontIcon
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.text.TextAlignment
import org.kordamp.ikonli.materialdesign2.MaterialDesignA
import org.kordamp.ikonli.materialdesign2.MaterialDesignR
import org.kordamp.ikonli.materialdesign2.MaterialDesignS

class CommsPanel : ControlPanel {
    val panel = vbox {
        maxWidth = 260.0
        spacing = 8.0
        add(vbox {
            style = "-fx-background-color: #1e2e4a"
            vgrow()
            align(Pos.TOP_CENTER)
            spacing = 8.0
            padding = Insets(8.0)
            add(hbox {
                align(Pos.CENTER)
                spacing = 8.0
                add(Label("Communication").apply {
                    style = "-fx-font-weight: bold"
                    prefWidth = 165.0
                })
                add(vbox {
                    prefWidth = 25.0
                    maxHeight = 10.0
                    style = "-fx-background-color:red"
                })
            })
            add(hbox {
                align(Pos.CENTER)
                spacing = 8.0
                add(Label("Server").apply {
                    style = "-fx-font-weight: bold"
                    prefWidth = 85.0
                })
                add(Label("localhost:18001").apply {
                    style = "-font-family: 'Arial';-fx-text-fill: #0f0; -fx-font-weight: bold"
                    prefWidth = 105.0
                })
            })

            add(hbox {
                align(Pos.CENTER)
                spacing = 8.0
                add(Label("Simulator").apply {
                    style = "-fx-font-weight: bold"
                    prefWidth = 75.0
                })
                add(Label("192.168.0.1:63345").apply {
                    style = "-font-family: 'Arial';-fx-text-fill: #f80; -fx-font-weight: bold"
                    prefWidth = 115.0
                })
            })

            vspace()
            add(Button("", fontIcon(MaterialDesignS.STOP, 16)))
            add(Button("", fontIcon(MaterialDesignR.RELOAD, 16)))
        })
        add(vbox {
            style = "-fx-background-color: #1e2e4a"
            prefHeight = 70.0
            padding = Insets(0.0, 8.0, 0.0, 8.0)
            align(Pos.CENTER)
            add(Label("No Simulator Communication").apply {
                isWrapText = true
                textAlignment = TextAlignment.CENTER
                style = "-fx-font-size: 20; -fx-font-weight:bold"
            })
        })
    }

    override fun getNode() = panel
}