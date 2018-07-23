package evolution

import Library

class Genome(val library: Library = Library()) {

    var fitnessResults: MutableList<Int> = mutableListOf()

    fun fitnessAverage(): Double {
        return fitnessResults.toIntArray().average()
    }

    fun copy(): Genome{
        return Genome(Library(library.cards.toMutableList()))
    }
}