
import constants.CardType
import evolution.Genome
import java.util.stream.Collectors

class Game(genome: Genome) {

    private val player: Player = Player("Player", genome.library)
    private val enemyPlayer: Player = Player("Enemy")
    private var currentTurn: Int = 0

    fun run(): Int {
        gameSetup()

        do{
            currentTurn++
            //println("\nTurn #$currentTurn")

            if (currentTurn >= 15) {
                //println("\nDeck not good enough. Ending on turn $currentTurn")
                return currentTurn
            }

            untapStep()
            upkeepStep()
            drawStep()
            mainPhase()
            combatPhase()
            mainPhasePostCombat()
            endStep()
        } while (enemyPlayer.life > 0)

        // println("\nDeck won on turn $currentTurn")
        return currentTurn
    }

    private fun gameSetup() {
        player.library.shuffle()
        player.drawOpeningHand(7)
    }

    private fun untapStep() {
        player.library.cards.forEach { card -> card.isTapped = false }
    }

    private fun upkeepStep() {

    }

    private fun drawStep() {
        player.drawFromLibrary(1)
    }

    private fun mainPhase() {
        player.playLand()
        player.tapLands()
        findPlay()
        player.manaPool = 0
    }

    private fun combatPhase() {
        runDamage()
    }

    private fun mainPhasePostCombat() {

    }

    private fun endStep() {
        player.battleField.removeAll { card -> card.type == CardType.INSTANT || card.type == CardType.SORCERY }
    }

    private fun findPlay() {
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

    private fun runDamage() {
        var totalDamage = 0
        player.battleField
                .filter { card -> card.type != CardType.LAND }
                .forEach { card ->
                    ///println("Attacking with ${card.name} for ${card.attack}")
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
        //println("Enemy HP: ${enemyPlayer.life}, Damaged for: $totalDamage")
    }
}