
import evolution.EvolutionManager
import evolution.truncationSelection

fun main(args: Array<String>) {
    EvolutionManager(
            false,
            0.5,
            0.02,
            true,
            ::truncationSelection).run(20)
}