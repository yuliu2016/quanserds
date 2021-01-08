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
     */
    fun onKeyPressed(e: KeyEvent) {
        // do nothing
    }

    fun onKeyReleased(e: KeyEvent) {
        // do nothing
    }

    /**
     * When the connection status changes
     */
    fun onConnectionStatus(state: ConnectionState, pings: ArrayDeque<CommLevel>, client: String) {
    }

    /**
     * Event handler for command submission
     */
    fun onCommandSubmitted(command: Command) {
    }

    /**
     * All data that the interface needs to update
     * (without user interaction) should happen here
     */
    fun periodicRequestData(frame: Int)

    /**
     * Filtered data according to mailFilter
     */
    fun periodicResponseData(containers: List<Container>)
}