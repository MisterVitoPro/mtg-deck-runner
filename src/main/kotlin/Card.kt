import constants.CardSubType
import constants.CardType
import constants.Color

data class Card(val name: String,
                val type: CardType,
                val subType: CardSubType = CardSubType.NONE,
                val color: Color,
                val cost: Int = 0,
                val attack: Int = 0,
                val defense: Int = 0) {

    var isTapped: Boolean = false

    fun printShort(): String {
        return when (type) {
            CardType.CREATURE -> "$name [$type] ($cost) $attack/$defense"
            CardType.LAND -> "$name [$type]"
            else -> "$name [$type] ($cost)"
        }

    }

}