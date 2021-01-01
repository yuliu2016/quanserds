package io.quanserds.command

interface Command {
    val shortName: String
        get() = this::class.java.simpleName
    val name: String
        get() = shortName
    val pyCommand: String
        get() = "print(\"$name\")"

    fun start() {
    }

    fun isFinished(): Boolean {
        return false
    }

    fun execute() {

    }

    fun stop() {
    }
}

private class InstantCommand(
    override val shortName: String,
    override val name: String,
    override val pyCommand: String,
    val func: () -> Unit
) : Command {
    override fun start() {
        func()
    }

    override fun isFinished(): Boolean {
        return true
    }
}

fun instantCommand(
    shortName: String,
    name: String,
    pyCommand: String,
    func: () -> Unit
): Command = InstantCommand(shortName, name, pyCommand, func)