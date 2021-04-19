package cz.model

class City(var name: String, var populationCount: Int, var posX: Double, var posY: Double) {
    override fun toString(): String {
        return "City(name='$name', population=$populationCount, posX=$posX, posY=$posY)"
    }
}