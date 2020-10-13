package evolution

import java.lang.Math.random

/**
 * Random Selection
 *
 * Selects an individual randomly from the population.
 */
fun <T> randomSelection(scoredPopulation: Collection<Pair<Double, T>>): T {
    return scoredPopulation.elementAt((random() * scoredPopulation.size.toDouble()).toInt()).second
}

/**
 * Ranked Selection
 *
 * Selects an individual randomly from fittest 50% of the population.
 */
fun <T> truncationSelection(scoredPopulation: Collection<T>): T {
    return scoredPopulation.elementAt((random() * scoredPopulation.size.toDouble() * 0.5).toInt())
}

fun tournamentStyleSelection(scoredPopulation: Collection<Genome>): Genome {
    val populationPool = scoredPopulation.toMutableList()
    val selected = mutableListOf<Genome>()

    do {
        val pick = populationPool.random()
        if (!selected.contains(pick)) {
            selected.add(pick)
            populationPool.remove(pick)
        }
    } while (selected.size < 3)

    selected.sortByDescending { it.fitness }
    return selected.first()
}

/**
 * Roulette Wheel Selection
 *
 * Selects an individual with a probability proportional to it's score. Also known as roulette wheel selection.
 */
fun fitnessProportionateSelection(scoredPopulation: Collection<Genome>): Genome {
    var value = scoredPopulation.sumBy { it.fitness } * random()

    for (genome in scoredPopulation) {
        value -= genome.fitness
        if (value <= 0) return genome
    }

    return scoredPopulation.last()
}