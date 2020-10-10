package utils

import constants.CardType
import io.magicthegathering.kotlinsdk.model.card.MtgCard
import java.util.concurrent.ThreadLocalRandom

object CardUtil {

    fun getRandomCard(list: List<MtgCard>): MtgCard {
        return list[ThreadLocalRandom.current().nextInt(list.size)]
    }

    fun MtgCard.printableDataShort(): String {
        val type = this.types.map { it.toLowerCase() }.toList()
        return when {
            type.contains(CardType.CREATURE.name.toLowerCase()) -> "$name [${this.type}] (${this.cmc}) ${this.power}/${this.toughness}"
            type.contains(CardType.LAND.name.toLowerCase()) -> "$name [${this.type}]"
            else -> "$name [${this.type}] (${this.cmc})"
        }
    }

}