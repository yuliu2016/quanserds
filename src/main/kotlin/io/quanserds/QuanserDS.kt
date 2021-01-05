package io.quanserds

import io.quanserds.panel.*
import javafx.application.Application
import javafx.scene.text.Font
import javafx.stage.Stage

class QuanserDS : Application() {

    override fun start(primaryStage: Stage) {

        Font.loadFont(
            QuanserDS::class.java.getResource("/Audiowide-Regular.ttf")
                .toExternalForm(), 20.0
        )

        QDSWindow(primaryStage, listOf(
            CommsPanel(),
            QArmPanel(),
            QBot2ePanel(),
            TablePanel(),
            AutoclavePanel()
        ))
    }
}