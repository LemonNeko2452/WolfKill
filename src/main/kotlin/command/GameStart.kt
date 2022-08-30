package work.anqi.command


import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.delay
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.nextMessage
import work.anqi.WolfKill
import work.anqi.WolfKillRoom
import work.anqi.config.CommandConfig
import work.anqi.data.Members
import work.anqi.data.ROOMS

object GameStart : SimpleCommand(
    WolfKill,
    primaryName = "game-start",
    secondaryNames = CommandConfig.GameStart,
    description = "创建游戏房间"
) {

    @ConsoleExperimentalApi
    @ExperimentalCommandDescriptors
    override val prefixOptional = true


    @ConsoleExperimentalApi
    @Handler

    suspend fun CommandSenderOnMessage<*>.handle() {
        val thisGroup = getGroupOrNull()?.id ?: sendMessage("群不存在").also { return }
        val thisId = fromEvent.sender.id
        var flagx = false
        WolfKillRoom.rooms.forEach { if (it.room == thisGroup) flagx = true }
        if (flagx) {
            sendMessage("游戏已存在")
            return
        }
        // 创建房间
        WolfKillRoom.rooms.add(ROOMS(thisGroup as Long, fromEvent.sender))
        sendMessage("请选择游戏模式\n1.标准模式\n2.自定义模式")
        val listener: CompletableJob = globalEventChannel().subscribeAlways<GroupMessageEvent> { event ->
            val flagG = group.id == thisGroup
            val flagM1 =
                message.contentToString().startsWith("1") or message.contentToString().startsWith("标准")
            val flagM2 =
                message.contentToString().startsWith("2") or message.contentToString().startsWith("自定义")
            val flagI = sender.id == thisId
            if (flagG and flagM1 and flagI) {
                WolfKillRoom.rooms.forEach {
                    if (it.room == thisGroup) {
                        it.members.removeAt(0)
                        it.members.add(Members(fromEvent.sender))
                        it.detail!!.join++
                    }
                }
                sendMessage("你选择了标准模式")
                sendMessage("请输入 join-game 加入游戏")
                flagx = true

            } else if (flagG and flagM2 and flagI) {

                val s = nextMessage(60000).filterIsInstance<PlainText>()[0].toString()
                var result = 0
                WolfKillRoom.rooms.forEach {
                    if (it.room == thisGroup) {
                        it.members.removeAt(0)
                        it.members.add(Members(fromEvent.sender))
                        it.detail!!.join++
                        result = it.build(s)
                    }
                }
                when (result) {
                    1 -> {
                        sendMessage("数目错误，请正确输入职业数目(6种职业)")
                    }

                    2 -> {
                        sendMessage("游戏人数超出限制，请重新开始游戏并控制人数在4~18人")
                    }

                    3 -> {
                        sendMessage("狼人数量过多，请重新开始游戏并控制狼人数量少于总人数一半")
                    }

                    4 -> {
                        sendMessage("成功创建房间")
                        sendMessage("请输入 join-game 加入游戏")
                    }
                }

                sendMessage("你选择了自定义模式")
                sendMessage("请依次输入各职业数目（狼人、村民、预言家、猎人、女巫、白痴）")
                sendMessage("示例:")
                sendMessage("3,2,1,1,1,0")
                sendMessage("将创建一个包含3名狼人、2名村民、1名预言家、1名猎人、1名女巫、0名白痴的8人游戏房间")
                flagx = true
            }
        }
        while (true) {
            delay(1000)
            if (flagx) {
                listener.complete()
                break
            }
        }
    }

}
