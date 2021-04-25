package cz.block

import cz.data.IKeyable
import org.apache.commons.lang3.SerializationUtils
import java.io.RandomAccessFile
import java.io.Serializable

class BlockFile<K, T> where T : Serializable, T : IKeyable<K> {
    private var filename: String
    private lateinit var controlBlock: ControlBlock
    private val controlBlockSize = 500

    constructor(fileName: String, dataBlockSize: Int, dataBlockCount: Int, dataPerDataBlock: Int) {
        this.filename = fileName
        initNewBlockFile(dataBlockSize, dataBlockCount, dataPerDataBlock)
    }

    constructor(fileName: String) {
        this.filename = fileName
        this.controlBlock = loadControlBlock()
    }

    public fun getDataBlockCount(): Int {
        return controlBlock.dataBlockCount
    }

    private fun initNewBlockFile(dataBlockSize: Int, dataBlockCount: Int, dataPerDataBlock: Int) {
        this.controlBlock =
            saveBlock(ControlBlock(dataBlockSize, dataBlockCount), 0, controlBlockSize) as ControlBlock

        for (i in 1..dataBlockCount) {
            saveDataBlock(DataBlock(dataPerDataBlock, null), i)
        }
    }

    private fun loadControlBlock(): ControlBlock {
        return loadBlock(0) as ControlBlock
    }

    public fun loadAllDataBlocks(): List<DataBlock<K, T>> {
        val dataBlockList = ArrayList<DataBlock<K, T>>()
        for (i in 1..controlBlock.dataBlockCount) {
            dataBlockList.add(loadDataBlock(i))
        }
        return dataBlockList
    }

    public fun loadDataBlock(index: Int): DataBlock<K, T> {
        if (index < 1 || index > controlBlock.dataBlockCount) {
            throw ArrayIndexOutOfBoundsException("Invalid index $index, insert between 1 and ${controlBlock.dataBlockCount}")
        }
        return loadBlock(index) as DataBlock<K, T>
    }

    private fun loadBlock(index: Int): IBlock {
        RandomAccessFile(filename, "rw").use { stream ->
            stream.seek(getBlockOffsetByIndex(index))
            val size = stream.readInt()
            val buffer = ByteArray(size)
            stream.read(buffer, 0, size)
            stream.close()
            return SerializationUtils.deserialize(buffer)
        }
    }

    public fun saveDataBlock(dataBlock: DataBlock<K, T>, index: Int) {
        if (index < 1 || index > controlBlock.dataBlockCount) {
            throw ArrayIndexOutOfBoundsException("Invalid index $index, insert between 1 and ${controlBlock.dataBlockCount}")
        }
        saveBlock(dataBlock, index, controlBlock.dataBlockSize)
    }

    private fun saveBlock(block: IBlock, index: Int, size: Int): IBlock {
        RandomAccessFile(filename, "rw").use { stream ->
            stream.seek(getBlockOffsetByIndex(index))
            // shorten bytes count due to Integer size at beginning
            val updatedSize = size-4
            stream.writeInt(updatedSize)
            stream.write(convertToByteArray(block, updatedSize), 0, updatedSize)
            stream.close()
            return block
        }
    }

    private fun convertToByteArray(block: IBlock, blockSize: Int): ByteArray {
        val blockBytes = SerializationUtils.serialize(block)
        val buffer = ByteArray(blockSize)
        System.arraycopy(blockBytes, 0, buffer, 0, blockBytes.size)
        return buffer
    }

    private fun getBlockOffsetByIndex(index: Int): Long {
        return if (index == 0) {
            0L
        } else {
            (controlBlockSize + (index * controlBlock.dataBlockSize)).toLong()
        }
    }
}
