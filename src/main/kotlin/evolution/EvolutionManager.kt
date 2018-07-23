package evolution

import Game
import Library
import data.getRandomCard
import java.util.concurrent.ThreadLocalRandom

class EvolutionManager(private val elitism: Boolean = true) {

    // Chance for 2 genomes to swap halves
    private val swapChance = 0.5
    // Chance to mutate some of the cards
    private val mutationChance = 0.01
    // Number of times the deck will be ran
    private val numOfRuns: Int = 2
    // Size of the population that will be evaluated
    private val populationSize: Int = 10
    // Current generation during the evolution
    private var generation: Int = 1
    private var population: MutableList<Genome> = mutableListOf()

    init {
        while (population.size < populationSize) {
            population.add(Genome())
        }

        println("-- Initial Population Sample --")
        population[0].library.printLibrary()
    }

    fun run(numOfGenerations: Int) {
        for (g in 0 until numOfGenerations) {
            evaluatePopulation()

            println("-- Gen $generation --")
            println("-- Best deck --")
            println("Fitness: ${population[0].fitnessAverage()}\n")
            population[0].library.printLibrary()
            println("-- Worst deck --")
            println("Fitness: ${population[1].fitnessAverage()}\n")
            population[population.size - 1].library.printLibrary()


            buildNextGeneration()
        }


    }

    private fun evaluatePopulation() {
        for (genome in population) {
            for (i in 0 until numOfRuns) {
                println("\n-- Run $i --")
                genome.fitnessResults.add(Game(genome.copy().library).run())
            }
        }
        population.sortBy { genome -> genome.fitnessAverage() }
    }

    private fun buildNextGeneration(): Array<Genome> {

        val newPopulation = population.toTypedArray()
        val elitismOffset = if (elitism) 1 else 0

        (elitismOffset until population.size).forEach {
            val genome = breed(
                    truncationSelection(population),
                    truncationSelection(population))
            mutate(genome)
            newPopulation[it] = genome
        }

        return newPopulation
    }

    private fun breed(genome1: Genome, genome2: Genome): Genome {
        genome1.library.sortCards()
        genome2.library.sortCards()

        val splitPos = ThreadLocalRandom.current().nextInt(Math.min(genome1.library.cards.size, genome2.library.cards.size))

        return if (ThreadLocalRandom.current().nextDouble() <= swapChance) {
            val childGenome = Genome(Library(ArrayList(genome1.library.cards.subList(0, splitPos))))
            childGenome.library.cards.addAll(ArrayList(genome2.library.cards.subList(splitPos, genome2.library.cards.size)))
            mutate(childGenome)
        } else {
            if (ThreadLocalRandom.current().nextDouble() < 0.5)
                genome1
            else
                genome2
        }
    }

    private fun mutate(genome: Genome): Genome {
        for (i in 0 until genome.library.cards.size) {
            if (ThreadLocalRandom.current().nextDouble() <= mutationChance) {
                genome.library.cards[i] = getRandomCard()
            }
        }
        return genome
    }
}

/**
 * Selects an individual randomly from fittest 50% of the population.
 */
fun <T> truncationSelection(scoredPopulation: Collection<T>): T {
    return scoredPopulation.elementAt((ThreadLocalRandom.current().nextDouble() * scoredPopulation.size.toDouble() * 0.5).toInt())
}
