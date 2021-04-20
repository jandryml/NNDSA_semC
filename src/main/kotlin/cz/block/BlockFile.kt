package cz.block

import org.apache.commons.lang3.SerializationUtils
import java.io.RandomAccessFile
import java.io.Serializable

class BlockFile<T : Serializable> {
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

    public fun loadDataBlock(index: Int): DataBlock<T> {
        if (index < 1 || index > controlBlock.dataBlockCount) {
            throw ArrayIndexOutOfBoundsException("Invalid index, insert between 1 and ${controlBlock.dataBlockCount}")
        }
        return loadBlock(index) as DataBlock<T>
    }

    private fun loadBlock(index: Int): IBlock {
        RandomAccessFile(filename, "rw").use { stream ->
            stream.seek(getBlockOffsetByIndex(index))
            val size = stream.readInt()
            val buffer = ByteArray(size)
            stream.read(buffer, 0, size)
            return SerializationUtils.deserialize(buffer)
        }
    }

    public fun saveDataBlock(dataBlock: DataBlock<T>, index: Int) {
        if (index < 1 || index > controlBlock.dataBlockCount) {
            throw ArrayIndexOutOfBoundsException("Invalid index, insert between 1 and ${controlBlock.dataBlockCount}")
        }
        saveBlock(dataBlock, index, controlBlock.dataBlockSize)
    }

    private fun saveBlock(block: IBlock, index: Int, size: Int): IBlock {
        RandomAccessFile(filename, "rw").use { stream ->
            stream.seek(getBlockOffsetByIndex(index))
            stream.writeInt(size)
            stream.write(convertToByteArray(block, size), 0, size)
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
