package app.ikd9684.android.study.blackjack_sample.model

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import app.ikd9684.android.study.blackjack_sample.R

data class Card(
    val suit: Suit,
    val number: Int,
    val rank: String,
    var isDown: Boolean = false
) {

    enum class Suit(val mark: String) {
        Spade("♠︎"),
        Club("♣︎"),
        Diamond("♦︎"),
        Heart("♥︎"),
        Joker("Joker"),
    }

    companion object {

        private const val FACE_DOWN = R.drawable.card_back

        private val drawables = mapOf(
            "Spade_1" to R.drawable.spade_1,
            "Spade_2" to R.drawable.spade_2,
            "Spade_3" to R.drawable.spade_3,
            "Spade_4" to R.drawable.spade_4,
            "Spade_5" to R.drawable.spade_5,
            "Spade_6" to R.drawable.spade_6,
            "Spade_7" to R.drawable.spade_7,
            "Spade_8" to R.drawable.spade_8,
            "Spade_9" to R.drawable.spade_9,
            "Spade_10" to R.drawable.spade_10,
            "Spade_11" to R.drawable.spade_11_jack,
            "Spade_12" to R.drawable.spade_12_queen,
            "Spade_13" to R.drawable.spade_13_king,

            "Club_1" to R.drawable.club_1,
            "Club_2" to R.drawable.club_2,
            "Club_3" to R.drawable.club_3,
            "Club_4" to R.drawable.club_4,
            "Club_5" to R.drawable.club_5,
            "Club_6" to R.drawable.club_6,
            "Club_7" to R.drawable.club_7,
            "Club_8" to R.drawable.club_8,
            "Club_9" to R.drawable.club_9,
            "Club_10" to R.drawable.club_10,
            "Club_11" to R.drawable.club_11_jack,
            "Club_12" to R.drawable.club_12_queen,
            "Club_13" to R.drawable.club_13_king,

            "Diamond_1" to R.drawable.diamond_1,
            "Diamond_2" to R.drawable.diamond_2,
            "Diamond_3" to R.drawable.diamond_3,
            "Diamond_4" to R.drawable.diamond_4,
            "Diamond_5" to R.drawable.diamond_5,
            "Diamond_6" to R.drawable.diamond_6,
            "Diamond_7" to R.drawable.diamond_7,
            "Diamond_8" to R.drawable.diamond_8,
            "Diamond_9" to R.drawable.diamond_9,
            "Diamond_10" to R.drawable.diamond_10,
            "Diamond_11" to R.drawable.diamond_11_jack,
            "Diamond_12" to R.drawable.diamond_12_queen,
            "Diamond_13" to R.drawable.diamond_13_king,

            "Heart_1" to R.drawable.heart_1,
            "Heart_2" to R.drawable.heart_2,
            "Heart_3" to R.drawable.heart_3,
            "Heart_4" to R.drawable.heart_4,
            "Heart_5" to R.drawable.heart_5,
            "Heart_6" to R.drawable.heart_6,
            "Heart_7" to R.drawable.heart_7,
            "Heart_8" to R.drawable.heart_8,
            "Heart_9" to R.drawable.heart_9,
            "Heart_10" to R.drawable.heart_10,
            "Heart_11" to R.drawable.heart_11_jack,
            "Heart_12" to R.drawable.heart_12_queen,
            "Heart_13" to R.drawable.heart_13_king,

            "Joker_1" to R.drawable.joker_1,
            "Joker_2" to R.drawable.joker_2,
            "Joker_3" to R.drawable.joker_3,
        )

        private fun drawable(suit: Suit, number: Int): Int? {
            return drawables["${suit.name}_$number"]
        }

        private fun newJoker(id: Int): Card {
            return Card(Suit.Joker, id, "Joker_$id")
        }

        fun newCardList(
            suits: Set<Suit> = setOf(Suit.Spade, Suit.Club, Suit.Diamond, Suit.Heart),
            rangeOfNumber: Set<Int> = (1..13).toSet(),
            numberOfJoker: Int = 3
        ): MutableList<Card> {
            if (rangeOfNumber.isNotEmpty()) {
                if (rangeOfNumber.minOf { it } < 1 || 13 < rangeOfNumber.maxOf { it }) {
                    throw IllegalArgumentException("rangeOfNumber should be a range within 1 to 13: $rangeOfNumber")
                }
            }
            if (numberOfJoker < 0 || 3 < numberOfJoker) {
                throw IllegalArgumentException("numberOfJoker should be between 0 and 3: $numberOfJoker")
            }

            val cardList = mutableListOf<Card>()

            suits.forEach { suit ->
                rangeOfNumber.forEach { number ->
                    when (number) {
                        1 -> cardList.add(Card(suit, 1, "A"))
                        11 -> cardList.add(Card(suit, 11, "J"))
                        12 -> cardList.add(Card(suit, 12, "Q"))
                        13 -> cardList.add(Card(suit, 13, "K"))
                        else -> cardList.add(Card(suit, number, "$number"))
                    }
                }
            }

            (1..numberOfJoker).forEach { id ->
                cardList.add(newJoker(id))
            }

            return cardList
        }
    }

    private val image: Int?
        get() {
            return when (number) {
                1 -> drawable(suit, 1)
                11 -> drawable(suit, 11)
                12 -> drawable(suit, 12)
                13 -> drawable(suit, 13)
                else -> drawable(suit, number)
            }
        }

    fun getDrawable(context: Context): Drawable? {
        return if (isDown) {
            ContextCompat.getDrawable(context, FACE_DOWN)
        } else {
            image?.let { ContextCompat.getDrawable(context, it) }
        }
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = suit.hashCode()
        result = 31 * result + number
        result = 31 * result + rank.hashCode()
        return result
    }

    override fun toString(): String {
        return "${suit.mark}$rank"
    }
}
