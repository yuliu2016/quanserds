package ca.warp7.rt.view.fxkt

import javafx.geometry.Pos
import javafx.scene.layout.HBox
import org.kordamp.ikonli.Ikon
import org.kordamp.ikonli.javafx.FontIcon

@FXKtDSL
fun fontIcon(ic: Ikon, size: Int): FontIcon {

    return FontIcon(ic).apply {
        iconSize = size.dp2px.toInt()
    }
}

@FXKtDSL
fun FontIcon.centerIn(width: Int): HBox = hbox {
    add(this@centerIn)
    prefWidth = width.dp2px
    alignment = Pos.CENTER
}