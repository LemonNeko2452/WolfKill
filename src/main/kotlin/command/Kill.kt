package work.anqi.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import work.anqi.WolfKill
import work.anqi.WolfKillRoom
import work.anqi.config.CommandConfig

object Kill : SimpleCommand(
    WolfKill,
    primaryName = "kill",
    secondaryNames = CommandConfig.Kill,
    description = "狼人刀人投票"
) {
    @ConsoleExperimentalApi
    @ExperimentalCommandDescriptors
    override val prefixOptional = true

    @ConsoleExperimentalApi
    @Handler
    suspend fun CommandSenderOnMessage<*>.handle(code: Int) {
        WolfKillRoom.rooms.forEach { room_it ->
            room_it.wolfs.forEach {
                if (it.id == fromEvent.sender.id) {
                    if (room_it.detail?.section != 2) {
                        return
                    }
                    if (room_it.members.size < code || code < 0) {
                        sendMessage("参数超出范围")
                        return
                    }
                    var flag_all_kill = true
                    room_it.wolfs.forEach {
                        if (it.id == fromEvent.sender.id) {
                            it.role.kill(code)
                        }
                        if (!it.role.flag_action) {
                            flag_all_kill = false
                        }
                    }
                    // 所有狼人完成投票
                    if (flag_all_kill) {
//            fromEvent.sender.bot.getGroup(roomId)?.sendMessage("狼人请闭眼")
                        room_it.willDie()
                        room_it.detail?.section = room_it.detail?.section!! + 1
                        room_it.detail?.act = false
                    }
                }
            }
        }
    }
}