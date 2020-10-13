import cards.CardIOUtils
import cards.callGetCardsBySet
import evolution.EvolutionManager
import evolution.tournamentStyleSelection
import mu.KotlinLogging
import utils.convertTimeToMinutesAndSeconds
import kotlin.system.measureTimeMillis

/**
 * NOTE: Currently this only supports single color decks, and as of now, Red only decks (Red Deck Wins, anyone?)
 */
private val logger = KotlinLogging.logger {}

fun main() {
    val time = measureTimeMillis {
        CardIOUtils.initializeCardsFromFiles()
        callGetCardsBySet(configs.mtgSet)
        EvolutionManager(configs.populationSize, ::tournamentStyleSelection).run(configs.generations)
    }

    logger.info("GA took a total of ${convertTimeToMinutesAndSeconds(time)}")
}