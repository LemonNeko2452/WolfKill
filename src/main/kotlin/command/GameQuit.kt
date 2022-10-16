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


object GameQuit : SimpleCommand(
    WolfKill,
    primaryName = "game-quit",
    secondaryNames = CommandConfig.GameQuit,
    description = "退出房间"
) {

    @ConsoleExperimentalApi
    @ExperimentalCommandDescriptors
    override val prefixOptional = true

    @ConsoleExperimentalApi
    @Handler
    suspend fun CommandSenderOnMessage<*>.handle() {
        val thisGroup = getGroupOrNull()?.id ?: sendMessage("群不存在").also { return }
        val thisId = fromEvent.sender.id
        var flag = 0
        var index = -1
        WolfKillRoom.rooms.forEach {
            if (thisGroup == it.room) {
                it.members.forEach {
                    if(it.id==thisId){
                        index=flag
                    }
                    else{
                        flag++
                    }
                }
                if(index==-1){
                    return
                }
                if(it.detail?.running == true){
                    return
                }
                it.members.removeAt(index)
                it.detail?.join = it.detail?.join?.minus(1)!!
                val chain = buildMessageChain {
                    +"正在等待玩家加入，当前人数："
                    add(it.detail!!.join.toString())
                    +"/"
                    add(it.detail!!.total.toString())
                }
                sendMessage(chain)
            }
        }
    }
}