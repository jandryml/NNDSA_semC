package cz.block

import cz.data.IKeyable
import org.apache.commons.lang3.SerializationUtils
import java.io.RandomAccessFile
import java.io.Serializable

class BlockFile<K, T> where T : Serializable, T : IKeyable<K> {
    private val file: RandomAccessFile
    private lateinit var controlBlock: ControlBlock

    constructor(filename: String, keyMaxSize: Int, blockCount: Int, dataPerBlock: Int) {
        file = RandomAccessFile(filename, "rw")
        initNewBlockFile(keyMaxSize, blockCount, dataPerBlock)
    }

    constructor(filename: String) {
        file = RandomAccessFile(filename, "rw")
        this.controlBlock = loadControlBlock()
    }

    fun getDataBlockCount(): Int {
        return controlBlock.dataBlockCount
    }

    fun saveDataBlock(dataBlock: DataBlock<K, T>): DataBlock<K, T> {
        return saveBlock(dataBlock, dataBlock.index, controlBlock.dataBlockSize) as DataBlock<K, T>
    }

    fun loadDataBlock(index: Int): DataBlock<K, T> {
        return loadBlock(index) as DataBlock<K, T>
    }

    fun loadAllDataBlocks(): List<DataBlock<K, T>> {
        val dataBlockList = ArrayList<DataBlock<K, T>>()
        for (i in 1..controlBlock.dataBlockCount) {
            val actualDataBlock = loadDataBlock(i)
            dataBlockList.add(actualDataBlock)

            var substituteBlockIndex = actualDataBlock.substituteBlockIndex

            while (substituteBlockIndex != null) {
                val substituteBlock = loadDataBlock(substituteBlockIndex)
                dataBlockList.add(substituteBlock)
                substituteBlockIndex = substituteBlock.substituteBlockIndex
            }
        }
        return dataBlockList
    }

    fun loadSubstituteBlock(parentBlock: DataBlock<K, T>): DataBlock<K, T>{
        return if (parentBlock.substituteBlockIndex != null) {
            val substituteBlock = loadDataBlock(parentBlock.substituteBlockIndex!!)
            if (substituteBlock.isFull()) loadSubstituteBlock(substituteBlock) else substituteBlock
        } else {
            createSubstituteBlock(parentBlock)
        }
    }

    fun close() {
        file.close()
    }

    private fun saveControlBlock(controlBlock: ControlBlock): ControlBlock {
        return saveBlock(controlBlock, 0, controlBlock.controlBlockSize) as ControlBlock
    }

    private fun initNewBlockFile(keyMaxSize: Int, blockCount: Int, dataPerBlock: Int) {
        val dataBlockMaxSize = calculateDataBlockMaxSize(keyMaxSize, dataPerBlock)
        this.controlBlock = saveControlBlock(ControlBlock(dataBlockMaxSize, blockCount))

        for (i in 1..blockCount) {
            saveDataBlock(DataBlock(i, dataPerBlock, keyMaxSize, null))
        }
    }

    private fun createSubstituteBlock(parentBlock: DataBlock<K, T>): DataBlock<K, T> {
        parentBlock.substituteBlockIndex = controlBlock.nextFreeSubstituteBlockIndex++
        saveControlBlock(controlBlock)
        saveDataBlock(parentBlock)
        return DataBlock(parentBlock.substituteBlockIndex!!, parentBlock.dataPerBlock, parentBlock.keyMaxSize, null)
    }

    private fun calculateDataBlockMaxSize(keyMaxSize: Int, dataPerDataBlock: Int): Int {
        // 304 - empty data block size
        // 29 - byte size of data block element with empty key, keyMaxSize * 2 - special chars
        // TODO change calculation for generic types, now only works with City class -> dynamic calculation of size
        return 304 + (29 + keyMaxSize * 2) * dataPerDataBlock
    }

    private fun loadControlBlock(): ControlBlock {
        return loadBlock(0) as ControlBlock
    }

    private fun loadBlock(index: Int): IBlock {
        file.seek(getBlockOffsetByIndex(index))
        val size = file.readInt()
        val buffer = ByteArray(size)
        file.read(buffer, 0, size)
        return SerializationUtils.deserialize(buffer)
    }

    private fun saveBlock(block: IBlock, index: Int, size: Int): IBlock {
        file.seek(getBlockOffsetByIndex(index))
        // shorten bytes count due to Integer size information at beginning
        val updatedSize = size - 4
        file.writeInt(updatedSize)
        file.write(convertToByteArray(block, updatedSize), 0, updatedSize)
        return block
    }

    private fun convertToByteArray(block: IBlock, blockSize: Int): ByteArray {
        val blockBytes = SerializationUtils.serialize(block)
//        println(blockBytes.size)
        val buffer = ByteArray(blockSize)
        System.arraycopy(blockBytes, 0, buffer, 0, blockBytes.size)
        return buffer
    }

    private fun getBlockOffsetByIndex(index: Int): Long {
        return if (index == 0) {
            0L
        } else {
            (controlBlock.controlBlockSize + (index * controlBlock.dataBlockSize)).toLong()
        }
    }
}
