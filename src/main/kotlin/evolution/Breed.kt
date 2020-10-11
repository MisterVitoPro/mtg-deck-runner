package evolution

import Library
import configs
import io.magicthegathering.kotlinsdk.model.card.MtgCard
import mu.KotlinLogging
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.ceil

private val logger = KotlinLogging.logger {}

object BreedUtil {

    /**
     * Breed children of passed in Genomes
     *
     * If it hits its swap chance, till breed the 2 parents, otherwise, it will just return the parents
     *
     * One-Point Crossover
     * In this one-point crossover, a random crossover point is selected and the tails of its two parents are swapped to get new off-springs
     */
    internal fun breed(genome1: Genome, genome2: Genome): List<Genome> {
        val nextGenInt = genome1.generation + 1

        // Check if we should actually create children, or just send in a copy
        if (ThreadLocalRandom.current().nextDouble() <= configs.swapChance) {
            logger.info { "Breeding: ${genome1.name} and ${genome2.name}" }
            // We need to filter out any cards that would bring a card count > 4 (MTG rules for decks)
            // Take Genome 2 and remove and cards that would bring the card count > 4 when Genome 1 cards are added
            val g1NonLands = genome1.library.nonLands().sortedBy { it.name }.toList()
            val g1Lands = genome1.library.lands().sortedBy { it.name }.toList()

            val g2NonLands = genome2.library.nonLands().sortedBy { it.name }.toList()
            val g2Lands = genome2.library.lands().sortedBy { it.name }.toList()

            // Split Lands
            val gc1Lands = splitCards(g1Lands, g2Lands)
            val gc2Lands = splitCards(g2Lands, g1Lands)

            // Split Non-Lands
            val gc1NonLand = splitCards(g1NonLands, g2NonLands)
            val gc2NonLand = splitCards(g2NonLands, g1NonLands)

            val childGenome1 = Genome(Library(gc1Lands), generation = nextGenInt)
            val childGenome2 = Genome(Library(gc2Lands), generation = nextGenInt)

            // Fill up rest of deck
            addNonLandToChild(childGenome1, gc1NonLand, gc2NonLand)
            addNonLandToChild(childGenome2, gc2NonLand, gc1NonLand)

            logger.info { "Child 1:\n" + childGenome1.library.getPrintableLibrary() }
            logger.info { "Child 2:\n" + childGenome2.library.getPrintableLibrary() }
            return listOf(childGenome1, childGenome2)
        } else {
            return listOf(genome1.copy(), genome2.copy())
        }
    }

    /**
     * Take top half and combine with bottom half
     */
    private fun splitCards(top: List<MtgCard>, bottom: List<MtgCard>): MutableList<MtgCard> {
        val cards = top.take(ceil(top.size / 2.0).toInt()).toMutableList()
        cards.addAll(bottom.takeLast(bottom.size / 2))
        return cards
    }


    /**
     *  Populate Child genome library
     *
     *  @param cardsToAdd will prioritize its cards to be added
     *  @param backUpFromOtherParent will be used in case it runs out
     */
    private fun addNonLandToChild(childGenome: Genome, cardsToAdd: List<MtgCard>, backUpFromOtherParent: List<MtgCard>) {
        val p1Iterator = cardsToAdd.shuffled().iterator()
        val p2Iterator = backUpFromOtherParent.shuffled().iterator()

        while (childGenome.library.cards.size < 60) {
            when {
                p1Iterator.hasNext() -> {
                    childGenome.library.addCardLegally(p1Iterator.next())
                }
                p2Iterator.hasNext() -> { // We are doing this as a precaution in case we are under 60
                    if (childGenome.library.addCardLegally(p2Iterator.next())) {
                        logger.warn { "Original Set of cards depleted. Added from Back up" }
                    }
                }
                else -> throw NoSuchElementException("Both genomes did not have enough cards to complete 60 card deck in Child Genome.")
            }
        }
        validateDeckSize(childGenome)
    }

    private fun validateDeckSize(g: Genome) {
        if (g.library.nonLands()
                        .groupingBy { it.name }
                        .eachCount()
                        .any { c -> c.value > 4 }) {
            logger.error("** ERROR ** Greater than 4 Count")
            logger.error(g.library.getPrintableLibrary())
        }
    }
}