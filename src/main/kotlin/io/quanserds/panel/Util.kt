package io.quanserds.panel

import io.quanserds.fx.*
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.GridPane

internal val Double.f get() = "%.3f".format(this)
internal val Float.f get() = "%.3f".format(this)

fun tf() = textField {
    text = "0"
    isEditable = false
    width(60.0)
}

fun vertBox(title: String, vararg node: Node) = vbox {
    spacing = 4.0
    align(Pos.TOP_CENTER)
    add(label(title))
    node.forEach { add(it) }
}

fun Node.gridCenter() = apply {
    GridPane.setHalignment(this, HPos.CENTER)
}

fun gridLabel(text: String): Label {
    return label {
        this.text = text
        GridPane.setHalignment(this, HPos.CENTER)
    }
}