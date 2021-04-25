package cz.data

import java.io.Serializable

class City(
    var name: String,
    var populationCount: Int,
    var posX: Double,
    var posY: Double
) : Serializable, IKeyable<String> {
    override fun toString(): String {
        return "City(name='$name', population=$populationCount, posX=$posX, posY=$posY)"
    }

    override fun getKey(): String {
        return name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as City

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun getKeySize(): Int {
        return name.length
    }
}