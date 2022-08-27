package work.anqi.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import work.anqi.WolfKill
import work.anqi.WolfKillRoom
import work.anqi.config.CommandConfig
import work.anqi.data.Members
import work.anqi.data.ROOMS

object Vote : SimpleCommand(
    WolfKill,
    primaryName = "vote",
    secondaryNames = CommandConfig.Vote,
    description = "狼人杀投票"
) {
    @ConsoleExperimentalApi
    @ExperimentalCommandDescriptors
    override val prefixOptional = true

    @ConsoleExperimentalApi
    @Handler
    suspend fun CommandSenderOnMessage<*>.handle(code: Int) {
        val groupId = getGroupOrNull()?.id ?: sendMessage("该群不存在房间").also { return }
        var thisRoom = ROOMS(0L)
        WolfKillRoom.rooms.forEach {
            if (groupId == it.room) {
                thisRoom = it
            }
        }
        if (thisRoom.room == 0L) {
            return
        }
        if (thisRoom.detail?.section != 7) {
            return
        }

        var all_member: MutableList<Members> = mutableListOf<Members>()
        all_member = thisRoom.members
        val roomId = thisRoom.room

        if (all_member.size < code || code < 0) {
            sendMessage("参数超出范围")
            return
        }
        var flag_all_vote = true
        all_member.forEach {
            if (it.id == fromEvent.sender.id) {
                if (it.role.id == 6) {
                    it.role.vote(0)
                } else {
                    it.role.vote(code)
                }
            }
            if (!it.role.flag_voted) {
                flag_all_vote = false
            }
        }
        // 所有人完成投票
        if (flag_all_vote) {
            fromEvent.sender.bot.getGroup(roomId)?.sendMessage("所有人完成投票")
            thisRoom.voteOut()
            thisRoom.detail?.section = thisRoom.detail?.section!! + 1
            thisRoom.detail?.act = false
        }
    }
}