package evolution

import cards.CardFetcherUtils.filterCardsByColor
import cards.masterCardCatalog
import configs
import constants.CardType
import io.magicthegathering.kotlinsdk.model.card.MtgCard
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import utils.CardUtil.getRandomCard
import utils.ForgeUtil.executeForgeMatch
import utils.ForgeUtil.forgeOutput
import utils.ForgeUtil.getLastMatchWinInt
import utils.convertTimeToMinutesAndSeconds
import java.io.File
import java.lang.Math.random
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

class EvolutionManager(private val populationSize: Int = 10,
                       private val selection: (scoredPopulation: Collection<Genome>) -> Genome) {

    companion object {
        // How many times a generation can continue with no improvement on its fitness
        private const val MAX_NO_IMPROVEMENT_COUNT = 5

        // How many games should be played in a single match
        // NOTE: Keep this number Odd. Even numbers can end up with a DRAW.
        private const val NUM_OF_GAMES_IN_MATCH: Int = 9
    }

    private var population: MutableList<Genome> = mutableListOf()
    private var genList: MutableList<Genome> = mutableListOf()
    private var currentGeneration: Int = 1

    // We want to track improvement every generation
    private var previousFitness = 0
    private var improvementCounter = 0
    private val hasConverged: Boolean
        get() {
            return improvementCounter >= MAX_NO_IMPROVEMENT_COUNT
        }

    init {
        while (population.size < populationSize) {
            population.add(Genome(generation = currentGeneration))
        }
        logger.info { "Running Selection: ${selection.toString()}" }
        logger.info { "--- Built Initial Population: $populationSize ---" }
        logger.info { "Cards in Library:\n" + population[0].library.toString() }
    }

    /**
     * Run the evolution for X generations
     */
    fun run(numOfGenerations: Int) {
        // Run through GA
        for (g in 1..numOfGenerations) {
            currentGeneration = g
            //evaluatePopulation(g)
            evaluatePopulationSingleDeck(g, "modern_red_1")
            population.sortByDescending { genome -> genome.fitness }
            printGenerationResults(g)
            genList.add(population[0])

            // Increment No Improvement
            if (previousFitness == population[0].fitness) {
                improvementCounter += 1
                logger.info { "Increased 'No Improvement' = $improvementCounter" }
            } else {
                improvementCounter = (improvementCounter - 1).coerceAtLeast(0)
                logger.info { "Decreased 'No Improvement' = $improvementCounter" }
            }

            // If converged or hit max gen, stop
            if (g >= numOfGenerations || hasConverged) {
                logger.info { "Converged after No Improvement for $currentGeneration Generations." }
                break
            } else {
                previousFitness = population[0].fitness
                population = buildNextGeneration()
            }
        }

        // Print out Final Results
        println("\n**** [Results] ****")
        println("Generations Ran: $currentGeneration")
        println("Elitism: ${configs.elitism}")
        println("Mutation Chance: ${configs.mutationChance}")
        println("** Best From Final Generation **")
        println(genList[genList.size - 1].toString())
        // Write all generations best decks in file
        val allDecks = genList.map { it.toString() }.joinToString("") { it }
        val f = File("Results.txt")
        f.writeText("List of all Best in Generations")
        f.writeText(allDecks)
    }

    /**
     * For every genome, run a game and produce a fitness
     */
    private fun evaluatePopulationSingleDeck(gen: Int, deckBaseLine: String) {
        // Use ThreadPool to run multiple Forge AI Matches
        logger.info { "Starting Population Eval" }
        val nThreads = 24
        val threadPool = Executors.newFixedThreadPool(nThreads)
        val duration = measureTimeMillis {
            population.forEachIndexed { i, genome ->
                threadPool.execute {
                    genome.name = "${gen}_${i}_${genome.color}"
                    forgeOutput(genome)
                    val matchLogs = executeForgeMatch(genome.name, deckBaseLine, NUM_OF_GAMES_IN_MATCH)

                    for (n in matchLogs.indices.reversed()) {
                        if (matchLogs[n].contains("Match")) {
                            val genomeFit = getLastMatchWinInt(matchLogs[n], genome.name)
                            val baseFit = getLastMatchWinInt(matchLogs[n], deckBaseLine)
                            val matchWin = if (genomeFit > baseFit) 1 else 0
                            genome.fitness = fitnessCalc(genomeFit, baseFit, matchWin)
                            logger.debug { genome.getNameFitnessString() }
                            break
                        }
                    }
                }
            }
            runBlocking {
                threadPool.shutdown()
                threadPool.awaitTermination(10, TimeUnit.MINUTES)
            }
        }
        logger.info { "Population eval took ${convertTimeToMinutesAndSeconds(duration)}" }
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

            val matchLogs = executeForgeMatch(duoGenomes[0].name, duoGenomes[1].name, NUM_OF_GAMES_IN_MATCH)

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
                    logger.info { duoGenomes[0].getNameFitnessString() }
                    logger.info { duoGenomes[1].getNameFitnessString() }
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
        val newPopulation = mutableListOf<Genome>()
        if (configs.elitism) newPopulation.add(population[0])
        // Breed and Mutate
        while (newPopulation.size < population.size) {
            val parents: List<Genome> = selectParents()
            val childGenomes = BreedUtil.breed(parents[0], parents[1])
            childGenomes.forEach { g -> mutate(g) }
            newPopulation.addAll(childGenomes)
        }

        if (configs.newChild) newPopulation[newPopulation.lastIndex] = Genome(generation = (currentGeneration + 1))
        return newPopulation
    }

    /**
     * Select a parent and make sure they are not the same
     */
    private fun selectParents(): List<Genome> {
        var p1: Genome
        var p2: Genome
        do {
            p1 = selection(population)
            p2 = selection(population)
        } while (p1.name == p2.name)
        return listOf(p1.copy(), p2.copy())
    }

    private fun mutate(genome: Genome): Genome {
        val myCardList = filterCardsByColor(genome.color, masterCardCatalog[configs.mtgSet]!!)
        for (i in 0 until genome.library.cards.size) {
            if (genome.library.cards[i].types.map { it.toLowerCase() }.contains(CardType.LAND.name.toLowerCase())) continue
            if (random() <= configs.mutationChance) {
                val changeCard: MtgCard = getRandomCard(myCardList)
                val counts: Map<String, Int> = genome.library.nonLands().groupingBy { it.name }.eachCount()
                if (counts.containsKey(changeCard.name)
                        && counts[changeCard.name] ?: error("Unable to find card in counts for mutation") < 4
                        && genome.library.cards[i].name != changeCard.name) {
                    logger.info { "Mutating ${genome.library.cards[i].name} => ${changeCard.name}" }
                    genome.library.cards[i] = changeCard
                }
            }
        }
        return genome.copy()
    }

    private fun printGenerationResults(gen: Int) {
        val sb = StringBuilder()
        sb.appendLine("\n-----[Generation #$gen]-----")
        sb.appendLine("**** Best Decks ****")
        sb.append(population[0].toString())
        println(sb)
    }


}

