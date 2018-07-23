import constants.CardType
import java.util.stream.Collectors

class Game(library: Library) {

    private val player: Player = Player("Player", library)
    private val enemyPlayer: Player = Player("Enemy")
    var currentTurn: Int = 0

    fun run(): Int {
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

            if(currentTurn >= 12){
                println("\nDeck not good enough. Ending on turn $currentTurn")
                return currentTurn
            }
        }

        println("\nDeck won on turn $currentTurn")
        return currentTurn
    }

    fun gameSetup() {
        player.drawOpeningHand(7)
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
            var sortedByAttack: List<Card> = playableCards.sortedBy { it.attack }.reversed()
            var highestAttack: MutableList<Card> = mutableListOf()

            var tempMana = player.manaPool
            for (c in sortedByAttack) {
                if (tempMana - c.cost >= 0)
                    highestAttack.add(c)
                tempMana -= c.cost

                if (tempMana == 0)
                    break
            }

            for (c in highestAttack)
                player.playCard(c)
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
}