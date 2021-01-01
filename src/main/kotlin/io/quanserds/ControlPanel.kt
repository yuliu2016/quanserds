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

    // N.W. should always be called before getNode is called
    fun accept(manager: DSManager)

    /**
     * Handle keystrokes
     */
    fun onKeyPressed(e: KeyEvent) {
        // do nothing
    }

    fun onConnectionStatus(pings: BooleanArray, server: String, client: String) {
    }

    fun onCommandSubmitted(command: Command) {
    }

    /**
     * All data that the interface needs to update
     * (without user interaction) should happen here
     */
    fun periodicRequestData()

    /**
     * Filtered data according to mailFilter
     */
    fun periodicResponseData(containers: List<Container>)
}