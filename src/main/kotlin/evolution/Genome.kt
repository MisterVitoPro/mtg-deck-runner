package evolution

import Library
import constants.CardType

class Genome(val library: Library = Library()) {

    var name: String = ""
    var generation: Int = 0
    var fitness: Int = 0

    fun copy(): Genome {
        return Genome(Library(library.cards.toMutableList()))
    }

    fun print(){
        println("Name: $name - Fitness: $fitness")
    }
}