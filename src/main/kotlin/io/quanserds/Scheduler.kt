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
    private val inbox = panels.associateBy { it.mailFilter }

    init {
        panels.forEach { it.accept(this) }
        timer.scheduleAtFixedRate(0L, kUpdateRate) { periodicUpdate() }
    }

    private var state = AwaitingConnection
    private var client = "None"

    private val pings = mutableListOf<Boolean>()
    private val pingsArray = BooleanArray(5)

    private fun updatePingStatus(connected: Boolean) {
        pings.add(connected)
        if (pings.size > 5) {
            pings.removeAt(0)
        }
        for (i in 0 until 5) {
            pingsArray[i] = pings.getOrNull(i) ?: false
        }
        panels.forEach {
            it.onConnectionStatus(state, pingsArray, client)
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
            updatePingStatus(true)
            state = Connected
        }
    }

    private fun periodicUpdateConnection() {
        // Step 1 - Check mail
        if (commServer.receiveNewData()) {
            val containers = commServer.receivedContainers
            if (containers.isEmpty()) {
                updatePingStatus(false)
            } else {
                parseContainers(containers)
                // leftover containers are discarded after this poing
            }
        } else {
            updatePingStatus(false)
        }

        // Step 2 - Add all requests for data
        panels.forEach {
            it.periodicRequestData()
        }

        // Step 3 - Run commands

        // Step 4 - Deliver
        commServer.sendQueue()
    }

    private fun parseContainers(containers: List<Container>) {

    }

    private fun periodicUpdateDisconnected() {
        // history of the entire do nothing i guess
    }


    override fun mail(container: Container) {
        // must run on the scheduler thread
        commServer.queueContainer(container)
    }

    override fun submit(command: Command) {
        panels.forEach { it.onCommandSubmitted(command) }
    }

    override fun stopConnection() {
        // context-switch to comms thread with delay 0
        timer.schedule(0) {
            commServer.disconnect()
            state = Disconnected

            // Try to get rid of some memory...
            Runtime.getRuntime().gc()
        }
    }

    override fun restartConnection() {
        // context-switch to comms thread with delay 0
        timer.schedule(0) {
            commServer.connect(false)
            state = AwaitingConnection
        }
    }
}