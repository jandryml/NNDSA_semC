package cz

import org.apache.commons.lang3.SerializationUtils
import java.io.*


//}


//fun main() = Window {
//    var text by remember { mutableStateOf("Hello, World!") }
//
//    MaterialTheme {
//        Button(onClick = {
//            text = "Hello, Desktop!"
//        }) {
//            Text(text)
//        }
//    }
data class Block(var name: String, var count: Int) : Serializable {
    override fun toString(): String {
        return "Block(name='$name', count=$count)"
    }
}

fun main() {
//    saveBLocks(
//        listOf(
//            Block("a", 42),
//            Block("á", 666),
//            Block("aa", 420)
//        )
//    )
//    loadBlocks().forEach { println(it) }

}

