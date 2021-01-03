package io.quanserds

import io.quanserds.comm.api.Container
import io.quanserds.comm.api.ModularServer
import io.quanserds.command.Command

class Scheduler : DSManager {

//    val server by lazy { ModularServer(18001) }

    fun acceptAll(panels: List<ControlPanel>) {
        panels.forEach { it.accept(this) }
    }

    override fun mail(container: Container) {
    }

    override fun submit(command: Command) {
    }

    override fun stopConnection() {
    }

    override fun restartConnection() {
    }

    override fun showPanelInfo(info: String) {
    }
}