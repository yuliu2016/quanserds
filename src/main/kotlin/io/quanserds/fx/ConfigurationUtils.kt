@file:Suppress("unused")

package io.quanserds.fx

import javafx.scene.layout.HBox
import javafx.scene.layout.VBox


inline fun hbox(builder: HBox.() -> Unit): HBox = HBox().apply(builder)

inline fun vbox(builder: VBox.() -> Unit): VBox = VBox().apply(builder)