package work.anqi.data

import kotlinx.serialization.Serializable

//0. 未开始阶段
//1. 天黑
//2. 狼人行动阶段
//3. 预言家行动阶段
//4. 女巫行动阶段
//5. 白天
@Serializable
class Detail {
    var total: Int = 0
    var join: Int = 0
    var rounds:Int = 1
    var running:Boolean = false
    var section = 0
    var act:Boolean = false
    constructor(total: Int, join: Int) {
        this.total = total
        this.join = join
    }
}