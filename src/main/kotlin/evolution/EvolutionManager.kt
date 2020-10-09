package evolution

import Card
import EvolutionSettings
import Library
import constants.CardType
import data.getRandomCard
import executeForgeMatch
import forgeOutput
import getLastMatchWinInt
import java.io.File
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.NoSuchElementException
import kotlin.collections.ArrayList
import kotlin.math.min


/**
 * swapChance       = Chance for 2 genomes to swap halves
 * mutationChance   = Chance to mutate some of the cards
 * newChild         = New Generation will add brand new random child
 */
class EvolutionManager(private val elitism: Boolean = true,
                       private val swapChance: Double = 0.5,
                       private val mutationChance: Double = 0.01,
                       private val newChild: Boolean = true,
                       private val populationSize: Int = 10,
                       private val selection: (scoredPopulation: Collection<Genome>) -> Genome) {

    // Number of games played in a match
    private val numOfGamesInMatch: Int = 7

    // Of the scored population, top percentage we want to breed from
    private var population: MutableList<Genome> = mutableListOf()
    private var genList: MutableList<Genome> = mutableListOf()

    init {
        while (population.size < populationSize) {
            population.add(Genome())
        }
        println("-- Built Initial Population  --")
        population[0].library.print()
        genList.add(population[0])
    }

    /**
     * Run the evolution for X generations
     */
    fun run(numOfGenerations: Int) {
        for (g in 1..numOfGenerations) {
            //evaluatePopulation(g)
            evaluatePopulationSingleDeck(g)
            printGenerationResults(g)
            genList.add(population[0])
            if (g < numOfGenerations)
                population = buildNextGeneration()
        }

        // Print out Final Results
        println("\n**** [Results] ****")
        println("Generations Ran: $numOfGenerations")
        println("Elitism: $elitism")
        println("Mutation Chance: $mutationChance")
        println("** Initial Library **")
        printGenomeInfo(genList[0])
        println("** Best From Last Generation **")
        printGenomeInfo(genList[genList.size - 1])
        File("$numOfGenerations-${Date().time}")
    }

    /**
     * For every genome, run a game and produce a fitness
     */
    private fun evaluatePopulationSingleDeck(gen: Int) {
        population.forEachIndexed { index, genome ->
            genome.name = "${gen}_${index}_${genome.color}"
            forgeOutput(genome)

            val matchLogs = executeForgeMatch(genome.name, "Alpha_Mono_Red", numOfGamesInMatch)

            for (n in matchLogs.indices.reversed()) {
                if (matchLogs[n].contains("Match")) {
                    val genomeFit = getLastMatchWinInt(matchLogs[n], genome.name)
                    val baseFit = getLastMatchWinInt(matchLogs[n], "Alpha_Mono_Red")
                    val matchWin = if (genomeFit > baseFit) 1 else 0
                    genome.fitness = fitnessCalc(genomeFit, baseFit, matchWin)
                    genome.print()
                    break
                }
            }
        }
        population.sortByDescending { genome -> genome.fitness }
    }

    /**
     * For every genome, run a game and produce a fitness
     */
    private fun evaluatePopulation(gen: Int) {
        val chunks = population.chunked(2)
        var num = 0
        chunks.forEach { duoGenomes ->
            duoGenomes[0].name = "${gen}_${num}"
            duoGenomes[1].name = "${gen}_${num.inc()}"
            num += 2
            forgeOutput(duoGenomes[0])
            forgeOutput(duoGenomes[1])

            val matchLogs = executeForgeMatch(duoGenomes[0].name, duoGenomes[1].name, numOfGamesInMatch)

            for (n in matchLogs.indices.reversed()) {
                if (matchLogs[n].contains("Match")) {
                    var fit0 = getLastMatchWinInt(matchLogs[n], duoGenomes[0].name)
                    var fit1 = getLastMatchWinInt(matchLogs[n], duoGenomes[1].name)

                    // Check who won
                    // Need to make a copy because I decided "Hey, mutable things are cool, right?"
                    val fit0Copy = fit0
                    if (fit0 > fit1) {
                        fit0 = fitnessCalc(fit0Copy, fit1, 1)
                        fit1 = fitnessCalc(fit1, fit0Copy, 0)
                    } else {
                        fit0 = fitnessCalc(fit0Copy, fit1, 0)
                        fit1 = fitnessCalc(fit1, fit0Copy, 1)
                    }

                    duoGenomes[0].fitness = fit0
                    duoGenomes[1].fitness = fit1
                    duoGenomes[0].print()
                    duoGenomes[1].print()
                    break
                }
            }
        }
        population.sortByDescending { genome -> genome.fitness }
    }

    /**
     * Calculate the Fitness of wins based on weights
     */
    private fun fitnessCalc(wins: Int, losses: Int, matchWins: Int): Int {
        val matchWinWeight = 3
        val gameWinWeight = 2
        val gameLossWeight = 1
        val totalLoss = (losses * gameLossWeight).coerceAtLeast(0)
        return (wins * gameWinWeight) + (matchWinWeight * matchWins) - totalLoss
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

        if (newChild) newPopulation[newPopulation.lastIndex] = Genome()

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
            // We need to filter out any cards that would bring a card count > 4 (MTG rules for decks)
            // Take Genome 2 and remove and cards that would bring the card count > 4 when Genome 1 cards are added
            val filteredGenome2NonLands: MutableList<Card> = ArrayList(genome2.library.nonLands())
            val genome1CopyNoneLands: MutableList<Card> = ArrayList(genome1.library.nonLands())
            val genome2CopyNonLands: MutableList<Card> = ArrayList(genome2.library.nonLands())
            for (i in 0 until genome2CopyNonLands.size) {
                val card = genome2CopyNonLands[i]
                genome1CopyNoneLands.add(card)
                if (!Library.checkIfCardCountLegality(genome1CopyNoneLands, card.name)) {
                    filteredGenome2NonLands.remove(card)
                }
            }

            // We need to adjust the split in case genome1 is too large.
            // There is an edge case if filterdGenome2 is too small or 0, we want to return 1
            val num = if (filteredGenome2NonLands.size < 1) {
                System.err.println("** ERROR ** Uh oh.. filtered too many")
                0
            } else 1

            /* From our filtered out list, create a 60 card deck
             * Of the non-land cards, where are we going to split the cards and swap
             */

            //We need to split using the smallest number to make sure we do not indexOutOfBounds when we sublist
            val minValOfGenomes = min(genome1.library.nonLands().size, genome2.library.nonLands().size)
            val splitPos = ThreadLocalRandom.current().nextInt(minValOfGenomes)
            val adjustedSplitPos = genome1.library.nonLands().size - filteredGenome2NonLands.size + num
            val reevaluatedSplitPos = if (adjustedSplitPos > splitPos) adjustedSplitPos else splitPos

            // Randomly select lands from either genome parent
            val landsForChildGenome: MutableList<Card> = if (ThreadLocalRandom.current().nextDouble() < 0.5)
                genome1.library.lands() as MutableList<Card>
            else
                genome2.library.lands() as MutableList<Card>

            val childGenome = Genome(Library(landsForChildGenome))
            childGenome.library.cards.addAll(genome1.library.nonLands().subList(0, reevaluatedSplitPos))

            // Need to start from the bottom to avoid duplicates
            val genome1Iterator: Iterator<Card> = genome1.library.nonLands().shuffled().iterator()
            val genome2Iterator: Iterator<Card> = filteredGenome2NonLands.reversed().iterator()

            // Populate Child genome library with more cards if child genome library is less than 60
            while (childGenome.library.cards.size < 60) {
                when {
                    genome2Iterator.hasNext() -> {
                        childGenome.library.addCardLegally(genome2Iterator.next())
                    }
                    genome1Iterator.hasNext() -> { // We are doing this as a precaution in case we are under 60
                        println("** WARN ** Added from other list")
                        childGenome.library.addCardLegally(genome1Iterator.next())
                    }
                    else -> throw NoSuchElementException("Both genomes did not have enough cards to complete 60 card deck in Child Genome.")
                }
            }

            // Check if child genome as more than 4 cards
            // Maybe able to Remove now
            if (childGenome.library.nonLands()
                            .groupingBy { it.name }
                            .eachCount()
                            .any { c -> c.value > 4 }) {
                System.err.println("** ERROR ** Greater than 4 Count")
                System.err.println(childGenome.library.getPrintableLibrary())
            }

            return childGenome
        } else {
            // Randomly select genome 1 or 2
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
                        && counts[changeCard.name] ?: error("Unable to find card") < 4
                        && genome.library.cards[i].name != changeCard.name)
                    genome.library.cards[i] = changeCard
            }
        }
        return genome.copy()
    }

    private fun printGenomeInfo(genome: Genome) {
        println("Name: ${genome.name}")
        println("Fitness: ${genome.fitness}")
        println("Deck Size: ${genome.library.cards.size}\n")
        genome.library.print()
    }

    private fun printGenerationResults(gen: Int, printWorst: Boolean = false) {
        println("\n**** [Generation #$gen] ****")
        println("** Best **")
        printGenomeInfo(population[0])
        if (printWorst) {
            println("** Worst **")
            printGenomeInfo(population[population.size - 1])
        }
    }
}

