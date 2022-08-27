package work.anqi

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value
import work.anqi.data.ROOMS

object WolfKillRoom : AutoSavePluginData("WolfKillRoom") {
    val rooms: MutableList<ROOMS> by value()
}