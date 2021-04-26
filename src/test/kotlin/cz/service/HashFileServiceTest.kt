package cz.service

import cz.data.City
import cz.exception.DataKeyTooLongException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertTrue

internal class HashFileServiceTest {
    private val file = File("hashTest.txt")
    private lateinit var hashFile: HashFileService<String, City>

    @BeforeEach
    fun setUp() {
        if (file.exists()) {
            assertTrue(file.delete())
        }
    }

    @AfterEach
    fun cleanUp() {
        hashFile.close()
    }

    @Test
    fun searchTest() {
        hashFile = HashFileService(file.name, 50, 10, 10)

        val city1 = City("Praha", 50000, 543.2, 984.545465)
        val city2 =City("ščřščřščřščžščžščžščřěščččřčřččěšřěšřščžřřčžřčžřčž", 500, 543.2, 984.545465)

        hashFile.saveData(city1)
        hashFile.saveData(City("", 500, 543.2, 984.545465))
        hashFile.saveData(city2)

        val fetchedCities1 = hashFile.findByKey("Praha")
        val fetchedCities2 = hashFile.findByKey("ščřščřščřščžščžščžščřěščččřčřččěšřěšřščžřřčžřčžřčž")

        assertEquals(city1.name, fetchedCities1.name)
        assertEquals(city1.populationCount, fetchedCities1.populationCount)
        assertEquals(city1.posX, fetchedCities1.posX)
        assertEquals(city1.posY, fetchedCities1.posY)

        assertEquals(city2.name, fetchedCities2.name)
        assertEquals(city2.populationCount, fetchedCities2.populationCount)
        assertEquals(city2.posX, fetchedCities2.posX)
        assertEquals(city2.posY, fetchedCities2.posY)
    }

    @Test
    fun insertTest() {
        hashFile = HashFileService(file.name, 50, 10, 10)

        hashFile.saveData(City("Praha", 50000, 543.2, 984.545465))
        hashFile.saveData(City("", 500, 543.2, 984.545465))
        hashFile.saveData(City("ščřščřščřščžščžščžščřěščččřčřččěšřěšřščžřřčžřčžřčž", 500, 543.2, 984.545465))

        val fetchedCities = hashFile.loadAllData()

        assertEquals(3, fetchedCities.size)
    }

    @Test
    fun removeTest() {
        hashFile = HashFileService(file.name, 50, 10, 10)

        hashFile.saveData(City("Praha", 50000, 543.2, 984.545465))
        hashFile.saveData(City("", 500, 543.2, 984.545465))
        hashFile.saveData(City("ščřščřščřščžščžščžščřěščččřčřččěšřěšřščžřřčžřčžřčž", 500, 543.2, 984.545465))

        hashFile.removeData("ščřščřščřščžščžščžščřěščččřčřččěšřěšřščžřřčžřčžřčž")

        val fetchedCities = hashFile.loadAllData()

        assertEquals(2, fetchedCities.size)
    }

    @Test
    fun maxDataBlockSizeTest() {
        val maxDataPerBlock = 20

        hashFile = HashFileService(file.name, 100, 10, maxDataPerBlock)

        for (i in 0 until maxDataPerBlock) {
            hashFile.saveData(City("ščřščřščřščžščžščžščřěščččřčřččěšřěšřščžřřčžřčžřčžščřščřščřščžščžščžščřěščččřčřččěšřěšřščžřřčžřčžřčž", 500, 543.2, 984.545465))
        }

        val fetchedCities = hashFile.loadAllData()

        assertEquals(20, fetchedCities.size)
    }

    @Test
    fun tooLongDataKeyTest() {
        assertThrows(DataKeyTooLongException::class.java) {
            hashFile = HashFileService(file.name, 50, 10, 10)
            val city2 = City("ščřščřščřščžščžščžščřěščččřčřččěšřěšřščžřřčžřčžřčža", 500, 543.2, 984.545465)
            hashFile.saveData(city2)
        }
    }

    @Test
    fun substituteBlocktest() {
        hashFile = HashFileService(file.name, 50, 10, 1)
        val city2 = City("test", 500, 543.2, 984.545465)
        hashFile.saveData(city2)
        hashFile.findByKey("test")
        hashFile.saveData(city2)
        hashFile.findByKey("test")
        hashFile.saveData(city2)
        hashFile.findByKey("test")

        val fetchedCities = hashFile.loadAllData()

        assertEquals(3, fetchedCities.size)
    }
}