package evolution

import Card
import Game
import Library
import constants.CardType
import data.getRandomCard
import forgeOutput
import java.util.concurrent.ThreadLocalRandom

/**
 * swapChance       = Chance for 2 genomes to swap halves
 * mutationChance   = Chance to mutate some of the cards
 */
class EvolutionManager(private val elitism: Boolean = true,
                       private val swapChance: Double = 0.5,
                       private val mutationChance: Double = 0.01,
                       private val selection: (scoredPopulation: Collection<Genome>) -> Genome) {
    // Number of times the deck will be ran
    private val numOfRuns: Int = 500
    // Size of the population that will be evaluated
    private val populationSize: Int = 200
    // Of the scored population, top percentage we want to breed from
    private var population: MutableList<Genome> = mutableListOf()
    private var genList: MutableList<Genome> = mutableListOf()

    init {
        while (population.size < populationSize) {
            population.add(Genome())
        }
        println("-- Initial Population Sample --")
        population[0].library.print()
        genList.add(population[0])
    }

    /**
     * Run the evolution for X generations
     */
    fun run(numOfGenerations: Int) {
        for (g in 1..numOfGenerations) {
            evaluatePopulation()
            printResults(g)
            if (g == numOfGenerations) {

                val printDeck = {genome: Genome ->
                    {
                        println("Fitness: ${genome.fitnessAverage}")
                        println("Deck Size: ${genome.fitnessAverage}\n")
                        genome.library.print()
                    }
                }

                genList.add(population[0])
                println("\n**** [Results] ****")
                println("Generations Ran: $numOfGenerations")
                println("Elitism: $elitism")
                println("Mutation Chance: $mutationChance")
                println("** Initial Library **")
                printDeck(genList[0])
                println("** Best From Last Generation **")
                printDeck(genList[genList.size - 1])

                forgeOutput(genList[0].library.cards, "0_${genList[0].fitnessAverage}_MonoRed")
                forgeOutput(genList[genList.size - 1].library.cards, "${g}_${genList[genList.size - 1].fitnessAverage}_MonoRed")

                break
            }
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
                    selection(population).copy(),
                    selection(population)).copy()
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
            val childLands1: MutableList<Card> = ArrayList(genome2.library.lands())
            val childLands2: MutableList<Card> = ArrayList(genome1.library.lands())



            // We need to filter out any cards that would bring a card count > 4 (MTG rules for decks)
            val filteredGenome2: MutableList<Card> = ArrayList(genome2.library.nonLands())
            val genome1Copy: MutableList<Card> = ArrayList(genome1.library.nonLands())
            val genome2Copy: MutableList<Card> = ArrayList(genome2.library.nonLands())
            for (i in 0 until genome2Copy.size) {
                val c = genome2Copy[i]
                genome1Copy.add(c)
                val counts: Map<String, Int> = genome1Copy
                        .groupingBy { it.name }
                        .eachCount()
                if (counts[c.name]!! > 4) {
                    filteredGenome2.remove(c)
                }
            }

            // From our filtered out list, create a 60 card deck
            val num = if (filteredGenome2.size < 1) {
                println("** ERROR ** Uh oh.. filtered too many")
                0
            } else 1

            val splitPos = ThreadLocalRandom.current().nextInt(Math.min(genome1.library.nonLands().size,genome2.library.nonLands().size))
            val adjustedSplitPos = genome1.library.nonLands().size - filteredGenome2.size + num
            val reevaluatedSplitPos = if (adjustedSplitPos > splitPos) adjustedSplitPos else splitPos

            val childGenome = Genome(Library(ArrayList(childLands2)))
            childGenome.library.cards.addAll(genome1.library.nonLands().subList(0, reevaluatedSplitPos))

            // Need to start from the bottom to avoid duplicates
            val genome1Iterator: Iterator<Card> = genome1.library.nonLands().reversed().iterator()
            val genome2Iterator: Iterator<Card> = filteredGenome2.reversed().iterator()

            while (childGenome.library.cards.size < 60) {
                if (genome2Iterator.hasNext())
                    childGenome.library.cards.add(genome2Iterator.next())
                else if (genome1Iterator.hasNext()) // We are doing this as a precaution in case we are under 60
                    println("** ERROR ** Added from other list")
                    childGenome.library.cards.add(genome1Iterator.next())
            }
            if(childGenome.library.nonLands()
                            .groupingBy { it.name }
                            .eachCount()
                            .any{ c -> c.value > 4}){
                println("** ERROR ** Greater than 4 Count")
                childGenome.library.print()
            }

            return childGenome
        } else {
            if (ThreadLocalRandom.current().nextDouble() < 0.5
                    && genome1.library.cards.any { c -> c.type == CardType.LAND })
                genome1.copy()
            else
                genome2.copy()
        }
    }

    private fun mutate(genome: Genome): Genome {
        for (i in 0 until genome.library.cards.size) {
            if (ThreadLocalRandom.current().nextDouble() <= mutationChance) {
                val changeCard: Card = getRandomCard()
                val counts: Map<String, Int> = genome.library.cards.filter { c -> c.type != CardType.LAND }.groupingBy { it.name }.eachCount()
                if (counts.containsKey(changeCard.name)
                        && counts[changeCard.name]!! < 4
                        && genome.library.cards[i].name != changeCard.name)
                    genome.library.cards[i] = changeCard
            }
        }
        return genome.copy()
    }

    private fun printResults(gen: Int) {
        println("\n**** [Generation #$gen] ****")
        println("** Best **")
        println("Fitness: ${population[0].fitnessAverage}")
        println("Deck Size: ${population[0].library.cards.size}\n")
        population[0].library.print()
        println("** Worst **")
        println("Fitness: ${population[population.size - 1].fitnessAverage}")
        println("Deck Size: ${population[population.size - 1].library.cards.size}\n")
        population[population.size - 1].library.print()
    }
}

