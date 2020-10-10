
import com.charleskorn.kaml.Yaml
import evolution.EvolutionManager
import evolution.truncationSelection

private val configYaml = object {}.javaClass.getResource("config.yaml").readText()
val configs: EvolutionSettings = Yaml.default.decodeFromString(EvolutionSettings.serializer(), configYaml)

fun main() {
    EvolutionManager(
            configs.elitism,
            configs.swapChance,
            0.02,
            true,
            configs.populationSize,
            ::truncationSelection).run(configs.generations)
}