package io.quanserds

import io.quanserds.ConnectionState.*
import io.quanserds.comm.api.Container
import io.quanserds.comm.api.ModularServer
import io.quanserds.command.Command
import javafx.animation.AnimationTimer
import kotlin.collections.ArrayDeque

class Scheduler(private val panels: List<ControlPanel>) : DSManager {

    companion object {
        const val kPort = 18001
        const val kUpdateRate = 0.05e9.toLong() // nano
    }

    private val commServer = ModularServer(kPort)

    private val timer2 = object : AnimationTimer() {
        var previousTime = 0L
        override fun handle(now: Long) {
            val delta = now - previousTime
            if (delta > kUpdateRate) {
                periodicUpdate()
                previousTime = now
            }
        }
    }
    private val inboxFilters = panels.associateBy { it.mailFilter }

    private var state = AwaitingConnection
    private var client = "None"

    private val commands = mutableListOf<Command>()

    private val pings = ArrayDeque<CommLevel>(30)

    init {
        for (i in 0 until 30) {
            pings.addLast(CommLevel.NoConnection)
        }

        panels.forEach { it.accept(this) }

        // Start by waiting for a connection
        commServer.connect(false)
        state = AwaitingConnection

        // Start a periodic timer
        timer2.start()
    }

    private fun updatePingStatus(commLevel: CommLevel) {
        pings.addLast(commLevel)
        pings.removeFirst()
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

    private var updateFrames = 0

    private fun periodicAwaitConnection() {
        if (commServer.acceptAsynchronously()) {
            // Established a connection; begin to read data
            client = commServer.clientAddress
            updatePingStatus(CommLevel.NoData)
            state = Connected
            updateFrames = 0
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

        // Step 2 - Add all requests for data and commands
        panels.forEach {
            it.periodicRequestData(updateFrames)
        }

        // Step 3 - Run commands
        val finished = commands.filter { it.isFinished() }
        finished.forEach { it.stop() }
        commands.removeAll(finished)
        commands.forEach { it.execute() }

        // Step 4 - Deliver and move on to next frame
        commServer.sendQueue()
        updateFrames += 1
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

    override fun postMail(container: Container) {
        commServer.queueContainer(container)
    }

    override fun submit(command: Command) {
        panels.forEach { it.onCommandSubmitted(command) }
        command.start()
        commands.add(command)
    }

    override fun stopConnection() {
        commands.clear()
        client = "None"
        commServer.disconnect()
        state = Disconnected
        updatePingStatus(CommLevel.NoConnection)

        // Try to get rid of some memory...
        Runtime.getRuntime().gc()
    }

    override fun stopAll() {
        timer2.stop()
        commServer.close()
    }

    override fun restartConnection() {
        if (state != Disconnected) {
            // incorrect state
            return
        }
        commServer.connect(false)
        state = AwaitingConnection
    }
}