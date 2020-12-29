package io.quanserds

import ca.warp7.rt.view.aads.kDarkCSS
import ca.warp7.rt.view.aads.kMainCSS
import io.quanserds.fx.*
import io.quanserds.icon.fontIcon
import io.quanserds.panel.CommsPanel
import io.quanserds.panel.QArmPanel
import javafx.application.Application
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.materialdesign2.*
import kotlin.math.sqrt

class QuanserDS : Application() {
    override fun start(stage: Stage) {

        Font.loadFont(QuanserDS::class.java.getResource("/Audiowide-Regular.ttf")
            .toExternalForm(), 20.0)

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

                add(ToggleButton("Connection", fontIcon(MaterialDesignA.ACCESS_POINT, 16)).apply {
                    isSelected = true
                })
                add(ToggleButton("QArm", fontIcon(MaterialDesignR.ROBOT_INDUSTRIAL, 16)).apply {
                    isSelected = true
                })
                add(ToggleButton("QBot2e", fontIcon(MaterialDesignR.ROBOT_VACUUM, 16)).apply {
                    isSelected = true
                })
                add(ToggleButton("Table", fontIcon(MaterialDesignF.FERRIS_WHEEL, 16)).apply {
                    isSelected = true
                })
                add(ToggleButton("Autoclave", fontIcon(MaterialDesignF.FILE_CABINET, 16)))
                add(ToggleButton("EMG", fontIcon(MaterialDesignA.ARM_FLEX, 16)))

                add(ToggleButton("Spawner", fontIcon(MaterialDesignP.PACKAGE_VARIANT, 16)))
                add(ToggleButton("Scale", fontIcon(MaterialDesignS.SCALE, 16)))

                hspace()

                add(fontIcon(MaterialDesignI.INFORMATION_OUTLINE, 20))
                add(fontIcon(MaterialDesignC.COG_OUTLINE, 20))
                add(fontIcon(MaterialDesignR.RELOAD, 20))
                add(fontIcon(MaterialDesignW.WINDOW_MINIMIZE, 20).also {
                    it.setOnMouseClicked { stage.isIconified = true }
                })
                add(fontIcon(MaterialDesignW.WINDOW_CLOSE, 20).also {
                    it.setOnMouseClicked { stage.close() }
                })
            })
            add(hbox {
                vgrow()
                spacing = 8.0
                add(CommsPanel().getNode())
                add(QArmPanel().getNode())
                add(vbox {
                    width(300.0)
                    style = "-fx-background-color: #1e2e4a"
                })
                add(vbox {
                    width(300.0)
                    style = "-fx-background-color: #1e2e4a"
                })
            })
        }).apply {
            stylesheets.addAll("/quanserds.css")
        }
        stage.width = bounds.width
        stage.height = h
        stage.show()
    }
}