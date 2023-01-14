package work.anqi.data


import kotlinx.serialization.Serializable
import net.mamoe.mirai.contact.User
import java.util.*

@Serializable
class ROOMS {
    val room: Long
    var detail: Detail? = null
    var members: MutableList<Members> = mutableListOf<Members>()
    var roles: MutableList<Int> = mutableListOf<Int>()
    var wolfs: MutableList<Members> = mutableListOf<Members>()
    var goods: MutableList<Members> = mutableListOf<Members>()
    var will_dea = 0L
    var will_dea_name = ""
    var witch_kill: MutableList<Long> = mutableListOf<Long>()
    var game_over = false
    val timestamp = Date().time


    constructor(thisgroup: Long, a: User) {
        this.room = thisgroup
        this.members.add(Members(a))

        this.detail = Detail(8, 0)
        this.roles = mutableListOf(1, 1, 1, 2, 2, 3, 4, 5)
    }

    constructor(thisgroup: Long) {
        this.room = thisgroup
    }

    fun build(s: String): Int {
        val list = s.split(" ", ",","，")
        if (list.size != 6) {
            return 1
        }
        var n = 0
        val list2: MutableList<Int> = mutableListOf<Int>()
        for (i in 1 .. 6) {
            for (j in 1..list[i-1].toInt()) {
                list2.add(i)
                n++
            }
        }
        if (n > 18 || n < 4) {
            return 2
        }
        if (list2[0] > n / 2) {
            return 3
        }
        this.detail = Detail(n, 1)
        this.roles = list2
        return 4
    }

    fun willDie() {
        val x = this.wolfs.groupBy { it.role.kill_target }
        var max = 0
        for ((k, v) in x) {
            if (v.size > max) {
                max = v.size
                if (k > 0) {
                    this.will_dea = this.members[k - 1].id
                    this.will_dea_name = this.members[k - 1].name
                }
            }
        }
    }

    fun voteOut() {
        val x = this.members.groupBy { it.role.vote_target }
        var max = 0
        for ((k, v) in x) {
            if (v.size > max) {
                max = v.size
                if (k > 0) {
                    this.will_dea = this.members[k - 1].id
                    this.will_dea_name = this.members[k - 1].name
                }
            }
        }
    }

    fun begin() {
        this.detail?.section = 1
    }

    fun init_room() {
        this.witch_kill.clear()
        this.will_dea = 0L
        this.will_dea_name = ""
        this.wolfs.forEach {
            it.role.flag_action = false
            it.role.kill_target = 0
        }
        this.goods.forEach {
            it.role.flag_action = false
        }
        this.members.forEach {
            it.role.vote_target = 0
            it.role.flag_voted = false
        }
    }

    fun kill(id: Long) {
        this.goods.removeIf { it.id == id }
        this.wolfs.removeIf { it.id == id }
        this.members.removeIf { it.id == id }
    }
}