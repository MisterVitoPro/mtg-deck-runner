package evolution

import Library
import constants.Color

class Genome(val color: Color = Color.GREEN,
             val library: Library = Library(color = color),
             val generation: Int,
             var name: String = "") {

    var fitness: Int = 0

    fun copy(): Genome {
        return Genome(color, Library(library.cards.toMutableList(), color), generation, name)
    }

    override fun toString(): String {
        return StringBuilder()
                .appendLine("==== [${this.name}] ====")
                .append("Fitness: ${this.fitness}")
                .appendLine(" -- Deck Size: ${this.library.cards.size}")
                .append(this.library.toString())
                .toString()
    }

    fun getNameFitnessString(): String {
        return "Name: $name - Fitness: $fitness"
    }
}