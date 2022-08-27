package work.anqi.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import work.anqi.WolfKill
import work.anqi.WolfKillRoom
import work.anqi.config.CommandConfig


object GameStop : SimpleCommand(
    WolfKill,
    primaryName = "game-stop",
    secondaryNames = CommandConfig.GameStop,
    description = "游戏暂停"
) {

    @ConsoleExperimentalApi
    @ExperimentalCommandDescriptors
    override val prefixOptional = true

    @ConsoleExperimentalApi
    @Handler
    suspend fun CommandSenderOnMessage<*>.handle() {
        val thisGroup = getGroupOrNull()?.id ?: sendMessage("群不存在").also { return }
        val thisId = fromEvent.sender.id
        var flagx = true
        WolfKillRoom.rooms.forEach {
            if (thisGroup == it.room) {
                it.members.forEach {
                    if (it.id == thisId) {
                        flagx = false
                    }
                }
            }
        }
        if (flagx) {
            sendMessage("只有游戏参与者才有权限结束游戏")
            return
        }
        for (room in WolfKillRoom.rooms) {
            if (room.room == (thisGroup ?: 0)) {
                WolfKillRoom.rooms.remove(room)
                sendMessage("狼人杀已结束")
                break
            }
        }
    }
}