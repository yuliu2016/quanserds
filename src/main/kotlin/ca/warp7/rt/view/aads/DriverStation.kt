package ca.warp7.rt.view.aads

import io.quanserds.fx.*;
import javafx.application.Application
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.scene.text.TextAlignment
import javafx.scene.text.TextFlow
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle

class DriverStation : Application() {

    private var enabled = false

    private val enable = Button("Enable").apply {
        style = "-fx-font-size:22; -fx-font-weight: normal; -fx-text-fill: #00ff00; -fx-background-color:#1e2e4a"
        prefWidth = 200.0
        prefHeight = 70.0
        onAction = EventHandler {
            enabled = true
            updateEnabled()
        }
    }

    private val disable = Button("Disable").apply {
        style = "-fx-font-size:22; -fx-font-weight: bold; -fx-text-fill: red; -fx-background-color:black"
        prefWidth = 200.0
        prefHeight = 70.0
        onAction = EventHandler {
            enabled = false
            updateEnabled()
        }
    }

    private fun updateEnabled() {
        if (enabled) {
            enable.style = "-fx-font-size:22; -fx-font-weight: bold; -fx-text-fill: #00ff00; -fx-background-color:black"
            disable.style = "-fx-font-size:22; -fx-font-weight: bold; -fx-text-fill: red; -fx-background-color:#1e2e4a"
        } else {
            enable.style = "-fx-font-size:22; -fx-font-weight: bold; -fx-text-fill: #00ff00; -fx-background-color:#1e2e4a"
            disable.style = "-fx-font-size:22; -fx-font-weight: bold; -fx-text-fill: red; -fx-background-color:black"
        }
    }

    override fun start(stage: Stage) {
//        val ik = Image(DriverStation::class.java.getResourceAsStream(kIcon))
        val bounds = Screen.getPrimary().visualBounds
        val h = Screen.getPrimary().dpi * 3
        stage.title = "Driver Station"
        stage.isResizable = false
        stage.x = 0.0
        stage.y = bounds.maxY - h
        stage.initStyle(StageStyle.UNDECORATED)
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
//                    add(ImageView(ik).apply {
//                        isPreserveRatio = true
//                        fitWidth = 140.0
//                    })
                    add(Label("Driver Station").apply {
                        style = "-fx-font-weight: bold"
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
                    add(Label("No Robot Communication").apply {
                        isWrapText = true
                        textAlignment = TextAlignment.CENTER
                        style = "-fx-font-size: 20; -fx-font-weight:bold"
                    })
                })
                add(hbox {
                    spacing = 8.0
                    add(enable)
                    add(disable)
                })
            })
            add(vbox {
                prefWidth = 225.0
                maxWidth = 225.0
                spacing = 8.0
                style = "-fx-background-color: #1e2e4a"
                padding = Insets(8.0)
                align(Pos.TOP_CENTER)
                add(Label("Control Mode:").apply {
                    style = "-fx-font-weight: bold"
                })
                add(ChoiceBox((1..16).map { "Mode $it" }.observable()).apply {
                    selectionModel.select(0)
                    prefWidth = 250.0
                })
                vspace()
                add(hbox {
                    align(Pos.CENTER)
                    spacing = 8.0
                    add(Label("IP Address").apply {
                        style = "-fx-font-weight: bold"
                        prefWidth = 95.0
                    })
                    add(Label("192.168.1.1").apply {
                        style = "-fx-text-fill: #0f0; -fx-font-weight: bold"
                        prefWidth = 95.0
                    })
                })
                add(hbox {
                    align(Pos.CENTER)
                    spacing = 8.0
                    add(Label("Elapsed").apply {
                        style = "-fx-font-weight: bold"
                        prefWidth = 95.0
                    })
                    add(Label("0 min 0 s").apply {
                        style = "-fx-text-fill: #888; -fx-font-weight: bold"
                        prefWidth = 95.0
                    })
                })
                add(hbox {
                    align(Pos.CENTER)
                    spacing = 8.0
                    add(Label("Packet Loss").apply {
                        style = "-fx-font-weight: bold"
                        prefWidth = 95.0
                    })
                    add(Label("100%").apply {
                        style = "-fx-text-fill: #888; -fx-font-weight: bold"
                        prefWidth = 95.0
                    })
                })
                add(hbox {
                    align(Pos.CENTER)
                    spacing = 8.0
                    add(Label("Voltage").apply {
                        style = "-fx-font-weight: bold"
                        prefWidth = 95.0
                    })
                    add(Label("---").apply {
                        style = "-fx-text-fill: #888; -fx-font-weight: bold"
                        prefWidth = 95.0
                    })
                })
            })
            add(joy("Controller 1"))
            add(joy("Controller 2"))
            add(vbox {
                hgrow()
                spacing = 8.0
                add(hbox {
                    prefHeight = 30.0
                    spacing = 8.0
                    align(Pos.CENTER_LEFT)
                    add(hbox {
                        hgrow()
                        align(Pos.CENTER_LEFT)
                        style = "-fx-background-color: #1e2e4a"
                        add(Label("Packets").apply {
                            padding = Insets(8.0)
                            style = "-fx-font-weight:bold"
                        })
                    })

                    add(Button("Reboot").apply {
                        onAction = EventHandler {
                            stage.close()
                        }
                    })

                    add(Button("Restart Code").apply {
                        onAction = EventHandler {
                            stage.close()
                        }
                    })

                    add(Button("Exit").apply {
                        onAction = EventHandler {
                            stage.close()
                        }
                    })
                })
                add(vbox {
                    padding = Insets(8.0)
                    add(TextFlow().apply {
                        add(Label("Log").apply {
                            style = "-fx-font-weight:bold"
                        })
                    })
                    prefWidth = 400.0
                    vgrow()
                    style = "-fx-background-color: #1e2e4a"
                })
            })

        }).apply {
            stylesheets.addAll(kMainCSS, kDarkCSS)
            stage.width = bounds.width
            stage.height = h
        }
