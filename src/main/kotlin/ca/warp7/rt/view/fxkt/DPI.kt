package ca.warp7.rt.view.fxkt

import javafx.stage.Screen

private val effectiveDPI: Double = (Screen.getPrimary().dpi + 160) / 2

val Int.dp2px: Double get() = this * (effectiveDPI / 160)