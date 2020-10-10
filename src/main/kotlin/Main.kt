import constants.LIMITED_EDITION_ALPHA
import evolution.EvolutionManager
import evolution.truncationSelection
import utils.callGetCardsBySet

fun main() {

    callGetCardsBySet(LIMITED_EDITION_ALPHA)

    EvolutionManager(
            configs.elitism,
            configs.swapChance,
            0.02,
            true,
            configs.populationSize,
            ::truncationSelection).run(configs.generations)
}