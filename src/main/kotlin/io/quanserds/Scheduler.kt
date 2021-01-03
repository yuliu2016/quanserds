package io.quanserds

import io.quanserds.ConnectionState.*
import io.quanserds.comm.api.Container
import io.quanserds.comm.api.ModularServer
import io.quanserds.command.Command
import java.util.*
import kotlin.concurrent.schedule
import kotlin.concurrent.scheduleAtFixedRate

class Scheduler(private val panels: List<ControlPanel>) : DSManager {

    companion object {
        const val kPort = 18001
        const val kUpdateRate = 1000L // millis
    }

    private val commServer = ModularServer(kPort)
    private val timer = Timer("QDS Comms Scheduler", true)
    private val inboxFilters = panels.associateBy { it.mailFilter }

    private var state = AwaitingConnection
    private var client = "None"

    private val commands = mutableListOf<Command>()

    private val pings = MutableList(5) { CommLevel.NoConnection }

    init {
        panels.forEach { it.accept(this) }

        // Start by waiting for a connection
        commServer.connect(false)
        state = AwaitingConnection

        // Start a periodic timer
        timer.scheduleAtFixedRate(0L, kUpdateRate) { periodicUpdate() }
    }

    private fun updatePingStatus(commLevel: CommLevel) {
        pings.add(commLevel)
        pings.removeAt(0)
        panels.forEach {
            it.onConnectionStatus(state, pings, client)
        }
    }

    private fun periodicUpdate() {
        when (state) {
            AwaitingConnection -> periodicAwaitConnection()
            Connected -> periodicUpdateConnection()
            Disconnected -> periodicUpdateDisconnected()
        }
    }

    private fun periodicAwaitConnection() {
        if (commServer.acceptAsynchronously()) {
            // Established a connection; begin to read data
            client = commServer.clientAddress
            updatePingStatus(CommLevel.Data)
            state = Connected
        }
    }

    private fun periodicUpdateConnection() {
        // Step 1 - Check mail
        if (commServer.receiveNewData()) {
            val containers = commServer.receivedContainers
            parseContainers(containers)

            if (containers.isEmpty()) {
                updatePingStatus(CommLevel.NoData)
            } else {
                updatePingStatus(CommLevel.Data)
            }
        } else {
            updatePingStatus(CommLevel.NoData)
        }

        // Step 2 - Add all requests for data
        panels.forEach {
            it.periodicRequestData()
        }

        // Step 3 - Run commands
        val finished = commands.filter { it.isFinished() }
        finished.forEach { it.stop() }
        commands.removeAll(finished)

        // Step 4 - Deliver
        commServer.sendQueue()
    }

    private fun parseContainers(containers: List<Container>) {
        inboxFilters.entries.forEach {
            val filters = it.key
            val panel = it.value
            val response = containers.filter { c -> c.deviceID in filters }
            panel.periodicResponseData(response)
        }
    }

    private fun periodicUpdateDisconnected() {
        updatePingStatus(CommLevel.NoConnection)
    }

    override fun mail(container: Container) {
        // must run on the scheduler thread
        commServer.queueContainer(container)
    }

    override fun submit(command: Command) {
        timer.schedule(0L) {
            panels.forEach { it.onCommandSubmitted(command) }
            command.start()
            commands.add(command)
        }
    }

    override fun stopConnection() {
        // context-switch to comms thread with delay 0
        timer.schedule(0L) {
            disconnectAll()
            updatePingStatus(CommLevel.NoConnection)
        }
    }

    private fun disconnectAll() {
        commands.clear()
        client = "None"
        commServer.disconnect()
        state = Disconnected

        // Try to get rid of some memory...
        Runtime.getRuntime().gc()
    }

    override fun stopAll() {
        commServer.close()
    }

    override fun restartConnection() {
        if (state != Disconnected) {
            // incorrect state
            return
        }
        // context-switch to comms thread with delay 0
        timer.schedule(0L) {
            commServer.connect(false)
            state = AwaitingConnection
        }
    }
}