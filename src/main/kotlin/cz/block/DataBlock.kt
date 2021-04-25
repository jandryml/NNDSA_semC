package cz.block

import cz.data.IKeyable
import cz.exception.DataBlockFullException
import java.io.Serializable

class DataBlock<K, T>(
    private var dataPerDataBlock: Int,
    val keyMaxSize: Int,
    private var substituteBlockIndex: Int?
) : IBlock where T : Serializable, T : IKeyable<K> {
    private var dataList: MutableList<T> = ArrayList()

    fun addData(data: T) {
        if (dataList.size <= dataPerDataBlock) {
            dataList.add(data)
        } else {
            throw DataBlockFullException("Block is full!")
        }
    }

    fun removeData(key: K) {
        dataList.forEach {
            if (it.getKey() == key) {
                dataList.remove(it)
                return
            }
        }
    }

    fun getData(): List<T> {
        return dataList
    }

    fun setSubstituteBlockIndex(index: Int) {
        this.substituteBlockIndex = index
    }
}