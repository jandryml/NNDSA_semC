package cz

import cz.data.City
import cz.service.HashFileService
import io.github.serpro69.kfaker.Faker
import org.junit.jupiter.api.*
import java.io.File
import java.time.Duration
import java.time.LocalDateTime
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class DemoTest {
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

    @Order(1)
    fun generateData(dataCount: Int, testCity: City) {
        val start = LocalDateTime.now()
        hashFile = HashFileService(file.name, 200, 1000, 10)

        val faker = Faker()
        for (i in 0 until dataCount) {
            println(i)
            hashFile.saveData(
                City(
                    faker.address.city(),
                    (0..100).random(),
                    getRandomCoordinate(),
                    getRandomCoordinate()
                )
            )
        }
        hashFile.saveData(testCity)

        // +1 for testCity
        assertEquals(dataCount + 1, hashFile.loadAllData().size)

        val duration: Duration = Duration.between(start, LocalDateTime.now())
        println("Overall time: ${duration.seconds}")
        hashFile.close()
    }

    @Test
    fun loadEntryTest() {
        val testCity = City("Pardubice", 53_000, 50.0, 42.0)
        generateData(10_000, testCity)

        hashFile = HashFileService(file.name, 0, 0, 0)

        val foundTestCity = hashFile.findByKey("Pardubice")

        println(foundTestCity)
        Assertions.assertEquals(testCity.name, foundTestCity.name)
        Assertions.assertEquals(testCity.populationCount, foundTestCity.populationCount)
        Assertions.assertEquals(testCity.posX, foundTestCity.posX)
        Assertions.assertEquals(testCity.posY, foundTestCity.posY)
    }

    private fun getRandomCoordinate(): Double {
        return Random.nextDouble(0.0, 1000.0)
    }
}