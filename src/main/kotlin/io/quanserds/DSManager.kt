package io.quanserds

import io.quanserds.comm.api.Container

interface DSManager {
    fun mail(container: Container)

    fun stopConnection()

    fun restartConnection()
}