package io.quanserds

import io.quanserds.comm.api.Container
import io.quanserds.comm.api.ModularServer
import io.quanserds.command.Command

class Scheduler(private val panels: List<ControlPanel>) : DSManager {

    companion object {
        const val kPort = 18001
    }

    private val server = ModularServer(kPort)
    private val inbox = panels.associateBy { it.mailFilter }

    init {
//        server.connect()
        panels.forEach { it.accept(this) }
    }

    override fun mail(container: Container) {
        server.queueContainer(container)
    }

    override fun submit(command: Command) {
        panels.forEach { it.onCommandSubmitted(command) }
    }

    override fun stopConnection() {
        server.disconnect()
    }

    override fun restartConnection() {
        server.connect(false)
    }

    override fun showPanelInfo(info: String) {
    }
}