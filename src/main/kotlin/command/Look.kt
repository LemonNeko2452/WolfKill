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

object Look : SimpleCommand(
    WolfKill,
    primaryName = "look",
    secondaryNames = CommandConfig.Look,
    description = "预言家查人"
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
        if (thisRoom.detail?.section != 3) {
            return
        }
        var goods: MutableList<Members> = mutableListOf<Members>()
        var all_member: MutableList<Members> = mutableListOf<Members>()

        goods = thisRoom.goods
        all_member = thisRoom.members
        val roomId = thisRoom.room

        if (all_member.size < code || code < 1) {
            sendMessage("参数超出范围")
            return
        }

        val yTip = buildMessageChain {
            +all_member[code - 1].toString()
            +"的身份是"
            +all_member[code - 1].role.name
        }

        var flag_all_act = true
        goods.forEach {
            if (it.role.id == 3) {
                if(it.role.flag_action){
                    return
                }
                if (it.id == fromEvent.sender.id) {
                    fromEvent.sender.bot.getGroup(roomId)?.getMember(fromEvent.sender.id)?.sendMessage(yTip)
                    it.role.flag_action = true
                }
                if (!it.role.flag_action) {
                    flag_all_act = false
                }

            }
        }
        if (flag_all_act) {
//            fromEvent.sender.bot.getGroup(roomId)?.sendMessage("预言家请闭眼")
            thisRoom.detail?.section = thisRoom.detail?.section!! + 1
            thisRoom.detail?.act = false
        }
    }
}