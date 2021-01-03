package io.quanserds.panel

import io.quanserds.CommLevel
import io.quanserds.CommLevel.*
import io.quanserds.ConnectionState
import io.quanserds.ConnectionState.*
import io.quanserds.ControlPanel
import io.quanserds.DSManager
import io.quanserds.comm.api.Container
import io.quanserds.fx.*
import io.quanserds.icon.fontIcon
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import org.kordamp.ikonli.materialdesign2.MaterialDesignA
import org.kordamp.ikonli.materialdesign2.MaterialDesignR
import org.kordamp.ikonli.materialdesign2.MaterialDesignS

class CommsPanel : ControlPanel {

    override val name = "Connection"
    override val icon = MaterialDesignA.ACCESS_POINT

    override val mailFilter = listOf<Int>()

    override fun periodicRequestData() {
    }

    private lateinit var dsManager: DSManager

    override fun accept(manager: DSManager) {
        dsManager = manager
    }

    override fun periodicResponseData(containers: List<Container>) {
    }

    private val serverBox = textField {
        text = "localhost:18001"
        isEditable = false
        width(120.0)
    }

    private val clientBox = textField {
        text = "None"
        isEditable = false
        width(120.0)
    }

    private val commState = Label("No Simulator Communication").apply {
        isWrapText = true
        textAlignment = TextAlignment.CENTER
        style = "-fx-font-size: 20; -fx-font-weight:bold"
    }

    private val commCanvas = canvas {
        width = 120.0
        height = 12.0
    }
    private val gc = commCanvas.graphicsContext2D

    init {
        gc.fill = Color.BLACK
        gc.fillRect(0.0, 0.0, 120.0, 12.0)
    }

    private val disconnectBtn = Button("", fontIcon(MaterialDesignS.STOP, 20)).apply {
        setOnAction {
            dsManager.stopConnection()
        }
    }

    private val reconnectBtn = Button("", fontIcon(MaterialDesignR.RELOAD, 20)).apply {
        setOnAction {
            dsManager.restartConnection()
        }
    }

    override fun onConnectionStatus(state: ConnectionState, pings: ArrayDeque<CommLevel>, client: String) {
        Platform.runLater {
            clientBox.text = client
            commState.text = when (state) {
                AwaitingConnection -> "Waiting to Connect"
                Connected -> "Connected"
                Disconnected -> "Disconnected"
            }
            pings.forEachIndexed { index, level ->
                gc.fill = when (level) {
                    Data -> Color.valueOf("#0f0")
                    NoData -> Color.ORANGE
                    NoConnection -> Color.RED
                }
                val x = index * 4.0
                gc.fillRect(x, 0.0, 4.0, 12.0)
            }
        }
    }

    private val panel = vbox {
        maxWidth = 200.0
        spacing = 8.0

        val img = Image(CommsPanel::class.java.getResourceAsStream("/fireball_dark.png"))
        add(vbox {
            styleClass("modular-panel")
            vgrow()
            align(Pos.TOP_CENTER)
            spacing = 8.0
            padding = Insets(8.0)
            add(ImageView(img).apply {
                this.isSmooth = true
                isPreserveRatio = true
                this.fitWidth = 80.0
            })
            add(hbox {
                spacing = 8.0
                align(Pos.CENTER_LEFT)
                add(Label("Comms").apply {
                    style = "-fx-font-weight: bold"
                    prefWidth = 65.0
                })
                add(commCanvas)
            })
            add(hbox {
                spacing = 8.0
                align(Pos.CENTER_LEFT)
                add(Label("Server").apply {
                    style = "-fx-font-weight: bold"
                    prefWidth = 65.0
                })
                add(serverBox)
            })

            add(hbox {
                spacing = 8.0
                align(Pos.CENTER_LEFT)
                add(Label("Client").apply {
                    style = "-fx-font-weight: bold"
                    prefWidth = 65.0
                })
                add(clientBox)
            })

            vspace()
            add(hbox {
                spacing = 8.0
                padding = Insets(0.0, 4.0, 0.0, 4.0)
                align(Pos.CENTER_RIGHT)
                add(disconnectBtn)
                add(reconnectBtn)
            })
        })
        add(vbox {
            styleClass("modular-panel")
            prefHeight = 70.0
            padding = Insets(0.0, 8.0, 0.0, 8.0)
            align(Pos.CENTER)
            add(commState)
        })
    }

    override fun getNode() = panel
}