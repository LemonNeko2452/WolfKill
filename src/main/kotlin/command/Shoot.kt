package work.anqi.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.getMember
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
            room_it.goods.forEach {
                if (it.id == fromEvent.sender.id) {
                    thisRoom = room_it
                }
            }
        }
        if (!(thisRoom.detail?.section == 6 || thisRoom.detail?.section == 9)) {
            return
        }

        var goods: MutableList<Members> = mutableListOf<Members>()
        var all_member: MutableList<Members> = mutableListOf<Members>()
        goods = thisRoom.goods
        all_member = thisRoom.members
        val roomId = thisRoom.room

        if (all_member.size < code || code < 0) {
            sendMessage("参数超出范围")
            return
        }
        goods.forEach {
            if (it.role.id == 4) {
                if (it.id == fromEvent.sender.id) {
                    if(it.role.flag_action){
                        return
                    }
                    val yTip = buildMessageChain {
                        +fromEvent.sender.nick
                        +" 射杀了 "
                        +all_member[code - 1].name
                    }
                    if(code!=0){
                        fromEvent.sender.bot.getGroup(roomId)?.sendMessage(yTip)
                    }
                    it.role.flag_action = true
                }
            }
        }
        thisRoom.kill(all_member[code - 1].id)
    }
}