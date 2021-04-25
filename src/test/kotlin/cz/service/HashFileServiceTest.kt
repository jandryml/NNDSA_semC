package cz.service

import cz.data.City
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class HashFileServiceTest{

    @Test
    fun hashFileTest() {
        val hashFile = HashFileService<String, City>("hashTest.txt", 1000, 10, 10)


        hashFile.saveData(City("Praha", 50000, 543.2, 984.545465))
        hashFile.saveData(City("Pardubice", 500, 543.2, 984.545465))
        hashFile.saveData(City("ščřščřščřščžščžščžščřěšč+čř+řč+ěšřěšřščžřřčžřčžřčžřčžčřž", 500, 543.2, 984.545465))

        val fetchedCities = hashFile.loadAllData()

        assertEquals(3, fetchedCities.size)
    }


    @Test
    fun dataDeleteTest() {
        val hashFile = HashFileService<String, City>("hashTest.txt", 1000, 10, 10)


        hashFile.saveData(City("Praha", 50000, 543.2, 984.545465))
        hashFile.saveData(City("Pardubice", 500, 543.2, 984.545465))
        hashFile.saveData(City("ščřščřščřščžščžščžščřěšč+čř+řč+ěšřěšřščžřřčžřčžřčžřčžčřž", 500, 543.2, 984.545465))

        hashFile.removeData("ščřščřščřščžščžščžščřěšč+čř+řč+ěšřěšřščžřřčžřčžřčžřčžčřž")
        val fetchedCities = hashFile.loadAllData()

        assertEquals(2, fetchedCities.size)
    }
}