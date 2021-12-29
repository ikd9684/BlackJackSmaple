package app.ikd9684.android.study.blackjack_sample.logic

import app.ikd9684.android.study.blackjack_sample.model.Card
import app.ikd9684.android.study.blackjack_sample.model.Player

class BlackJack {

    class BJPlayer(name: String) : Player(name) {

        private val cardsImpl = mutableListOf<Card>()
        val cards: List<Card>
            get() = cardsImpl

        val count: Int
            get() {
                var count = 0

                cardsImpl.filter { 1 < it.number }.onEach { card ->
                    count += if (card.number in 11..13) {
                        10
                    } else {
                        card.number
                    }
                }
                val numberOfAces = cardsImpl.count { it.number == 1 }
                if (0 < numberOfAces) {
                    if (numberOfAces == 1) {
                        count += if (count + 11 <= 21) 11 else 1
                    } else {
                        count += ((numberOfAces - 1) * 1)
                        count += if (count + 11 <= 21) 11 else 1
                    }
                }

                return count
            }

        fun reset() {
            cardsImpl.clear()
        }

        fun deal(card: Card) {
            cardsImpl.add(card)
        }

        override fun toString(): String {
            return "BJPlayer($name: count=$count cards=$cardsImpl)"
        }
    }

    private val playersImpl = mutableListOf<BJPlayer>()
    val players: List<BJPlayer>
        get() = playersImpl

    val dealer = BJPlayer("ディーラー")

    private val cardsImpl = mutableListOf<Card>()
    val cards: List<Card>
        get() = cardsImpl

    var turn: BJPlayer = dealer
        private set

    var completionComputeDealer: ((dealer: BJPlayer) -> Unit)? = null

    fun startNewGame(playersNameList: List<String>, numberOfDeck: Int = 1) {
        playersImpl.clear()
        playersNameList.map { name ->
            playersImpl.add(BJPlayer(name))
        }

        cardsImpl.clear()
        repeat(numberOfDeck) {
            cardsImpl.addAll(Card.newCardList(numberOfJoker = 0))
        }
        cardsImpl.shuffle()

        startNextPlay()
    }

    fun startNextPlay() {
        playersImpl.onEach { player ->
            player.reset()
        }

        repeat(2) {
            dealOutACardTo(dealer)
            playersImpl.onEach { player ->
                dealOutACardTo(player)
            }
        }

        turn = playersImpl.first()
    }

    fun hit() {
        dealOutACardTo(turn)
        if (21 <= turn.count) {
            turnToNext()
        }
    }

    fun stand() {
        turnToNext()
    }

    private fun computeDealer() {
        playersImpl.filter {
            it.count <= 21
        }.maxOfOrNull {
            it.count
        }?.let { playersMaxCount ->
            while (dealer.count < playersMaxCount) {
                dealOutACardTo(dealer)
                completionComputeDealer?.invoke(dealer)
            }
        }
    }

    private fun turnToNext() {
        val index = playersImpl.indexOf(turn)
        turn = when (index) {
            -1 -> {
                playersImpl.first()
            }
            playersImpl.size - 1 -> {
                dealer
            }
            else -> {
                playersImpl[index + 1]
            }
        }

        if (turn == dealer) {
            computeDealer()
        }
    }

    private fun dealOutACardTo(player: BJPlayer): Boolean {
        if (player.count < 21) {
            cardsImpl.removeFirstOrNull()?.let { card ->
                player.deal(card)
                return true
            }
        }
        return false
    }

    fun judgeTheWinner(): Player? {
        return if (turn == dealer) {
            mutableListOf<BJPlayer>().apply {
                addAll(playersImpl)
                add(dealer)
            }.filter {
                it.count <= 21
            }.maxByOrNull {
                it.count
            } ?: dealer
        } else {
            null
        }
    }

//    private fun setCardFace(card: Card, isDown: Boolean) {
//        cardsImpl.firstOrNull {
//            it === card
//        }?.let { selectedCard ->
//            selectedCard.isDown = isDown
//        }
//    }
//
//    private fun setCardFace(suit: Card.Suit, number: Int, isDown: Boolean) {
//        cardsImpl.firstOrNull {
//            it.suit == suit && it.number == number
//        }?.let { selectedCard ->
//            selectedCard.isDown = isDown
//        }
//    }
//
//    private fun sortAscending() {
//        cardsImpl.sortedWith(
//            compareBy({
//                it.suit
//            }, {
//                it.number
//            })
//        )
//    }
//
//    private fun shuffle() {
//        cardsImpl.shuffle()
//    }
//
//    private fun setAllFace(isDown: Boolean) {
//        cardsImpl.onEach { card ->
//            card.isDown = isDown
//        }
//    }
}