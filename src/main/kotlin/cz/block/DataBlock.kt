package cz.block

import java.io.Serializable

class DataBlock<T : Serializable>(
    private var dataPerDataBlock: Int,
    private var substituteBlockIndex: Int?
) : IBlock {
    private var dataList: MutableList<T> = ArrayList()

    fun addData(data: T) {
        if (dataList.size <= dataPerDataBlock) {
            dataList.add(data)
        } else {
            throw IndexOutOfBoundsException("Block is full!")
        }
    }

    fun removeData(data: T) {
        dataList.forEach {
            if (it == data) {
                dataList.remove(it)
            }
        }
    }

    fun getData(): List<T> {
        return dataList
    }

    fun isDataInsertable(): Boolean {
        return dataList.size < dataPerDataBlock
    }

    fun setSubstituteBlockIndex(index: Int) {
        this.substituteBlockIndex = index
    }
}