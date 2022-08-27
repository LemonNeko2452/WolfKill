package work.anqi.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.message.data.buildMessageChain
import work.anqi.WolfKill
import work.anqi.WolfKillRoom
import work.anqi.config.CommandConfig
import work.anqi.data.Members

object JoinGame : SimpleCommand(
    WolfKill,
    primaryName = "join-game",
    secondaryNames = CommandConfig.JoinGame,
    description = "加入狼人杀游戏房间"
) {
    @ConsoleExperimentalApi
    @ExperimentalCommandDescriptors
    override val prefixOptional = true

    @ConsoleExperimentalApi
    @Handler
    suspend fun CommandSenderOnMessage<*>.handle() {
        val thisGroup = getGroupOrNull()?.id ?: sendMessage("群不存在").also { return }
        val thisId = fromEvent.sender.id
        WolfKillRoom.rooms.forEach {
            if (it.room == (thisGroup ?: 0)) {

                it.members.forEach {
                    if (it.id == (thisId)) {
                        sendMessage("您已在游戏中")
                        return
                    }
                }
                if ((it.detail?.total ?: 12) <= (it.detail?.join ?: 0)) {
                    sendMessage("游戏人数已满")
                    return
                }
                it.members.add(Members(fromEvent.sender))
                it.detail?.join = it.detail?.join!! + 1
                sendMessage("加入成功")
                if ((it.detail?.total ?: 8) <= (it.detail?.join ?: 0)) {
                    sendMessage("游戏人数已满，请输入 Go 开始游戏")
                } else {
                    val chain = buildMessageChain {
                        +"正在等待玩家加入，当前人数："
                        add(it.detail!!.join.toString())
                        +"/"
                        add(it.detail!!.total.toString())
                    }
                    sendMessage(chain)
                }
                return
            }
        }
        sendMessage("加入失败，请先输入 game-start 创建游戏")
    }

}