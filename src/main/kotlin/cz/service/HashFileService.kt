package cz.service

import cz.block.BlockFile
import cz.block.DataBlock
import cz.data.IKeyable
import cz.exception.DataNotFound
import java.io.File
import java.io.Serializable

class HashFileService<K, T>(
    fileName: String,
    dataBlockSize: Int,
    dataBlockCount: Int,
    dataPerDataBlock: Int
) where T : IKeyable<K>, T : Serializable {
    private val blockFile: BlockFile<T>

    init {
        blockFile = if (File(fileName).exists()) {
            BlockFile(fileName)
        } else {
            BlockFile(fileName, dataBlockSize, dataBlockCount, dataPerDataBlock)
        }
    }


    fun saveData(data: T) {
        val dataBlock = loadDataBlock(data.getKey())
        dataBlock.addData(data)
        blockFile.saveDataBlock(dataBlock, magicHash(data.getKey()))
    }

    fun loadData(key: K): T {
        val dataBlock = loadDataBlock(key)

        dataBlock.getData().forEach {
            if (it.getKey() == key) {
                return it
            }
        }
        throw DataNotFound("Data for key '$key' not found!")
    }

    private fun loadDataBlock(key: K): DataBlock<T> {
        val index = magicHash(key)
        return blockFile.loadDataBlock(index)
    }


    private fun magicHash(key: K): Int {
        var hash = key.hashCode()
        if (hash < 0) {
            hash *= -1
        }
        hash += 1

        return hash % blockFile.getDataBlockCount()
    }
}