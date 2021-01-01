package io.quanserds

import io.quanserds.fx.*
import io.quanserds.icon.fontIcon
import io.quanserds.panel.*
import javafx.application.Application
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.text.Font
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.kordamp.ikonli.Ikon
import org.kordamp.ikonli.materialdesign2.*

class QuanserDS : Application() {
    override fun start(stage: Stage) {

        Font.loadFont(
            QuanserDS::class.java.getResource("/Audiowide-Regular.ttf")
                .toExternalForm(), 20.0
        )

        val bounds = Screen.getPrimary().visualBounds;
        val h = 348.0
        stage.title = "Quanser Driver Station"
        stage.x = 0.0
        stage.y = bounds.maxY - h
        stage.initStyle(StageStyle.UNDECORATED)
        val ic = Image(QuanserDS::class.java.getResourceAsStream("/icon.png"))
        stage.icons.add(ic)
        stage.scene = Scene(vbox {
            padding = Insets(8.0)
            spacing = 8.0
            style = "-fx-background-color: rgb(0, 68, 126)"
            add(hbox {
                spacing = 8.0
                padding = Insets(0.0, 4.0, 0.0, 4.0)
                height(24.0)
                align(Pos.CENTER_LEFT)
                add(ImageView(ic).apply {
                    isPreserveRatio = true
                    fitWidth = 20.0
                    this.isSmooth = false
                })
                // Audiowide?
                add(Label("Quanser Driver Station").apply {
                    style = "-fx-font-size: 14"
                })

                add(label("v${BuildConfig.kVersion}").apply {
                    style = "-fx-text-fill: #0f0; -fx-font-size: 14"
                })

                add(toggleTab("Connection", MaterialDesignA.ACCESS_POINT, true))
                add(toggleTab("QArm", MaterialDesignR.ROBOT_INDUSTRIAL, true))
                add(toggleTab("QBot2e", MaterialDesignR.ROBOT_VACUUM, true))
                add(toggleTab("Table", MaterialDesignF.FERRIS_WHEEL, true))
                add(toggleTab("Autoclave", MaterialDesignF.FILE_CABINET, false))
                add(toggleTab("Autonomous", MaterialDesignC.CONSOLE_LINE, false))

                hspace()

                add(Button("", fontIcon(MaterialDesignW.WINDOW_MINIMIZE, 20)).also {
                    it.tooltip = Tooltip("Minimize Window")
                    it.setOnMouseClicked { stage.isIconified = true }
                })
                add(Button("", fontIcon(MaterialDesignW.WINDOW_CLOSE, 20)).also {
                    it.tooltip = Tooltip("Close Window")
                    it.setOnMouseClicked { stage.close() }
                })
            })
            add(hbox {
                vgrow()
                spacing = 8.0
                add(CommsPanel().getNode())
                add(QArmPanel().getNode())
                add(QBot2ePanel().getNode())
                add(TablePanel().getNode())
                add(AutoclavePanel().getNode())
                add(AutonPanel().getNode())
            })
        }).apply {
            accelerators[combo(KeyCode.T, control = true, shift = true)] = Runnable {
                showThreads()
            }
            stylesheets.addAll("/quanserds.css")
        }
        stage.width = bounds.width
        stage.height = h
        stage.show()
    }

    private fun showThreads() {
        val traces = Thread.getAllStackTraces()
        val t = traces.keys.sortedBy { it.name }.joinToString("\n") {
            val name = it.name + " ".repeat(26 - it.name.length)
            "$name Priority:${it.priority}  Daemon:${it.isDaemon}  Group:${it.threadGroup.name}"
        }
        Splash.alert(null, "Threads", t, true)
    }

    private fun toggleTab(text: String, icon: Ikon, selected: Boolean) =
        ToggleButton(text, fontIcon(icon, 16)).apply {
            tooltip = Tooltip("Toggle the $text Tab")
            styleClass("toggle-button-tab")
            isSelected = selected
        }
}