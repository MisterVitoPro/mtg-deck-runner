import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * swapChance       = Chance for 2 genomes to swap halves
 * mutationChance   = Chance to mutate some of the cards
 * newChild         = New Generation will add brand new random child
 */
@Serializable
data class EvolutionSettings(
    val generations: Int,
    val elitism: Boolean,
    val populationSize: Int, // Size of the population that will be evaluated
    val swapChance: Double,
    val newChild: Boolean,
    val deckOutputFilePath: String,
    val forgeDir: String
)