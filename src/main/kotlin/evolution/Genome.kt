package evolution

import Library
import constants.Color

class Genome(val library: Library = Library(), val color: Color = Color.RED) {

    var name: String = ""
    var fitness: Int = 0

    fun copy(): Genome {
        return Genome(Library(library.cards.toMutableList()))
    }

    override fun toString(): String {
        return StringBuilder()
                .append(this.name)
                .append(" -- Fitness: ${this.fitness}")
                .appendLine(" -- Deck Size: ${this.library.cards.size}")
                .appendLine(this.library.getPrintableLibrary())
                .toString()
    }

    fun print() {
        println("Name: $name - Fitness: $fitness")
    }
}