package io.quanserds.panel

import io.quanserds.ControlPanel
import io.quanserds.DSManager
import io.quanserds.comm.api.CommAPI.*
import io.quanserds.comm.api.Container
import io.quanserds.fx.*
import io.quanserds.icon.fontIcon
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.util.StringConverter
import org.kordamp.ikonli.materialdesign2.MaterialDesignF
import org.kordamp.ikonli.materialdesign2.MaterialDesignI
import org.kordamp.ikonli.materialdesign2.MaterialDesignR
import org.kordamp.ikonli.materialdesign2.MaterialDesignT

class TablePanel : ControlPanel {

    companion object {
        private const val kEnc = 360.0 / 4096
    }

    override val name = "Table"
    override val icon = MaterialDesignF.FERRIS_WHEEL

    override val mailFilter = listOf(ID_SRV02BOTTLETABLE, ID_SCALE)

    private lateinit var dsManager: DSManager

    override fun accept(manager: DSManager) {
        dsManager = manager
    }

    override fun periodicRequestData(frame: Int) {
        val ds = dsManager
        ds.postMail(srv02BottleTable_RequestEncoder(0))
        ds.postMail(srv02BottleTable_RequestTOF(0))
        if (shortRadio.isSelected) {
            ds.postMail(srv02BottleTable_RequestProximityShort(0))
        } else {
            ds.postMail(srv02BottleTable_RequestProximityTall(0))
        }
        ds.postMail(srv02BottleTable_RequestLoadMass(0))

        // scale
        ds.postMail(scale_RequestLoadMass(0))
    }

    override fun periodicResponseData(containers: List<Container>) {
        containers.forEach { it.parse() }
    }

    private fun Container.parse() = when (deviceFunction) {
        FCN_SRV02BT_RESPONSE_ENCODER -> {
            val enc = srv02BottleTable_ResponseEncoder(this)
            encoder.text = enc.toString()
            angle.text = (enc * kEnc % 360.0).f
        }
        FCN_SRV02BT_RESPONSE_TOF -> {
            val tofv = srv02BottleTable_ResponseTOF(this)
            tof.text = tofv.f
        }
        FCN_SRV02BT_RESPONSE_PROXIMITY_TALL,
        FCN_SRV02BT_RESPONSE_PROXIMITY_SHORT -> {
            val s = srv02BottleTable_ResponseProximityTall(this)
            proxX.text = s.relative_x.f
            proxY.text = s.relative_y.f
            proxZ.text = s.relative_z.f
            proxP.text = s.properties.ifBlank { "No Proximity Properties" }
        }
        FCN_SRV02BT_RESPONSE_LOAD_MASS -> when (deviceID) {
            ID_SCALE -> scale.text = scale_ResponesLoadMass(this).f
            ID_SRV02BOTTLETABLE -> mass.text =
                srv02BottleTable_ResponseLoadMass(this).f
            else -> {
            }
        }
        else -> {
        }
    }

    private val shortRadio = RadioButton("Short")
    private val tallRadio = RadioButton("Tall")

    init {
        val g = ToggleGroup()
        shortRadio.toggleGroup = g
        tallRadio.toggleGroup = g
        shortRadio.isSelected = true
    }

    private val encoder = tf()
    private val angle = tf()
    private val mass = tf()
    private val tof = tf()
    private val scale = tf()

    private val proxX = tf()
    private val proxY = tf()
    private val proxZ = tf()
    private val proxP = textField {
        width(186.0)
        isEditable = false
        text = "No Proximity Properties"
    }

    private val metalRadio = RadioButton("Metal")
    private val plasticRadio = RadioButton("Plastic")

    private val alphaSlider = slider {
        value = 100.0
        width(120.0)
    }

    private val alphaField = tf()

    init {
        val g = ToggleGroup()
        metalRadio.toggleGroup = g
        plasticRadio.toggleGroup = g
        metalRadio.isSelected = true

        alphaField.textProperty().bindBidirectional(alphaSlider.valueProperty(),
            object : StringConverter<Number>() {
                override fun toString(ob: Number): String {
                    return ob.toInt().toString() + "%"
                }

                override fun fromString(string: String?): Number {
                    error("Can't convert back")
                }
            })
    }

