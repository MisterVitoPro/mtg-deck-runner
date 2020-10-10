package data

import Card
import constants.CardSubType
import constants.CardType
import constants.Color
import java.util.concurrent.ThreadLocalRandom

val ALPHA_SET: List<Card> = arrayListOf(
        Card("Burrowing", CardType.ENCHANTMENT, color = Color.RED, cost = 1),
        Card("Chaoslace", CardType.INSTANT, color = Color.RED, cost = 1),
        Card("Disintegrate", CardType.SORCERY, color = Color.RED, cost = 1),
        Card("Dragon Whelp", CardType.CREATURE, color = Color.RED, cost = 4, attack = 2, defense = 3),
        Card("Dwarven Demolition Team", CardType.CREATURE, color = Color.RED, cost = 3, attack = 1, defense = 1),
        Card("Dwarven Warriors", CardType.CREATURE, color = Color.RED, cost = 3, attack = 1, defense = 1),
        Card("Earth Elemental", CardType.CREATURE, color = Color.RED, cost = 5, attack = 4, defense = 5),
        Card("Earthbind", CardType.ENCHANTMENT, color = Color.RED, cost = 1),
        Card("Earthquake", CardType.SORCERY, color = Color.RED, cost = 1),
        Card("False Orders", CardType.INSTANT, color = Color.RED, cost = 1),
        Card("Fire Elemental", CardType.CREATURE, color = Color.RED, cost = 5, attack = 5, defense = 4),
        Card("Fireball", CardType.SORCERY, color = Color.RED, cost = 1),
        Card("Firebreathing", CardType.ENCHANTMENT, color = Color.RED, cost = 1),
        Card("Flashfires", CardType.SORCERY, color = Color.RED, cost = 4),
        Card("Fork", CardType.INSTANT, color = Color.RED, cost = 2),
        Card("Goblin Balloon Brigade", CardType.CREATURE, color = Color.RED, cost = 1, attack = 1, defense = 1),
        Card("Goblin King", CardType.CREATURE, color = Color.RED, cost = 3, attack = 2, defense = 2),
        Card("Granite Gargoyle", CardType.CREATURE, color = Color.RED, cost = 3, attack = 2, defense = 2),
        Card("Gray Ogre", CardType.CREATURE, color = Color.RED, cost = 3, attack = 2, defense = 2),
        Card("Hill Giant", CardType.CREATURE, color = Color.RED, cost = 4, attack = 3, defense = 3),
        Card("Hurloon Minotaur", CardType.CREATURE, color = Color.RED, cost = 3, attack = 2, defense = 3),
        Card("Ironclaw Orcs", CardType.CREATURE, color = Color.RED, cost = 2, attack = 2, defense = 2),
        Card("Keldon Warlord", CardType.CREATURE, color = Color.RED, cost = 4, attack = 0, defense = 0),
        Card("Lightning Bolt", CardType.INSTANT, color = Color.RED, cost = 1, attack = 3),
        Card("Mana Flare", CardType.ENCHANTMENT, color = Color.RED, cost = 3),
        Card("Manabarbs", CardType.ENCHANTMENT, color = Color.RED, cost = 4),
        Card("Mons's Goblin Raiders", CardType.CREATURE, color = Color.RED, cost = 1, attack = 1, defense = 1),
        Card("Orcish Artillery", CardType.CREATURE, color = Color.RED, cost = 3, attack = 1, defense = 3),
        Card("Orcish Oriflamme", CardType.ENCHANTMENT, color = Color.RED, cost = 4),
        Card("Power Surge", CardType.ENCHANTMENT, color = Color.RED, cost = 2),
        Card("Red Elemental Blast", CardType.INSTANT, color = Color.RED, cost = 1),
        Card("Roc of Kher Ridges", CardType.CREATURE, color = Color.RED, cost = 4, attack = 3, defense = 3),
        Card("Rock Hydra", CardType.CREATURE, color = Color.RED, cost = 2, attack = 0, defense = 0),
        Card("Sedge Troll", CardType.CREATURE, color = Color.RED, cost = 3, attack = 2, defense = 2),
        Card("Shatter", CardType.INSTANT, color = Color.RED, cost = 2),
        Card("Shivan Dragon", CardType.CREATURE, color = Color.RED, cost = 6, attack = 5, defense = 5),
        Card("Smoke", CardType.ENCHANTMENT, color = Color.RED, cost = 2),
        Card("Stone Giant", CardType.CREATURE, color = Color.RED, cost = 4, attack = 3, defense = 4),
        Card("Stone Rain", CardType.SORCERY, color = Color.RED, cost = 3),
        Card("Tunnel", CardType.INSTANT, color = Color.RED, cost = 1),
        Card("Two-Headed Giant of Foriys", CardType.CREATURE, color = Color.RED, cost = 5, attack = 4, defense = 4),
        Card("Uthden Troll", CardType.CREATURE, color = Color.RED, cost = 3, attack = 2, defense = 2),
        Card("Wall of Fire", CardType.CREATURE, color = Color.RED, cost = 3, attack = 0, defense = 5),
        Card("Wall of Stone", CardType.CREATURE, color = Color.RED, cost = 3, attack = 0, defense = 8),
        Card("Badlands", CardType.LAND, CardSubType.NON_BASIC, Color.COLORLESS),
        Card("Mountain", CardType.LAND, CardSubType.BASIC, Color.COLORLESS)
)

fun getCopy(): MutableList<Card> {
    return ALPHA_SET.toMutableList()
}

fun getRandomLand(): Card {
    return Card("Mountain", CardType.LAND, CardSubType.BASIC, Color.COLORLESS)
}

fun getRandomCard(): Card {
    return ALPHA_SET[ThreadLocalRandom.current().nextInt(ALPHA_SET.size)]
}
