package io.quanserds

import ca.warp7.rt.view.aads.kDarkCSS
import ca.warp7.rt.view.aads.kMainCSS
import io.quanserds.fx.add
import io.quanserds.fx.hbox
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle

class QuanserDS : Application() {
    override fun start(stage: Stage) {
        val bounds = Screen.getPrimary().visualBounds;
        val h = bounds.height / 3
        stage.title = "Quanser Driver Station"
        stage.x = 0.0
        stage.y = bounds.maxY - h
        stage.initStyle(StageStyle.UNDECORATED)
        stage.scene = Scene(hbox {
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