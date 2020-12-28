@file:Suppress("unused", "SpellCheckingInspection", "NOTHING_TO_INLINE")

package io.quanserds.fx

import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import javafx.scene.layout.VBox

@FXKtDSL
fun <T : Node> T.styleClass(sc: String): T {
    styleClass.add(sc)
    properties["FXKtStyleClass"] = sc
    return this
}

@FXKtDSL
fun Node.noStyleClass() {
    styleClass.remove(properties["FXKtStyleClass"] ?: "")
}

@FXKtDSL
fun HBox.align(pos: Pos) {
    alignment = pos
}

@FXKtDSL
fun VBox.align(pos: Pos) {
    alignment = pos
}

@FXKtDSL
fun Region.height(height: Double) {
    prefHeight = height
}

@FXKtDSL
fun Region.width(width: Double) {
    prefWidth = width
}