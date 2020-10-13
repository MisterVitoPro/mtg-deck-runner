package cards

import kotlinx.serialization.Serializable

// Need to serialize the MTG file but do not have access to it
@Serializable
data class SerializedMtgCard(val id: String?,
                             val name: String,
                             val names: List<String>?,
                             val manaCost: String?,
                             val cmc: Int?,
                             val colors: List<String>?,
                             val colorIdentity: List<String>?,
                             val type: String,
                             val supertypes: List<String>?,
                             val types: List<String>,
                             val subtypes: List<String>?,
                             val rarity: String,
                             val set: String,
                             val setName: String,
                             val text: String?,
                             val artist: String,
                             val number: String?,
                             val power: String?,
                             val toughness: String?,
                             val loyalty: String?,
                             val multiverseid: Int?,
                             val imageUrl: String?,
                             val layout: String,
                             val legalities: List<MtgCardLegality>?,
        //val rulings: List<MtgCardRuling>?,
                             val foreignNames: List<MtgCardForeignName>?) {

    @Serializable
    data class MtgCardLegality(val format: String, val legality: String)

    @Serializable
    data class MtgCardRuling(val date: String, val text: String)

    @Serializable
    data class MtgCardForeignName(
            val name: String,
            val imageUrl: String?,
            val language: String,
            val multiverseid: Int?
    )
}