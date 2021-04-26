package cz.block

import cz.data.City
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertTrue

internal class BlockFileTest {
    val file = File("test.txt")

    @BeforeEach
    fun setUp() {
        if (file.exists()) {
            assertTrue(file.delete())
        }
    }

    @Test
    fun dataBlockOverflowTest() {
        val dataBlock = DataBlock<String, City>(10, 50, null)


    }


    @Test
    fun test() {
        val fileBoi = File("logFile - Copy")

        val valueArray = IntArray(201) { 0 }

        fileBoi.readLines().forEach {
            valueArray[it.toInt()]++
        }

        for(index in valueArray.indices) {
            println("index $index : ${valueArray[index]}")
        }
    }


    @Test
    fun blockFileTest() {
        val blockFile = BlockFile<String, City>(file.name, 1000, 10, 10)

        val dataBlock = DataBlock<String, City>(10, 50, null)

        dataBlock.addData(City("Praha", 50000, 543.2, 984.545465))
        dataBlock.addData(City("Kutná hora", 500, 543.2, 984.545465))
        dataBlock.addData(City("Kolín", 500, 543.2, 984.545465))
        dataBlock.addData(City("Jihlava", 500, 543.2, 984.545465))
        dataBlock.addData(City("Pardubice", 500, 543.2, 984.545465))
        dataBlock.addData(City("Brno", 500, 543.2, 984.545465))
        dataBlock.addData(City("ščřščřščřščžščžščžščřěščččřčřččěšřěšřščžřřčžřčžřčž", 500, 543.2, 984.545465))
        dataBlock.addData(City("ščřščřščřščžščžščžščřěščččřčřččěšřěšřščžřřčžřčžřčž", 500, 543.2, 984.545465))
        dataBlock.addData(City("ščřščřščřščžščžščžščřěščččřčřččěšřěšřščžřřčžřčžřčž", 500, 543.2, 984.545465))
        dataBlock.addData(City("ščřščřščřščžščžščžščřěščččřčřččěšřěšřščžřřčžřčžřčž", 500, 543.2, 984.545465))

        blockFile.saveDataBlock(dataBlock, 7)
        blockFile.loadDataBlock(7)


        print(blockFile)
    }
}