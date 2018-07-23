package evolution

import Library

class Genome(val library: Library = Library()) {

    var fitnessResults: MutableList<Int> = mutableListOf()
    var fitnessAverage: Double = 0.0

    fun fitnessAverage(): Double {
        fitnessAverage = fitnessResults.toIntArray().average()
        return fitnessAverage
    }

    fun copy(): Genome {
        return Genome(Library(library.cards.toMutableList()))
    }
}