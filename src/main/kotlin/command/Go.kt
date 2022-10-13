package work.anqi.command


import kotlinx.coroutines.delay
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.getMember
import net.mamoe.mirai.contact.isFriend
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource

import work.anqi.WolfKill
import work.anqi.WolfKillRoom
import work.anqi.config.CommandConfig
import work.anqi.data.ROOMS

import work.anqi.data.Role
import java.io.File


object Go : SimpleCommand(
    WolfKill,
    primaryName = "go",
    secondaryNames = CommandConfig.Go,
    description = "游戏进行"
) {

    @ConsoleExperimentalApi
    @ExperimentalCommandDescriptors
    override val prefixOptional = true

    @ConsoleExperimentalApi
    @Handler
    suspend fun CommandSenderOnMessage<*>.handle() {
        val groupId = getGroupOrNull()?.id ?: sendMessage("该群不存在房间").also { return }
        val senderId = fromEvent.sender.id
        var thisRoom = ROOMS(0L)
        var flagx = true
        var flagy = false
        var flagz = false
        WolfKillRoom.rooms.forEach {
            if (groupId == it.room) {
                thisRoom = it
                it.members.forEach {
                    if (it.id == senderId) {
                        flagx = false
                    }
                }
                if ((it.detail?.join ?: 0) < (it.detail?.total ?: 8)) {
                    flagy = true
                }
                if (it.members[0].id == 0L) {
                    flagz = true
                }
                if (it.detail?.running == true) {
                    sendMessage("游戏进行中")
                    return
                }
            }
        }
        if (thisRoom.room == 0L) {
            return
        }
        if (flagx) {
            sendMessage("只有游戏参与者才有权限进行游戏")
            return
        }
        if (flagy) {
            sendMessage("游戏人数未满，不能进行游戏")
            return
        }
        if (flagz) {
            sendMessage("未选择游戏模式，将以标准模式进行游戏")
            WolfKillRoom.rooms.forEach {
                if (it.room == groupId) {
                    it.members.removeAt(0)
                }
            }
        }
        if (thisRoom.members[0].id == 0L) {
            thisRoom.members.removeAt(0)
        }

//        分发角色
        val list = thisRoom.roles
        thisRoom.members.forEach {
            val i = (0 until list.size).random()
            it.role = Role(list[i])
            if (list[i] == 1) {
                thisRoom.wolfs.add(it)
            } else {
                thisRoom.goods.add(it)
            }
            //  通告身份
            val rolename = when (list[i]) {
                1 -> "狼人"
                2 -> "村民"
                3 -> "预言家"
                4 -> "猎人"
                5 -> "女巫"
                6 -> "白痴"
                else -> "错误身份"
            }
            val img = when (list[i]) {
                1 -> File("data/img/langren.jpg").toExternalResource()
                2 -> File("data/img/cunmin.jpg").toExternalResource()
                3 -> File("data/img/yuyanjia.jpg").toExternalResource()
                4 -> File("data/img/lieren.jpg").toExternalResource()
                5 -> File("data/img/nvwu.jpg").toExternalResource()
                6 -> File("data/img/baichi.jpg").toExternalResource()
                else -> File("data/img/error.jpg").toExternalResource()

            }
            val chain1 = buildMessageChain {
                +"你的身份是\n"
                +Image(fromEvent.sender.uploadImage(img).imageId)
                +"\n"
                +rolename
            }
            val chain2 = buildMessageChain {
                +"你的身份是"
                +rolename
            }
            try {
                val objnumber = fromEvent.sender.bot.getGroup(groupId as Long)?.getMember(it.id)
                if(CommandConfig.SendImgLevel==0){
                    objnumber?.sendMessage(chain2)
                }
                else if(CommandConfig.SendImgLevel==2){
                    objnumber?.sendMessage(chain1)
                }
                else {
                    if (objnumber?.isFriend == true) {
                        objnumber.sendMessage(chain1)
                    }
                    else{
                        objnumber?.sendMessage(chain2)
                    }
                }

            } catch (t: Throwable) {
                sendMessage("请允许群成员发起临时会话")
                sendMessage(t.toString())
            }
            list.removeAt(i)
            thisRoom.detail?.running = true
            delay(2000)
        }

//        开始
        val beginMsg = buildMessageChain {
            +Image(fromEvent.sender.uploadImage(File("data/img/beginImg.jpg").toExternalResource()).imageId)
            +"这是一个与世无争的小村庄，居住于此的人们勤劳而又简朴地生活着\n"
            +"然而风云突变，深夜里一个村民的离奇被害，扯去了所有被压抑着的平静\n"
            +"尸体前人声鼎沸，那个鲜红的爪印已经说明了一切\n"
            +"狼人，就在人们彼此中间\n"
            +"有人愤怒，有人迷茫，有人恐惧，有人沉默\n"
            +"有人宣称自己通晓巫术已经证明了的清白\n"
            +"有人直言自己是普通村民，堂堂陈词寻找着真凶\n"
            +"而有的人则，在角落瑟瑟发抖，只希望自己不要成为下一个死者\n"
            +"时间飞逝，夜幕降临，人们回到家中，祈祷黎明平安到来\n"
            +"然而，事与愿违\n"
            +"血腥的屠杀还在不断继续，一个又一个无辜的村民倒在了血泊中\n"
            +"死亡的阴影笼罩在所有人的头顶徘徊，不愿散去\n"
            +"所有人都在寻找和自问\n"
            +"狼人，究竟是谁？\n"
        }
        sendMessage(beginMsg)
        thisRoom.begin()
    }
}