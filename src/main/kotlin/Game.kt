import constants.CardType
import java.util.stream.Collectors

val player: Player = Player()
val enemyPlayer: Player = Player()
var currentTurn: Int = 0

fun main(args: Array<String>) {
    run()
}

fun run() {
    gameSetup()

    while (enemyPlayer.life > 0) {

        currentTurn++
        println("\nTurn #$currentTurn")

        untapStep()
        upkeepStep()
        drawStep()
        mainPhase()
        combatPhase()
        mainPhasePostCombat()
        endStep()
    }
}

fun gameSetup() {
    player.drawFromLibrary(7)
}

fun untapStep() {
    player.library.cards.forEach { card -> card.isTapped = false }
}

fun upkeepStep() {

}

fun drawStep() {
    player.drawFromLibrary(1)
}

fun mainPhase() {
    player.playLand()
    player.tapLands()
    findPlay()
    player.manaPool = 0
}

fun combatPhase() {
    runDamage()
}

fun mainPhasePostCombat() {

}

fun endStep() {
    player.battleField.removeAll { card -> card.type == CardType.INSTANT || card.type == CardType.SORCERY }
}

fun findPlay() {
    val playableCards: List<Card> = player.hand.stream()
            .filter { card -> card.type != CardType.LAND && card.cost <= player.manaPool }
            .collect(Collectors.toList())

    if (playableCards.isNotEmpty()) {
        player.playCard(playableCards.first())
    }
}

fun runDamage() {
    var totalDamage = 0
    player.battleField
            .filter { card -> card.type != CardType.LAND }
            .forEach { card ->
                println("Attacking with ${card.name} for ${card.attack}")
                totalDamage += when (card.type) {
                    CardType.CREATURE -> card.attack
                    CardType.INSTANT -> card.attack
                    CardType.SORCERY -> card.attack
                    else -> {
                        0
                    }
                }
            }
    if (totalDamage > 0)
        enemyPlayer.life -= totalDamage
    println("Enemy HP: ${enemyPlayer.life}, Damaged for: $totalDamage")
}