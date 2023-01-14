package work.anqi

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.getMember
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.info
import work.anqi.command.*
import work.anqi.config.*

object WolfKill : KotlinPlugin(
    JvmPluginDescription(
        id = "work.anqi.WolfKill",
        name = "WolfKill",
        version = "0.1.0",
    ) {
        author("LemonNeko")
        info("""尝试用mirai实现狼人杀游戏""")
    }
) {
    private fun init() {

        WolfKillRoom.reload()
        logger.info { "狼人杀数据已加载" }
        CommandConfig.reload()
        logger.info { "狼人杀指令已加载" }
        GameStart.register()
        GameStop.register()
        GameRestart.register()
        JoinGame.register()
        Go.register()
        Kill.register()
        Look.register()
        Witch.register()
        Vote.register()
        Shoot.register()
        GameQuit.register()
        logger.info { "狼人杀命令已注册" }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onEnable() {
        init()
        GlobalScope.launch {
            while (true) {
                if(Bot.instances.size==0)continue
                delay(1000)
                //2. 狼人行动阶段
                //3. 预言家行动阶段
                //4. 女巫行动阶段
                //5. 白天
                try{
                    for (rooms in WolfKillRoom.rooms) {
                        val detail = rooms.detail
                        val bot = Bot.instances[0].bot
                        val group = bot.getGroup(rooms.room)
                        if (!rooms.game_over && rooms.detail?.running==true) {
                            if (rooms.wolfs.size == 0) {
                                group?.sendMessage("好人阵营胜利")
                                group?.sendMessage("胜利者:")
                                rooms.goods.forEach {
                                    group?.sendMessage(At(it.id))
                                }
                                rooms.game_over = true
                                break
                            }
                            if (rooms.goods.size == 0) {
                                group?.sendMessage("狼人阵营胜利")
                                group?.sendMessage("胜利者:")
                                rooms.wolfs.forEach {
                                    group?.sendMessage(At(it.id))
                                }
                                rooms.game_over = true
                                break
                            }
                        }
                        if (detail?.act == true) continue
                        var list_member = ""
                        for (i in 1..rooms.members.size) {
                            list_member += "" + i + " " + rooms.members[i - 1].toString() + "\n"
                        }
                        when (detail?.section) {
                            1 -> {
                                logger.info { "天黑" }
                                group?.sendMessage("天黑请闭眼")
                                detail.section += 1
                            }
                            2 -> {
                                logger.info { "狼人行动阶段" }
                                group?.sendMessage("狼人请睁眼")
                                if (detail.rounds == 1) {
                                    var wolfString = ""
                                    rooms.wolfs.forEach {
                                        wolfString += it.toString() + "\n"
                                    }
                                    val wolfMsg = buildMessageChain {
                                        +"狼人：\n"
                                        +wolfString
                                        +"\n请自行联系其他狼人"
                                    }
                                    rooms.wolfs.forEach {
                                        group?.getMember(it.id)?.sendMessage(wolfMsg)
                                    }
                                }
                                val wolfTip = buildMessageChain {
                                    +"可刀角色：\n"
                                    +list_member
                                    +"使用命令 【kill 编号】 即可\n"
                                    +"kill 0 表示不出刀"
                                }
                                rooms.wolfs.forEach {
                                    group?.getMember(it.id)?.sendMessage(wolfTip)
                                }
                                detail.act = true
                            }

                            3 -> {

                                logger.info { "预言家行动阶段" }
                                group?.sendMessage("预言家请睁眼")
                                var flag_skip = true
                                rooms.goods.forEach {
                                    if (it.role.id == 3) {
                                        flag_skip = false
                                        val yTip = buildMessageChain {
                                            +"可查角色：\n"
                                            +list_member
                                            +"使用命令 【look 编号】 即可\n"
                                        }
                                        group?.getMember(it.id)?.sendMessage(yTip)
                                    }
                                }
                                detail.act = true
                                delay(3000)
                                if(flag_skip){
                                    detail.section +=1
                                    detail.act = false
                                }
                            }

                            4 -> {
                                logger.info { "女巫行动阶段" }
                                group?.sendMessage("女巫请睁眼")
                                var flag_skip = true
                                rooms.goods.forEach {
                                    if (it.role.id == 5) {
                                        flag_skip = false
                                        if (rooms.will_dea != 0L && it.role.healing_potion > 0) {
                                            val nTip = buildMessageChain {
                                                +"今晚 "
                                                +rooms.will_dea_name
                                                +" 被杀\n"
                                                +"使用命令【witch save】拯救ta"
                                            }
                                            group?.getMember(it.id)?.sendMessage(nTip)
                                        }
                                        if (it.role.death_potion > 0) {
                                            val nTip = buildMessageChain {
                                                +"可毒角色：\n"
                                                +list_member
                                                +"使用命令【witch 编号】使用毒药或是使用指令【witch skip】跳过"
                                            }
                                            group?.getMember(it.id)?.sendMessage(nTip)
                                        }
                                    }
                                }
                                detail.act = true
                                delay(3000)
                                if(flag_skip){
                                    detail.section += 1
                                    detail.act = false
                                }
                            }

                            5 -> {
                                logger.info { "天亮" }
                                if (detail.rounds == 1) {
                                    var godString = ""
                                    rooms.goods.forEach {
                                        if (it.role.id == 4 || it.role.id == 6) {
                                            godString += it.toString() + "\n"
                                        }
                                    }
                                    val godMsg = buildMessageChain {
                                        +"你的队友（身份是猎人或白痴）：\n"
                                        +godString
                                    }
                                    rooms.goods.forEach {
                                        if (it.role.id == 4 || it.role.id == 6) {
                                            group?.getMember(it.id)?.sendMessage(godMsg)
                                        }
                                    }
                                }
                                group?.sendMessage("天亮了")
                                if (rooms.will_dea == 0L && rooms.witch_kill.size == 0) {
                                    group?.sendMessage("昨晚是个平安夜")
                                } else {
                                    rooms.witch_kill.add(rooms.will_dea)
                                    var list_dea = ""
                                    for (i in rooms.members) {
                                        if (i.id in rooms.witch_kill) {
                                            list_dea += i.toString() + "\n"
                                        }
                                    }
                                    // 夜晚死亡
                                    val bTip = buildMessageChain {
                                        +"昨晚死亡角色：\n"
                                        +list_dea
                                    }
                                    group?.sendMessage(bTip)
                                    rooms.witch_kill.forEach { deaId ->
                                        rooms.kill(deaId)
                                    }
                                }
                                detail.section += 1
                            }

                            6,9-> {
                                logger.info { "猎人被杀时" }
                                rooms.goods.forEach {
                                    if(rooms.will_dea == it.id){
                                        if(it.role.id == 4){
                                            group?.sendMessage("猎人死亡，请等待10秒")
                                            val hTip = buildMessageChain {
                                                +At(it.id)
                                                +"请发动技能，射击一名角色：\n"
                                                +list_member
                                                +"使用命令 【shoot 编号】表示射击某人\n"
                                                +"shoot 0 表示不射击\n"
                                                +"你有10秒钟的时间来决定\n"
                                            }
                                            group?.getMember(it.id)?.sendMessage(hTip)
                                            delay(10000)
                                        }
                                    }
                                }
                                detail.section += 1
                                rooms.init_room()
                            }

                            7 -> {
                                logger.info { "发言阶段" }
                                val kTip = buildMessageChain {
                                    +"请按照以下顺序发言\n"
                                    +list_member
                                    +"使用命令 【vote 编号】表示投票给某人\n"
                                    +"vote 0 表示不投票\n"
                                }
                                group?.sendMessage(kTip)
                                detail.act = true
                            }

                            8 -> {
                                logger.info { "投票结束" }
                                var flag = false
                                rooms.goods.forEach {
                                    if (it.role.id == 6 && it.id == rooms.will_dea) {
                                        flag = true
                                    }
                                }
                                if (rooms.will_dea == 0L) {
                                    group?.sendMessage("没有人被投票，游戏继续")
                                } else if (flag) {
                                    group?.sendMessage(rooms.will_dea_name+"身份为白痴，无法投票出局")
                                } else {
                                    // 投票死亡
                                    rooms.kill(rooms.will_dea)
                                    val zTip = buildMessageChain {
                                        +rooms.will_dea_name
                                        +" 出局，你可以留下遗言(30秒时间)"
                                    }
                                    group?.sendMessage(zTip)
                                    delay(30000)
                                }
                                detail.section += 1
                            }

                            10 -> {
                                logger.info { "回合结束" }
                                rooms.init_room()
                                detail.rounds += 1
                                detail.section = 1
                            }
                        }
                    }
                }catch (t: Throwable){
                    logger.info { t.toString() }
                }
                WolfKillRoom.rooms.removeIf { it.game_over }
            }
        }
        logger.info { "狼人杀插件加载完毕" }
    }

    override fun onDisable() {
        WolfKillRoom.save()
        CommandConfig.save()
        // 注销命令
        GameStart.unregister()
        GameStop.unregister()
        GameRestart.unregister()
        JoinGame.unregister()
        Go.unregister()
        Kill.unregister()
        Look.unregister()
        Witch.unregister()
        Vote.unregister()
        Shoot.unregister()
        GameQuit.unregister()
        logger.info("狼人杀插件已卸载")
    }
}


