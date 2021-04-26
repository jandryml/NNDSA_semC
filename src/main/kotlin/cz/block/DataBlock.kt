package cz.block

import cz.data.IKeyable
import cz.exception.DataBlockFullException
import java.io.Serializable
import kotlin.jvm.Throws

class DataBlock<K, T>(
    val index: Int,
    val dataPerBlock: Int,
    val keyMaxSize: Int,
    var substituteBlockIndex: Int?
) : IBlock where T : Serializable, T : IKeyable<K> {
    private var dataList: MutableList<T> = ArrayList()

    @Throws(DataBlockFullException::class)
    fun addData(data: T) {
        if (!isFull()) {
            dataList.add(data)
        } else {
            throw DataBlockFullException("Block is full!")
        }
    }

    fun removeData(key: K): Boolean {
        dataList.forEach {
            if (it.getKey() == key) {
                return dataList.remove(it)
            }
        }
        return false
    }

    fun getData(): List<T> {
        return dataList
    }

    fun isFull(): Boolean {
        return dataList.size >= dataPerBlock
    }
}