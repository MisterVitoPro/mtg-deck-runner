package evolution

import Card
import Game
import Library
import constants.CardType
import data.getRandomCard
import java.util.concurrent.ThreadLocalRandom

class EvolutionManager(private val elitism: Boolean = true) {

    // Chance for 2 genomes to swap halves
    private val swapChance = 0.5
    // Chance to mutate some of the cards
    private val mutationChance = 0.01
    // Number of times the deck will be ran
    private val numOfRuns: Int = 500
    // Size of the population that will be evaluated
    private val populationSize: Int = 50
    private var population: MutableList<Genome> = mutableListOf()

    init {
        while (population.size < populationSize) {
            population.add(Genome())
        }
        println("-- Initial Population Sample --")
        population[0].library.printLibrary()
    }

    fun run(numOfGenerations: Int) {
        for (g in 1..numOfGenerations) {
            evaluatePopulation()

            println("\n**** [Generation #$g] ****")
            println("** Best **")
            println("Fitness: ${population[0].fitnessAverage()}\n")
            population[0].library.printLibrary()
            println("** Worst **")
            println("Fitness: ${population[population.size - 1].fitnessAverage()}\n")
            population[population.size - 1].library.printLibrary()

            population = buildNextGeneration()
        }
    }

    private fun evaluatePopulation() {
        for (genome in population) {
            for (i in 0 until numOfRuns) {
                //println("\n-- Run $i --")
                genome.fitnessResults.add(Game(genome.copy().library).run())
            }
        }
        population.sortBy { genome -> genome.fitnessAverage() }
    }

    private fun buildNextGeneration(): MutableList<Genome> {

        val newPopulation = population.toMutableList()
        val elitismOffset = if (elitism) 1 else 0

        (elitismOffset until population.size).forEach {
            val genome = breed(
                    truncationSelection(population, 0.3),
                    truncationSelection(population, 0.3))
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
            val temp: MutableList<Card> = ArrayList(genome1.library.cards.subList(0, splitPos))
            val temp2: MutableList<Card> = ArrayList(genome2.library.cards.subList(splitPos, genome2.library.cards.size))

            for(i in 0..temp2.size){
                val counts: Map<String, Int> = temp.groupingBy { it.name }.eachCount()
                if(counts[temp2[i].name]!! < 4){
                    
                }
            }

            val counts: Map<String, Int> = temp.filter { card -> card.type != CardType.LAND }
                    .groupingBy { it.name }
                    .eachCount()
                    .filter { i -> i.value > 4 }
                    .onEach { c -> c.value.minus(4) }

            val childGenome = Genome(Library(ArrayList(genome1.library.cards.subList(0, splitPos))))
            childGenome.library.cards.addAll(ArrayList(genome2.library.cards.subList(splitPos, genome2.library.cards.size)))
//            if(childGenome.library.cards.filter { card -> card.type != CardType.LAND }.groupingBy { it.name }.eachCount().any { i -> i.value > 4 }){
//                childGenome.library.printLibrary()
//            }
            return childGenome
        } else {
            if (ThreadLocalRandom.current().nextDouble() < 0.5)
                genome1.copy()
            else
                genome2.copy()
        }
    }

    private fun mutate(genome: Genome): Genome {
        for (i in 0 until genome.library.cards.size) {
            if (ThreadLocalRandom.current().nextDouble() <= mutationChance) {
                val changeCard: Card = getRandomCard()
                //println("Mutating: ${genome.library.cards[i].name} => ${changeCard.name}")
                val counts: Map<String, Int> = genome.library.cards.groupingBy { it.name }.eachCount()
                if (counts.containsKey(changeCard.name)
                        && counts[changeCard.name]!! < 4
                        && genome.library.cards[i].name != changeCard.name)
                    genome.library.cards[i] = changeCard
            }
        }
        return genome.copy()
    }
}

/**
 * Selects an individual randomly from fittest 50% of the population.
 */
fun <T> truncationSelection(scoredPopulation: Collection<T>, upperBound: Double): T {
    return scoredPopulation.elementAt((ThreadLocalRandom.current().nextDouble() * scoredPopulation.size.toDouble() * upperBound).toInt())
}
