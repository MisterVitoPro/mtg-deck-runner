package data

import Card
import constants.CardType
import constants.Color
import java.util.concurrent.ThreadLocalRandom

val ALPHA_SET: List<Card> = arrayListOf(
        Card("Burrowing", CardType.ENCHANTMENT, Color.RED, 1),
        Card("Chaoslace", CardType.INSTANT, Color.RED, 1),
        Card("Disintegrate", CardType.SORCERY, Color.RED, 1),
        Card("Dragon Whelp", CardType.CREATURE, Color.RED, 4, 2, 3),
        Card("Dwarven Demolition Team", CardType.CREATURE, Color.RED, 3, 1, 1),
        Card("Dwarven Warriors", CardType.CREATURE, Color.RED, 3, 1, 1),
        Card("Earth Elemental", CardType.CREATURE, Color.RED, 5, 4, 5),
        Card("Earthbind", CardType.ENCHANTMENT, Color.RED, 1),
        Card("Earthquake", CardType.SORCERY, Color.RED, 1),
        Card("False Orders", CardType.INSTANT, Color.RED, 1),
        Card("Fire Elemental", CardType.CREATURE, Color.RED, 5, 5, 4),
        Card("Fireball", CardType.SORCERY, Color.RED, 1),
        Card("Firebreathing", CardType.ENCHANTMENT, Color.RED, 1),
        Card("Flashfires", CardType.SORCERY, Color.RED, 4),
        Card("Fork", CardType.INSTANT, Color.RED, 2),
        Card("Goblin Balloon Brigade", CardType.CREATURE, Color.RED, 1, 1, 1),
        Card("Goblin King", CardType.CREATURE, Color.RED, 3, 2, 2),
        Card("Granite Gargoyle", CardType.CREATURE, Color.RED, 3, 2, 2),
        Card("Gray Ogre", CardType.CREATURE, Color.RED, 3, 2, 2),
        Card("Hill Giant", CardType.CREATURE, Color.RED, 4, 3, 3),
        Card("Hurloon Minotaur", CardType.CREATURE, Color.RED, 3, 2, 3),
        Card("Ironclaw Orcs", CardType.CREATURE, Color.RED, 2, 2, 2),
        Card("Keldon Warlord", CardType.CREATURE, Color.RED, 4, 0, 0),
        Card("Lightning Bolt", CardType.INSTANT, Color.RED, 1, 3),
        Card("Mana Flare", CardType.ENCHANTMENT, Color.RED, 3),
        Card("Manabarbs", CardType.ENCHANTMENT, Color.RED, 4),
        Card("Mons's Goblin Raiders", CardType.CREATURE, Color.RED, 1, 1, 1),
        Card("Orcish Artillery", CardType.CREATURE, Color.RED, 3, 1, 3),
        Card("Orcish Oriflamme", CardType.ENCHANTMENT, Color.RED, 4),
        Card("Power Surge", CardType.ENCHANTMENT, Color.RED, 2),
        Card("Red Elemental Blast", CardType.INSTANT, Color.RED, 1),
        Card("Roc of Kher Ridges", CardType.CREATURE, Color.RED, 4, 3, 3),
        Card("Rock Hydra", CardType.CREATURE, Color.RED, 2, 0, 0),
        Card("Sedge Troll", CardType.CREATURE, Color.RED, 3, 2, 2),
        Card("Shatter", CardType.INSTANT, Color.RED, 2),
        Card("Shivan Dragon", CardType.CREATURE, Color.RED, 6, 5, 5),
        Card("Smoke", CardType.ENCHANTMENT, Color.RED, 2),
        Card("Stone Giant", CardType.CREATURE, Color.RED, 4, 3, 4),
        Card("Stone Rain", CardType.SORCERY, Color.RED, 3),
        Card("Tunnel", CardType.INSTANT, Color.RED, 1),
        Card("Two-Headed Giant of Foriys", CardType.CREATURE, Color.RED, 5, 4, 4),
        Card("Uthden Troll", CardType.CREATURE, Color.RED, 3, 2, 2),
        Card("Wall of Fire", CardType.CREATURE, Color.RED, 3, 0, 5),
        Card("Wall of Stone", CardType.CREATURE, Color.RED, 3, 0, 8),
        Card("Wheel of Fortune", CardType.SORCERY, Color.RED, 3),
        Card("Badlands", CardType.NON_BASIC_LAND, Color.LAND),
        Card("Mountain", CardType.LAND, Color.LAND)
)

fun getCopy(): MutableList<Card> {
    return ALPHA_SET.toMutableList()
}

fun getRandomLand(): Card {
    return Card("Mountain", CardType.LAND, Color.LAND)
}

fun getRandomCard(): Card {
    return ALPHA_SET[ThreadLocalRandom.current().nextInt(ALPHA_SET.size)]
}
