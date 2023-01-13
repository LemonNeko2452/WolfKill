package work.anqi.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.getMember
import work.anqi.WolfKill
import work.anqi.WolfKillRoom
import work.anqi.config.CommandConfig
import work.anqi.data.Members
import work.anqi.data.ROOMS

object Witch : SimpleCommand(
    WolfKill,
    primaryName = "witch",
    secondaryNames = CommandConfig.Witch,
    description = "女巫使用技能"
) {
    @ConsoleExperimentalApi
    @ExperimentalCommandDescriptors
    override val prefixOptional = true

    @ConsoleExperimentalApi
    @Handler
    suspend fun CommandSenderOnMessage<*>.handle(code: String) {
        var thisRoom = ROOMS(0L)
        WolfKillRoom.rooms.forEach { room_it ->
            room_it.goods.forEach {
                if (it.id == fromEvent.sender.id) {
                    thisRoom = room_it
                }
            }
        }
        if (thisRoom.detail?.section != 4) {
            return
        }
        var goods: MutableList<Members> = mutableListOf<Members>()
        var all_member: MutableList<Members> = mutableListOf<Members>()
        goods = thisRoom.goods
        all_member = thisRoom.members
        val roomId = thisRoom.room



        var flag_all_act = true
        goods.forEach {
            if (it.role.id == 5) {
                if (it.id == fromEvent.sender.id) {
                    if (!it.role.flag_action) {
                        when (code) {
                            "save" -> {
                                if (it.role.healing_potion < 1) {
                                    fromEvent.sender.bot.getGroup(roomId)?.getMember(fromEvent.sender.id)
                                        ?.sendMessage("治疗药水数量不足")
                                } else {
                                    thisRoom.will_dea = 0L
                                    it.role.healing_potion -= 1
                                    it.role.flag_action = true
                                }
                            }

                            "skip" -> {
                                it.role.flag_action = true
                            }

                            else -> {
                                if (all_member.size < code.toInt() || code.toInt() < 1) {
                                    sendMessage("参数超出范围")
                                }
                                else{
                                    val id = thisRoom.members[code.toInt() - 1].id
                                    if (id !in thisRoom.witch_kill) {
                                        thisRoom.witch_kill.add(id)
                                    }
                                    it.role.healing_potion -= 1
                                    it.role.flag_action = true
                                }
                            }
                        }
                    }
                }
                if (!it.role.flag_action) {
                    flag_all_act = false
                }
            }

        }
        if (flag_all_act) {
//            fromEvent.sender.bot.getGroup(roomId)?.sendMessage("女巫请闭眼")
            thisRoom.detail?.section = thisRoom.detail?.section!! + 1
            thisRoom.detail?.act = false
        }
    }

}