package io.quanserds

import io.quanserds.comm.api.Container
import io.quanserds.command.Command

interface DSManager {

    /**
     * NOT THREAD SAFE. The only place that mail
     * should be called are inside a command that
     * is submitted with [submit], or during
     * [ControlPanel.periodicRequestData], which
     * are guaranteed to run on the scheduler thread
     */
    fun mail(container: Container)

    fun submit(command: Command)

    fun stopConnection()

    fun restartConnection()
}