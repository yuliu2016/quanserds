package io.quanserds

import io.quanserds.comm.api.Container
import io.quanserds.command.Command
import javafx.scene.Node
import javafx.scene.input.KeyEvent
import org.kordamp.ikonli.Ikon

interface ControlPanel {

    val name: String
    val icon: Ikon
    val mailFilter: MailFilter

    fun getNode(): Node

    /**
     * should always be called before getNode is called
     */
    fun accept(manager: DSManager)

    /**
     * Handle keystrokes
     *
     * Runs on the proper UI thread
     */
    fun onKeyPressed(e: KeyEvent) {
        // do nothing
    }

    /**
     * When the connection status changes
     *
     * Important: Runs on the comms thread. Must use
     * [javafx.application.Platform.runLater] to modify
     * the UI
     */
    fun onConnectionStatus(state: ConnectionState, pings: ArrayDeque<CommLevel>, client: String) {
    }

    /**
     * Event handler for command submission
     *
     * Runs on the same thread as when [DSManager.submit] is called,
     * typically the UI thread
     */
    fun onCommandSubmitted(command: Command) {
    }

    /**
     * All data that the interface needs to update
     * (without user interaction) should happen here
     */
    fun periodicRequestData()

    /**
     * Filtered data according to mailFilter
     *
     * Important: Runs on the comms thread. Must use
     * [javafx.application.Platform.runLater] to modify
     * the UI
     */
    fun periodicResponseData(containers: List<Container>)
}