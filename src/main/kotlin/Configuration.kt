import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable

private val configYaml = object {}.javaClass.getResource("config.yaml").readText()
val configs: EvolutionSettings = Yaml.default.decodeFromString(EvolutionSettings.serializer(), configYaml)

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
        val mutationChance: Double,
        val newChild: Boolean,
        val deckOutputFilePath: String,
        val forgeDir: String,
        val forgeJar: String
)