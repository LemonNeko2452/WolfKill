package work.anqi.data

import kotlinx.serialization.Serializable
import net.mamoe.mirai.contact.User

@Serializable
class Members {
    val id: Long
    val name: String
    val avatarUrl: String
    var role: Role

    constructor(user: User) {
        this.id = user.id
        this.name = user.nick
        this.avatarUrl = user.avatarUrl
        this.role = Role()
    }

    override fun toString(): String {
        return this.name+"("+this.id+")"
    }
}