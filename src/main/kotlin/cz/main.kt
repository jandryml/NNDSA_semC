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
    val string = "asdf"
    val result = ByteArray(100)
    System.arraycopy(string.toByteArray(), 0, result, 0, string.length)
    println()
}

fun saveBLocks(blocks: List<Block>) {
    DataOutputStream(FileOutputStream("someFile.txt")).use { stream ->
        for (block in blocks) {
            val buffer: ByteArray = SerializationUtils.serialize(block)
            stream.writeInt(buffer.size)
            stream.write(buffer, 0, buffer.size)
        }
        stream.flush()
    }
}

fun loadBlocks(): List<Block> {
    DataInputStream(FileInputStream("someFile.txt")).use { stream ->
        val blocksList = ArrayList<Block>()
        while (stream.available() != 0) {
            val size = stream.readInt()
            val buffer = stream.readNBytes(size)
            blocksList.add(SerializationUtils.deserialize(buffer))
        }

        return blocksList
    }
}