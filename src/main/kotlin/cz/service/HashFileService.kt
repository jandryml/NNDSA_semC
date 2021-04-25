package cz.service

import cz.block.BlockFile
import cz.block.DataBlock
import cz.data.IKeyable
import cz.exception.DataNotFoundException
import java.io.File
import java.io.Serializable

class HashFileService<K, T>(
    fileName: String,
    dataBlockSize: Int,
    dataBlockCount: Int,
    dataPerDataBlock: Int
) where T : IKeyable<K>, T : Serializable {
    private val blockFile: BlockFile<K, T>

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

    fun findByKey(key: K): T {
        val dataBlock = loadDataBlock(key)

        dataBlock.getData().forEach {
            if (it.getKey() == key) {
                return it
            }
        }
        throw DataNotFoundException("Data for key '$key' not found!")
    }

    fun removeData(key: K): T {
        val dataBlock: DataBlock<K, T>
        val data: T
        try {
            data = findByKey(key)
            dataBlock = loadDataBlock(key)
        } catch (e: DataNotFoundException) {
            throw DataNotFoundException("Data with key '$key' cannot be deleted, not found!")
        }
        dataBlock.removeData(key)
        blockFile.saveDataBlock(dataBlock, magicHash(key))
        return data
    }

    fun loadAllData(): List<T> {
        return blockFile.loadAllDataBlocks().flatMap { it.getData() }
    }

    private fun loadDataBlock(key: K): DataBlock<K, T> {
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