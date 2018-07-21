val player: Player = Player()
val enemyPlayer: Player = Player()
var currentTurn: Int = 0

fun run() {
    while (enemyPlayer.life > 0) {

        currentTurn++

        untapStep()
        upkeepStep()
        drawStep()
        mainPhase()
        combatPhase()
        mainPhasePostCombat()
        endStep()
    }
}

fun untapStep() {

}

fun upkeepStep() {

}

fun drawStep() {
    player.library.draw()
}

fun mainPhase() {

}

fun combatPhase() {

}

fun mainPhasePostCombat() {

}

fun endStep() {

}
