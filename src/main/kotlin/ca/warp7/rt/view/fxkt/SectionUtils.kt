package ca.warp7.rt.view.fxkt

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority

fun sectionBar(t: String): HBox {

    return HBox().apply {
        styleClass.add("split-pane-section")
        prefHeight = 36.dp2px
        minHeight = 36.dp2px
        maxHeight = 36.dp2px
        padding = Insets(0.0, 0.0, 0.0, 8.dp2px)
        children.add(Label(t.toUpperCase()))
        children.add(HBox().apply {
            HBox.setHgrow(this, Priority.ALWAYS)
        })
        alignment = Pos.CENTER_LEFT
    }
}

