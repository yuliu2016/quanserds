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
import javafx.scene.text.Font
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window
import org.kordamp.ikonli.Ikon
import org.kordamp.ikonli.materialdesign2.*

class QuanserDS : Application() {

    private val panels = listOf(
        CommsPanel(),
        AutonPanel(),
        QArmPanel(),
        QBot2ePanel(),
        TablePanel(),
        AutoclavePanel()
    )

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
                add(Label("DS").apply {
                    tooltip = Tooltip("Quanser Driver Station")
                    style = "-fx-font-size: 14"
                })

                add(label("v${BuildConfig.kVersion}").apply {
                    style = "-fx-text-fill: #0f0; -fx-font-size: 14"
                })

                hspace()

                for (panel in panels) {
                    add(toggleTab(panel.name, panel.icon, true))
                }

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
                align(Pos.CENTER)
                for (panel in panels) {
                    add(panel.getNode())
                }
            })
        }).apply {
            accelerators[combo(KeyCode.T, control = true, shift = true)] = Runnable {
                showThreads()
            }
            accelerators[combo(KeyCode.I, control = true, shift = true)] = Runnable {
                showInfo(stage, ic)
            }
            setOnKeyPressed {
                it?.let { e ->
                    for (panel in panels) {
                        panel.onKeyPressed(e)
                    }
                }
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

    private fun showInfo(window: Window, icon: Image) {
        Splash.info(window, BuildConfig.kVersion, icon)
    }

    private fun toggleTab(text: String, icon: Ikon, selected: Boolean) =
        ToggleButton(text, fontIcon(icon, 16)).apply {
            tooltip = Tooltip("Toggle the $text Tab")
            styleClass("toggle-button-tab")
            isSelected = selected
        }
}