package evolution

import Card
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
    private val numOfRuns: Int = 500
    // Size of the population that will be evaluated
    private val populationSize: Int = 50
    // Of the scored population, top percentage we want to breed from
    private val fittestPercentage: Double = 0.5
    private var population: MutableList<Genome> = mutableListOf()

    init {
        while (population.size < populationSize) {
            population.add(Genome())
        }
        println("-- Initial Population Sample --")
        population[0].library.print()
    }

    /**
     * Run the evolution for X generations
     */
    fun run(numOfGenerations: Int) {
        for (g in 1..numOfGenerations) {
            evaluatePopulation()

            println("\n**** [Generation #$g] ****")
            println("** Best **")
            println("Fitness: ${population[0].fitnessAverage}\n")
            population[0].library.print()
            println("** Worst **")
            println("Fitness: ${population[population.size - 1].fitnessAverage}\n")
            population[population.size - 1].library.print()

            population = buildNextGeneration()
        }
    }

    /**
     * For every genome, run a game and produce a fitness
     */
    private fun evaluatePopulation() {
        for (genome in population) {
            for (i in 0 until numOfRuns) {
                //println("\n-- Run $i --")
                genome.fitnessResults.add(Game(genome.copy()).run())
            }
        }
        population.sortBy { genome -> genome.fitnessAverage() }
    }

    /**
     * Take the current population and breed children with a chance of mutation
     */
    private fun buildNextGeneration(): MutableList<Genome> {

        val newPopulation = population.toMutableList()
        val elitismOffset = if (elitism) 1 else 0

        (elitismOffset until population.size).forEach {
            val genome = breed(
                    truncationSelection(population, fittestPercentage).copy(),
                    truncationSelection(population, fittestPercentage)).copy()
            mutate(genome)
            newPopulation[it] = genome
        }

        return newPopulation
    }

    /**
     * Breed children of passed in Genomes
     */
    private fun breed(genome1: Genome, genome2: Genome): Genome {
        genome1.library.sortCards()
        genome2.library.sortCards()

        // Check if we should actually create children, or just send in a copy
        return if (ThreadLocalRandom.current().nextDouble() <= swapChance) {
            val splitPos = genome1.library.cards.size / 2

            // We need to filter out any cards that would bring a card count > 4 (MTG rules for decks)
            val filteredGenome2: MutableList<Card> = ArrayList(genome2.library.cards)
            val genome1Copy: MutableList<Card> = ArrayList(genome1.library.cards)//.subList(0, splitPos))
            val genome2Copy: MutableList<Card> = ArrayList(genome2.library.cards)
            for(i in 0 until genome2Copy.size){
                val c = genome2Copy[i]
                genome1Copy.add(c)
                val counts: Map<String, Int> = genome1Copy.groupingBy { it.name }.eachCount()
                if(counts[c.name]!! > 4){
                    filteredGenome2.remove(c)
                }
            }

            // From out filtered out list, create a 60 card deck
            val adjustedSplitPos = genome1.library.cards.size - filteredGenome2.size + 1
            val reevaluatedSplitPos = if(adjustedSplitPos > splitPos) adjustedSplitPos else splitPos
            val childGenome = Genome(Library(ArrayList(genome1.library.cards.subList(0, reevaluatedSplitPos))))
            genome1.library.cards.reverse()
            filteredGenome2.reverse()
            val genome1Iterator: MutableIterator<Card> = genome1.library.cards.iterator()
            val genome2Iterator: MutableIterator<Card> = filteredGenome2.iterator()
            while(childGenome.library.cards.size < 60){
                if(genome2Iterator.hasNext())
                    childGenome.library.cards.add(genome2Iterator.next())
                else if(genome1Iterator.hasNext()) // We are doing this as a percaution incase we are under 60
                    childGenome.library.cards.add(genome1Iterator.next())
            }
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
 * Selects an individual randomly from fittest x% of the population.
 */
fun <T> truncationSelection(scoredPopulation: Collection<T>, upperBound: Double): T {
    return scoredPopulation.elementAt((ThreadLocalRandom.current().nextDouble() * scoredPopulation.size.toDouble() * upperBound).toInt())
}
