package io.quanserds

import ca.warp7.rt.view.aads.kDarkCSS
import ca.warp7.rt.view.aads.kMainCSS
import io.quanserds.fx.*
import javafx.application.Application
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import kotlin.math.sqrt

class QuanserDS : Application() {
    override fun start(stage: Stage) {

        Font.loadFont(QuanserDS::class.java.getResource("/Audiowide-Regular.ttf")
            .toExternalForm(), 20.0)

        val bounds = Screen.getPrimary().visualBounds;
        val h = bounds.height - bounds.width / ((3 + sqrt(5.0)) / 2)
        stage.title = "Quanser Driver Station"
        stage.x = 0.0
        stage.y = bounds.maxY - h
        stage.initStyle(StageStyle.UNDECORATED)
        val ic = Image(QuanserDS::class.java.getResourceAsStream("/icon.png"))
        stage.icons.add(ic)
        stage.isAlwaysOnTop = true
        stage.scene = Scene(hbox {
            padding = Insets(8.0)
            spacing = 8.0
            style = "-fx-background-color: rgb(0, 68, 126)"
            add(vbox {
                maxWidth = 260.0
                spacing = 8.0
                add(hbox {
                    spacing = 8.0
                    align(Pos.CENTER)
                    add(ImageView(ic).apply {
                        isPreserveRatio = true
                        fitWidth = 28.0
                        this.isSmooth = false
                    })
                    // Audiowide?
                    add(Label("Quanser Driver Station").apply {
                        style = "-fx-font-size: 14"
                    })
                })
                add(vbox {
                    style = "-fx-background-color: #1e2e4a"
                    vgrow()
                    align(Pos.CENTER)
                    spacing = 8.0
                    add(hbox {
                        align(Pos.CENTER)
                        spacing = 8.0
                        add(Label("Communication").apply {
                            style = "-fx-font-weight: bold"
                            prefWidth = 150.0
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
                        add(Label("Fleet Management").apply {
                            style = "-fx-font-weight: bold"
                            prefWidth = 150.0
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
                        add(Label("Robot Code").apply {
                            style = "-fx-font-weight: bold"
                            prefWidth = 150.0
                        })
                        add(vbox {
                            prefWidth = 25.0
                            maxHeight = 10.0
                            style = "-fx-background-color:red"
                        })
                    })

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
//                add(hbox {
//                    spacing = 8.0
//                    add(enable)
//                    add(disable)
//                })
            })
            add(Button("Exit").apply {
                setOnMouseClicked {
                    stage.close()
                }
            })
        }).apply {
            stylesheets.addAll(kMainCSS, kDarkCSS)
        }
        stage.width = bounds.width
        stage.height = h
        stage.show()
    }
}