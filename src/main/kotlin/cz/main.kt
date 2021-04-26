package cz

import cz.data.City
import cz.service.HashFileService
import io.github.serpro69.kfaker.Faker
import java.io.File
import java.time.Duration
import java.time.LocalDateTime
import kotlin.random.Random

fun main() {
    val file = File("dataFile.bin")

    val hashFile = HashFileService<String, City>(file.name, 200, 1000, 10)

    val start = LocalDateTime.now()
    val faker = Faker()
    for (i in 0..10_000) {
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

    val duration: Duration = Duration.between(start, LocalDateTime.now())

    println("Celkovy cas: ${duration.seconds}")

}

private fun getRandomCoordinate(): Double {
    return Random.nextDouble(0.0, 1000.0)
}

