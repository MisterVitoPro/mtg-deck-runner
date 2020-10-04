package evolution
import java.lang.Math.random

/**
 * Credit:
 * https://github.com/KonstantinLukaschenko/genetic-algorithm-kotlin
 */

/**
 * Selects an individual randomly from the population.
 */
fun <T> randomSelection(scoredPopulation: Collection<Pair<Double, T>>): T {
    return scoredPopulation.elementAt((random() * scoredPopulation.size.toDouble()).toInt()).second
}

/**
 * Selects an individual randomly from fittest 50% of the population.
 */
fun <T> truncationSelection(scoredPopulation: Collection<T>): T {
    return scoredPopulation.elementAt((random() * scoredPopulation.size.toDouble() * 0.5).toInt())
}

/**
 * Selects an individual with a probability proportional to it's score. Also known as roulette wheel selection.
 */
fun fitnessProportionateSelection(scoredPopulation:  Collection<Genome>): Genome {
    var value = scoredPopulation.sumByDouble { it.fitnessAverage } * random()

    for (genome in scoredPopulation) {
        value -= genome.fitnessAverage
        if (value <= 0) return genome
    }

    return scoredPopulation.last()
}