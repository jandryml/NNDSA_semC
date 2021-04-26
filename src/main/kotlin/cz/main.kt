package cz

import cz.data.City
import cz.service.HashFileService
import io.github.serpro69.kfaker.Faker
import java.io.File
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random

fun main() {
    val file = File("dataFile.bin")

    val hashFile = HashFileService<String, City>(file.name, 200, 200, 75)

    println(LocalDateTime.now())
    val faker = Faker()
    for (i in 0..10_000_000) {
        hashFile.saveData(
            City(
                faker.address.city(),
                (0..10_000_000).random(),
                getRandomCoordinate(),
                getRandomCoordinate()
            )
        )
    }
    println(LocalDateTime.now())

}

private fun getRandomCoordinate(): Double {
    return Random.nextDouble(0.0, 1000.0)
}

