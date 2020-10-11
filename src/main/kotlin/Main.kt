import constants.LIMITED_EDITION_ALPHA
import evolution.EvolutionManager
import evolution.truncationSelection
import utils.callGetCardsBySet

fun main() {
    callGetCardsBySet(LIMITED_EDITION_ALPHA)
    EvolutionManager(configs.populationSize, ::truncationSelection).run(configs.generations)
}