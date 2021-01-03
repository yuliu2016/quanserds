package io.quanserds

import io.quanserds.fx.*
import io.quanserds.icon.fontIcon
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ToggleButton
import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.kordamp.ikonli.Ikon
import org.kordamp.ikonli.materialdesign2.MaterialDesignW

class QDSWindow(private val stage: Stage, private val panels: List<ControlPanel>) {

    private val scheduler = Scheduler()

    init {
        scheduler.acceptAll(panels)
    }

    private val appIcon = Image(QuanserDS::class.java.getResourceAsStream("/icon.png"))

    private val content = vbox {
        padding = Insets(8.0)
        spacing = 8.0
        style = "-fx-background-color: rgb(0, 68, 126)"

        add(hbox {
            spacing = 8.0
            padding = Insets(0.0, 4.0, 0.0, 4.0)
            height(24.0)
            align(Pos.CENTER_LEFT)
            add(ImageView(appIcon).apply {
                isPreserveRatio = true
                fitWidth = 20.0
                this.isSmooth = false
            })

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
    }

    val scene = Scene(content).apply {
        accelerators[combo(KeyCode.T, control = true, shift = true)] = Runnable {
            showThreads()
        }
        accelerators[combo(KeyCode.I, control = true, shift = true)] = Runnable {
            showInfo()
        }
        accelerators[combo(KeyCode.G, control = true, shift = true)] = Runnable {
            Splash.gc(stage)
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


    init {
        val bounds = Screen.getPrimary().visualBounds;
        val h = 348.0
        stage.title = "Quanser Driver Station"
        stage.x = 0.0
        stage.y = bounds.maxY - h
        stage.initStyle(StageStyle.UNDECORATED)
        stage.icons.add(appIcon)
        stage.scene = scene
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

    private fun showInfo() {
        Splash.info(stage, BuildConfig.kVersion, appIcon)
    }

    @Suppress("SameParameterValue")
    private fun toggleTab(text: String, icon: Ikon, selected: Boolean) =
        ToggleButton(text, fontIcon(icon, 16)).apply {
            tooltip = Tooltip("Toggle the $text Tab")
            styleClass("toggle-button-tab")
            isSelected = selected
        }
}