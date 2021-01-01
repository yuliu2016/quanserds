package io.quanserds.panel

import io.quanserds.ControlPanel
import io.quanserds.DSManager
import io.quanserds.comm.api.CommAPI
import io.quanserds.comm.api.Container
import io.quanserds.fx.*
import io.quanserds.icon.fontIcon
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
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

    override fun onConnectionStatus(pings: BooleanArray, server: String, client: String) {

    }

    val panel = vbox {
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
                repeat(5) {
                    add(commBox())
                }
            })
            add(hbox {
                spacing = 8.0
                align(Pos.CENTER_LEFT)
                add(Label("Server").apply {
                    style = "-fx-font-weight: bold"
                    prefWidth = 65.0
                })
                add(textField {
                    text = "localhost:18001"
                    isEditable = false
                    width(120.0)
                })
            })

            add(hbox {
                spacing = 8.0
                align(Pos.CENTER_LEFT)
                add(Label("Client").apply {
                    style = "-fx-font-weight: bold"
                    prefWidth = 65.0
                })
                add(textField {
                    text = "192.168.0.1:63345"
                    isEditable = false
                    width(120.0)
                })
            })

            vspace()
            add(hbox {
                spacing = 8.0
                padding = Insets(0.0, 4.0, 0.0, 4.0)
                align(Pos.CENTER_RIGHT)
                add(Button("", fontIcon(MaterialDesignS.STOP, 20)))
                add(Button("", fontIcon(MaterialDesignR.RELOAD, 20)))
            })
        })
        add(vbox {
            styleClass("modular-panel")
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

    private fun commBox() = vbox {
        prefWidth = 20.0
        maxHeight = 10.0
        style = "-fx-background-color:red"
    }

    override fun getNode() = panel
}