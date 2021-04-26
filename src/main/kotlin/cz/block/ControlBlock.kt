package cz.block

class ControlBlock(
    val dataBlockSize: Int,
    val dataBlockCount: Int,
    val controlBlockSize:Int = 145,
    var nextFreeSubstituteBlockIndex: Int = dataBlockCount + 1
) : IBlock