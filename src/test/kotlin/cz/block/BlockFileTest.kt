package cz.block

import cz.data.City
import org.junit.jupiter.api.Test

internal class BlockFileTest {

    @Test
    fun blockFileTest() {
        val blockFile = BlockFile<City>("test.txt", 1000, 10, 10)

        val dataBlock = DataBlock<City>(10, null)

        dataBlock.addData(City("Praha", 50000, 543.2, 984.545465))
        dataBlock.addData(City("Kutná hora", 500, 543.2, 984.545465))
        dataBlock.addData(City("Kolín", 500, 543.2, 984.545465))
        dataBlock.addData(City("Jihlava", 500, 543.2, 984.545465))
        dataBlock.addData(City("Pardubice", 500, 543.2, 984.545465))
        dataBlock.addData(City("Brno", 500, 543.2, 984.545465))
        dataBlock.addData(City("ščřščřščřščžščžščžščřěšč+čř+řč+ěšřěšřščžřřčžřčžřčžřčžčřž", 500, 543.2, 984.545465))
        dataBlock.addData(City("ščřščřščřščžščžščžščřěšč+čř+řč+ěšřěšřščžřřčžřčžřčžřčžčřž", 500, 543.2, 984.545465))
        dataBlock.addData(City("ščřščřščřščžščžščžščřěšč+čř+řč+ěšřěšřščžřřčžřčžřčžřčžčřž", 500, 543.2, 984.545465))
        dataBlock.addData(City("ščřščřščřščžščžščžščřěšč+čř+řč+ěšřěšřščžřřčžřčžřčžřčžčřž", 500, 543.2, 984.545465))

        blockFile.saveDataBlock(dataBlock, 7)
        blockFile.loadDataBlock(7)


        print(blockFile)

    }
}