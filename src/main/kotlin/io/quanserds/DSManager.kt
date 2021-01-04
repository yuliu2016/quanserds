package io.quanserds

import io.quanserds.comm.api.Container
import io.quanserds.command.Command

interface DSManager {

    fun postMail(container: Container)

    fun submit(command: Command)

    fun stopConnection()

    fun restartConnection()

    fun stopAll()
}