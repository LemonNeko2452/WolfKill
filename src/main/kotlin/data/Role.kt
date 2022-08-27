package work.anqi.data


import kotlinx.serialization.Serializable

//1：狼人
//2：村民
//3：预言家
//4：猎人
//5：女巫
//6：白痴

@Serializable
class Role {
    var id: Int
    var name: String
    var flag_action = false
    var kill_target = 0
    var vote_target = 0
    var death_potion = 1
    var healing_potion = 1
    var flag_voted = false

    constructor() {
        this.id = 0
        this.name = "N0ll"
    }

    constructor(n: Int) {
        this.id = n
        this.name = "N0ll"
        when (n) {
            1 ->
                this.name = "狼人"

            2 ->
                this.name = "村民"

            3 ->
                this.name = "预言家"

            4 ->
                this.name = "猎人"

            5 ->
                this.name = "女巫"

            6 ->
                this.name = "白痴"
        }
    }

    fun kill(n : Int){
        this.flag_action = true
        this.kill_target = n
    }
    fun vote(n : Int){
        this.flag_voted = true
        this.vote_target = n
    }
}