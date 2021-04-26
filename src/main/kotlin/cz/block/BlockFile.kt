package cz.block

import cz.data.IKeyable
import cz.exception.DataBlockFullException
import org.apache.commons.lang3.SerializationUtils
import java.io.RandomAccessFile
import java.io.Serializable

class BlockFile<K, T> where T : Serializable, T : IKeyable<K> {
    private var filename: String
    private lateinit var controlBlock: ControlBlock
    private val controlBlockSize = 500

    constructor(fileName: String, keyMaxSize: Int, dataBlockCount: Int, dataPerDataBlock: Int) {
        this.filename = fileName
        initNewBlockFile(keyMaxSize, dataBlockCount, dataPerDataBlock)
    }

    constructor(fileName: String) {
        this.filename = fileName
        this.controlBlock = loadControlBlock()
    }

    fun getDataBlockCount(): Int {
        return controlBlock.dataBlockCount
    }

    fun loadAllDataBlocks(): List<DataBlock<K, T>> {
        val dataBlockList = ArrayList<DataBlock<K, T>>()
        for (i in 1..controlBlock.dataBlockCount) {
            val actualDataBlock = loadDataBlock(i)
            dataBlockList.add(actualDataBlock)

            var substituteBlockIndex = actualDataBlock.substituteBlockIndex

            while (substituteBlockIndex != null) {
                val substituteBlock = loadDataBlock(substituteBlockIndex, true)
                dataBlockList.add(substituteBlock)
                substituteBlockIndex = substituteBlock.substituteBlockIndex
            }
        }
        return dataBlockList
    }

    fun loadDataBlock(index: Int, isSubstitute: Boolean = false): DataBlock<K, T> {
        if (!isSubstitute) validateIndex(index)
        return loadBlock(index) as DataBlock<K, T>
    }

    fun saveControlBlock(controlBlock: ControlBlock): ControlBlock {
        return saveBlock(controlBlock, 0, controlBlockSize) as ControlBlock
    }

    fun saveDataBlock(dataBlock: DataBlock<K, T>, index: Int, isSubstitute: Boolean = false): DataBlock<K, T> {
        if (!isSubstitute) validateIndex(index)
        return saveBlock(dataBlock, index, controlBlock.dataBlockMaxSize) as DataBlock<K, T>
    }

    fun saveToSubstituteBlock(data: T, dataBlock: DataBlock<K, T>, actualIndex: Int) {
        val substituteBlockIndex = dataBlock.substituteBlockIndex

        if (substituteBlockIndex != null) {
            val substituteBlock = loadDataBlock(substituteBlockIndex, true)
            try {
                substituteBlock.addData(data)
                saveDataBlock(substituteBlock, substituteBlockIndex, true)
            } catch (e: DataBlockFullException) {
                saveToSubstituteBlock(data, substituteBlock, substituteBlockIndex)
            }
        } else {
            dataBlock.substituteBlockIndex = controlBlock.nextFreeSubstituteBlockIndex
            saveDataBlock(dataBlock, actualIndex, true)

            val newSubstituteBlock = DataBlock<K, T>(dataBlock.dataPerDataBlock, dataBlock.keyMaxSize, null)
            newSubstituteBlock.addData(data)
            saveDataBlock(newSubstituteBlock, controlBlock.nextFreeSubstituteBlockIndex++, true)

            saveControlBlock(controlBlock)
        }
    }

    private fun initNewBlockFile(keyMaxSize: Int, dataBlockCount: Int, dataPerDataBlock: Int) {
        val dataBlockMaxSize = calculateDataBlockMaxSize(keyMaxSize, dataPerDataBlock)
        this.controlBlock =
            saveControlBlock(ControlBlock(dataBlockMaxSize, dataBlockCount))

        for (i in 1..dataBlockCount) {
            saveDataBlock(DataBlock(dataPerDataBlock, keyMaxSize, null), i)
        }
    }

    private fun calculateDataBlockMaxSize(keyMaxSize: Int, dataPerDataBlock: Int): Int {
        // 296 - empty data block size
        // 29 - byte size of data block element with empty key, keyMaxSize * 2 - special chars
        return 296 + (29 + keyMaxSize * 2) * dataPerDataBlock
    }

    private fun loadControlBlock(): ControlBlock {
        return loadBlock(0) as ControlBlock
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

    private fun saveBlock(block: IBlock, index: Int, size: Int): IBlock {
        RandomAccessFile(filename, "rw").use { stream ->
            stream.seek(getBlockOffsetByIndex(index))
            // shorten bytes count due to Integer size at beginning
            val updatedSize = size - 4
            stream.writeInt(updatedSize)
            stream.write(convertToByteArray(block, updatedSize), 0, updatedSize)
            stream.close()
            return block
        }
    }

    private fun convertToByteArray(block: IBlock, blockSize: Int): ByteArray {
        val blockBytes = SerializationUtils.serialize(block)
//        println(blockBytes.size)
        val buffer = ByteArray(blockSize)
        System.arraycopy(blockBytes, 0, buffer, 0, blockBytes.size)
        return buffer
    }

    private fun validateIndex(index: Int) {
        if (index < 1 || index > controlBlock.dataBlockCount) {
            throw ArrayIndexOutOfBoundsException("Invalid index $index, insert between 1 and ${controlBlock.dataBlockCount}")
        }
    }

    private fun getBlockOffsetByIndex(index: Int): Long {
        return if (index == 0) {
            0L
        } else {
            (controlBlockSize + (index * controlBlock.dataBlockMaxSize)).toLong()
        }
    }
}