    private val colorRadio = RadioButton("")
    private val randomRadio = RadioButton("Random")

    private val colorPicker = ColorPicker().apply {
        this.width(120.0)
        this.translateX = 8.0
    }

    init {
        val g = ToggleGroup()
        colorRadio.toggleGroup = g
        randomRadio.toggleGroup = g
        randomRadio.isSelected = true
        colorRadio.graphic = colorPicker
    }

    private val leftPanel = vbox {
        spacing = 8.0
        add(hbox {
            align(Pos.CENTER_LEFT)
            spacing = 8.0
            add(label("Velocity"))
            add(slider {
                value = 50.0
                width(120.0)
            })
            add(textField {
                text = "0"
                width(60.0)
            })
        })

        add(hbox {
            spacing = 8.0
            add(label("Proximity Sensor"))
            add(shortRadio)
            add(tallRadio)
        })

        add(gridPane {
            hgap = 8.0
            vgap = 4.0
            add(gridLabel("x"), 0, 0)
            add(gridLabel("y"), 1, 0)
            add(gridLabel("z"), 2, 0)
            add(proxX, 0, 1)
            add(proxY, 1, 1)
            add(proxZ, 2, 1)
            add(proxP, 0, 2, 3, 1)
        })


        vspace()

        add(hbox {
            spacing = 8.0
            add(label("Material"))
            add(metalRadio)
            add(plasticRadio)
        })

        add(hbox {
            align(Pos.CENTER_LEFT)
            spacing = 8.0
            add(label("Alpha"))
            add(alphaSlider)
            add(alphaField)
        })

        add(hbox {
            align(Pos.CENTER_LEFT)
            spacing = 16.0
            add(colorRadio)
            add(randomRadio)
        })
    }

    private val rightPanel = vbox {
        spacing = 4.0
        align(Pos.TOP_CENTER)
        add(vertBox("Encoder", tf()))
        add(vertBox("Angle", tf()))
        add(vertBox("Mass", tf()))
        add(vertBox("TOF", tf()))
        add(vertBox("Scale", tf()))
    }

    private fun spawn() {
        val metal = metalRadio.isSelected
        val r: Float
        val g: Float
        val b: Float
        if (randomRadio.isSelected) {
            r = Math.random().toFloat()
            g = Math.random().toFloat()
            b = Math.random().toFloat()
        } else {
            val c = colorPicker.value
            r = c.red.toFloat()
            g = c.green.toFloat()
            b = c.blue.toFloat()
        }
        val alpha = alphaSlider.value / 100.0
        val cont = srv02BottleTable_SpawnContainer(
            0, 0.1f, 0.65f, if (metal) 1 else 0,
            r, g, b, alpha.toFloat(), 1f, 1f,
            if (metal) "metal" else "plastic"
        )

        dsManager.postMail(cont)
    }

    private val megaPanel = vbox {
        styleClass("modular-panel")
        spacing = 8.0
        padding = Insets(8.0)


        add(hbox {
            spacing = 8.0
            add(leftPanel)
            add(rightPanel)
            vgrow()
        })

        add(hbox {
            align(Pos.CENTER_RIGHT)
            spacing = 8.0
            add(Button("", fontIcon(MaterialDesignT.TRANSFER_DOWN, 20)).apply {
                setOnAction { spawn() }
            })
            hspace()
            add(Button("", fontIcon(MaterialDesignR.REWIND_5, 20)).apply {
                tooltip = Tooltip("Turn 5 deg counter-clockwise")
            })
            add(Button("", fontIcon(MaterialDesignR.REWIND_30, 20)).apply {
                tooltip = Tooltip("Turn 30 deg counter-clockwise")
            })

            add(Button("", fontIcon(MaterialDesignF.FAST_FORWARD_30, 20)).apply {
                tooltip = Tooltip("Turn 30 deg clockwise")
            })

            add(Button("", fontIcon(MaterialDesignF.FAST_FORWARD_5, 20)).apply {
                tooltip = Tooltip("Turn 5 deg clockwise")
            })
            add(Button("", fontIcon(MaterialDesignI.INFORMATION_OUTLINE, 20)).apply {
                tooltip = Tooltip("SRV03 Bottle Table Info")
            })
        })
    }

    override fun getNode() = megaPanel
}