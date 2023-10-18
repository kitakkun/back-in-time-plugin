import com.github.kitakkun.back_in_time.annotations.DebuggableStateHolder

@Suppress("unused")
@DebuggableStateHolder
class HogeViewModel {
    var hoge = 10
    var fuga = mutableListOf("")

    fun hogeSet() {
        hoge += 10
    }
}

fun main() {
    val hoge = HogeViewModel()
    hoge.hoge = 10
    hoge.fuga = mutableListOf("hoge")
    hoge.hogeSet()
}