//        stage.icons.add(ik)
        stage.show()
    }

    fun joy(n: String): VBox {
        return vbox {
            prefWidth = 160.0
            padding = Insets(8.0)
            style = "-fx-background-color: #1e2e4a"
            spacing = 16.0
            add(hbox {
                align(Pos.CENTER)
                spacing = 8.0
                add(Label(n).apply {
                    style = "-fx-font-weight: bold"
                })
                add(vbox {
                    prefWidth = 25.0
                    maxHeight = 10.0
                    style = "-fx-background-color:red"
                })
            })
            vspace()
            add(hbox {
                align(Pos.CENTER)
                spacing = 58.0
                add(Label("LB").apply {
                    style = "-fx-font-weight: bold; -fx-text-fill: #b60"
                })
                add(Label("RB").apply {
                    style = "-fx-font-weight: bold; -fx-text-fill: #b60"
                })
            })
            add(hbox {
                spacing = 8.0
                add(hbox {
                    align(Pos.CENTER)
                    prefWidth = 68.0
                    add(Label("LT").apply {
                        style = "-fx-text-fill:gray"
                    })
                    style = "-fx-background-color:black; -fx-padding: 0; -fx-font-weight:bold"
                })
                add(hbox {
                    align(Pos.CENTER)
                    prefWidth = 68.0
                    add(Label("RT").apply {
                        style = "-fx-text-fill:gray; -fx-padding: 0; -fx-font-weight:bold"
                    })
                    style = "-fx-background-color:black"
                })
            })
            add(hbox {
                add(hbox {
                    prefWidth = 70.0
                    prefHeight = 70.0
                    style = "-fx-background-radius:35; -fx-background-insets: 0, 30; -fx-background-color: black, #0f0"
                })
                add(vbox {
                    hgrow()
                    align(Pos.CENTER)
                    add(hbox {
                        align(Pos.CENTER)
                        add(Label("Y").apply {
                            style = "-fx-font-weight: bold; -fx-text-fill: #b60"
                        })
                    })
                    add(hbox {
                        align(Pos.CENTER)
                        spacing = 35.0
                        add(Label("X").apply {
                            style = "-fx-font-weight: bold; -fx-text-fill: #b60"
                        })
                        add(Label("B").apply {
                            style = "-fx-font-weight: bold; -fx-text-fill: #b60"
                        })
                    })
                    add(hbox {
                        align(Pos.CENTER)
                        add(Label("A").apply {
                            style = "-fx-font-weight: bold; -fx-text-fill: #b60"
                        })
                    })
                })
            })
            add(hbox {
                add(vbox {
                    hgrow()
                    align(Pos.CENTER)
                    spacing = 8.0
                    add(Label("START").apply {
                        style = "-fx-font-weight: bold; -fx-text-fill: #b60"
                    })
                    add(Label("BACK").apply {
                        style = "-fx-font-weight: bold; -fx-text-fill: #b60"
                    })
                })

                add(hbox {
                    prefWidth = 70.0
                    prefHeight = 70.0
                    style = "-fx-background-radius:35; -fx-background-insets: 0, 30; -fx-background-color: black, #0f0"
                })
            })
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(DriverStation::class.java)
        }
    }
}

