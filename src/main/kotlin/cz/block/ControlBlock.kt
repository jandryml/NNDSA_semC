package cz.block

class ControlBlock(
    val dataBlockMaxSize: Int,
    val dataBlockCount: Int,
    var nextFreeSubstituteBlockIndex: Int = dataBlockCount + 1
) : IBlock