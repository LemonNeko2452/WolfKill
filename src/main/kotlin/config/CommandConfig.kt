package work.anqi.config

import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object CommandConfig : ReadOnlyPluginConfig("Command") {

    @ValueDescription("game-start命令的别名")
    val GameStart by value(arrayOf("创建狼人杀", "开始狼人杀"))

    @ValueDescription("game-stop命令的别名")
    val GameStop by value(arrayOf("游戏暂停", "暂停狼人杀"))

    @ValueDescription("game-restart命令的别名")
    val GameRestart by value(arrayOf("重新创建狼人杀", "重新开始狼人杀"))

    @ValueDescription("join-game命令的别名")
    val JoinGame by value(arrayOf("加入狼人杀", "加入游戏"))

    @ValueDescription("Go命令的别名")
    val Go by value(arrayOf("Go","GO","开"))

    @ValueDescription("Kill命令的别名")
    val Kill by value(arrayOf("刀"))

    @ValueDescription("Look命令的别名")
    val Look by value(arrayOf("查"))

    @ValueDescription("Witch命令的别名")
    val Witch by value(arrayOf("毒"))

    @ValueDescription("Vote命令的别名")
    val Vote by value(arrayOf("投","票"))

    @ValueDescription("Shoot命令的别名")
    val Shoot by value(arrayOf("射"))
}