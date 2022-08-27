package work.anqi.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.message.data.buildMessageChain
import work.anqi.WolfKill
import work.anqi.WolfKillRoom
import work.anqi.config.CommandConfig
import work.anqi.data.Members
import work.anqi.data.ROOMS

object Shoot : SimpleCommand(
    WolfKill,
    primaryName = "shoot",
    secondaryNames = CommandConfig.Shoot,
    description = "猎人技能"
) {
    @ConsoleExperimentalApi
    @ExperimentalCommandDescriptors
    override val prefixOptional = true

    @ConsoleExperimentalApi
    @Handler
    suspend fun CommandSenderOnMessage<*>.handle(code: Int) {
        var thisRoom = ROOMS(0L)
        WolfKillRoom.rooms.forEach { room_it ->
            room_it.dead_hunter.forEach {
                if (it.id == fromEvent.sender.id) {
                    thisRoom = room_it
                }
            }
        }
        sendMessage("111")
        if (!(thisRoom.detail?.section == 6 || thisRoom.detail?.section == 9)) {
            return
        }
        sendMessage("www")
        var all_member: MutableList<Members> = mutableListOf<Members>()

        all_member = thisRoom.members
        val roomId = thisRoom.room

        if (all_member.size < code || code < 0) {
            sendMessage("参数超出范围")
            return
        }
        if (code == 0) {
            thisRoom.dead_hunter.removeIf { it.id == fromEvent.sender.id }
            return
        }

        val yTip = buildMessageChain {
            +fromEvent.sender.nick
            +" 射杀了 "
            +all_member[code - 1].name
        }
        thisRoom.kill(all_member[code - 1].id)
        thisRoom.dead_hunter.forEach {
            if (it.id == fromEvent.sender.id) {
                fromEvent.sender.bot.getGroup(roomId)?.sendMessage(yTip)
            }
        }
        thisRoom.dead_hunter.removeIf { it.id == fromEvent.sender.id }
        if (thisRoom.dead_hunter.size == 0) {
            thisRoom.detail?.section = thisRoom.detail?.section!! + 1
            thisRoom.detail?.act = false
        }
    }
}