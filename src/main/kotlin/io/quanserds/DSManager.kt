package io.quanserds

import io.quanserds.comm.api.Container
import io.quanserds.command.Command

interface DSManager {
    fun mail(container: Container)

    fun submit(command: Command)

    fun stopConnection()

    fun restartConnection()

    fun showPanelInfo(info: String)
}