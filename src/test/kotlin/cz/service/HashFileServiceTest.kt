package cz.service

import cz.block.BlockFile
import cz.block.DataBlock
import cz.data.City
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class HashFileServiceTest{

    @Test
    fun hashFileTest() {
        val hashFile = HashFileService<String, City>("hashTest.txt", 1000, 10, 10)


//        hashFile.saveData(City("Praha", 50000, 543.2, 984.545465))
//        hashFile.saveData(City("Pardubice", 500, 543.2, 984.545465))
//        hashFile.saveData(City("ščřščřščřščžščžščžščřěšč+čř+řč+ěšřěšřščžřřčžřčžřčžřčžčřž", 500, 543.2, 984.545465))

        hashFile.loadData("Praha")
        println()
    }
}