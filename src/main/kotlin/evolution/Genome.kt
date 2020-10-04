package evolution

import Library
import constants.CardType

class Genome(val library: Library = Library()) {

    var fitnessResults: MutableList<Int> = mutableListOf()
    var fitnessAverage: Double = 0.0
    var landCount: Int = library.cards.count { c -> c.type == CardType.LAND }

    fun fitnessAverage(): Double {
        fitnessAverage = fitnessResults.toIntArray().average()
        return fitnessAverage
    }

    fun copy(): Genome {
        return Genome(Library(library.cards.toMutableList()))
    }
}