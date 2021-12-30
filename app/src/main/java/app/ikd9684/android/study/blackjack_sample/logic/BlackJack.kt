package app.ikd9684.android.study.blackjack_sample.logic

import app.ikd9684.android.study.blackjack_sample.model.Card
import app.ikd9684.android.study.blackjack_sample.model.Player

class BlackJack(
    private val onHitPlayer: (player: BJPlayer, players: List<BJPlayer>) -> Unit,
    private val onStandPlayer: (player: BJPlayer, players: List<BJPlayer>) -> Unit,
    private val onHitDealer: (dealer: BJPlayer.BJDealer) -> Unit,
    private val onStandDealer: (dealer: BJPlayer.BJDealer) -> Unit,
    private val onChangeTurn: (turn: BJPlayer) -> Unit,
    private val onPlayCompletion: (result: BJJudgement.BJResult) -> Unit,
    private val judgement: BJJudgement = BJJudgementSample(),
    private val dealerLogic: DealerLogic = DealerLogicSample(),
) {

    class NoNewGameException :
        IllegalStateException("No new game have been started")

    class IllegalPlayerNameException(name: String) :
        IllegalArgumentException("You cannot specify that name as a player name: $name")

    open class BJPlayer(name: String) : Player(name) {

        class BJDealer : BJPlayer(DEALER_NAME)

        companion object {
            private const val DEALER_NAME = "dealer"

            var debug = false
        }

        init {
            if (this::class != BJDealer::class && name.lowercase() == DEALER_NAME) {
                throw IllegalPlayerNameException(name)
            }
        }

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

        val isBust: Boolean
            get() = 21 < count

        val isBlackJack: Boolean
            get() = count == 21

        val isNaturalBlackJack: Boolean
            get() = isBlackJack && cardsImpl.size == 2

        fun reset() {
            cardsImpl.clear()
        }

        fun deal(card: Card) {
            cardsImpl.add(card)
        }

        fun allOpen() {
            cardsImpl.forEach {
                it.isDown = false
            }
        }

        override fun toString(): String {
            val isDown = debug.not() && cardsImpl.any { it.isDown }
            val status =
                when {
                    isDown -> "?"
                    isBust -> "Bust"
                    isNaturalBlackJack -> "NaturalBlackJack"
                    isBlackJack -> "BlackJack"
                    else -> "Not BlackJack"
                }
            val countStr = if (isDown) "?" else "$count"
            return "$name($status count=$countStr, cards=$cardsImpl)"
        }
    }

    interface DealerLogic {
        fun compute(
            dealer: BJPlayer.BJDealer,
            players: List<BJPlayer>,
            onHit: () -> Unit,
            onStand: () -> Unit
        )
    }

    class DealerLogicSample : DealerLogic {

        override fun compute(
            dealer: BJPlayer.BJDealer,
            players: List<BJPlayer>,
            onHit: () -> Unit,
            onStand: () -> Unit
        ) {
            players.filter {
                it.count <= 21
            }.maxOfOrNull {
                it.count
            }?.let { playersMaxCount ->
                while (dealer.count < playersMaxCount || dealer.count < 17) {
                    onHit()
                }
            }
            dealer.allOpen()
            onStand()
        }
    }

    interface BJJudgement {
        class BJResult(
            val winners: List<BJPlayer>,
            val losers: List<BJPlayer>,
            val draws: List<BJPlayer>,
        )

        fun judge(
            dealer: BJPlayer.BJDealer,
            players: List<BJPlayer>,
            completion: (result: BJResult) -> Unit
        )
    }

    open class BJJudgementSample : BJJudgement {
        // ブラックジャックではプレイヤー同士は勝負しない
        // プレイヤーが複数いても、それは単に１対１の勝負が複数あるだけ
        //
        // ・ディーラーとプレイヤーの双方がバストしていたらディーラーの勝ち
        // ・ディーラーとプレイヤーの双方がバストしていない場合は 21 に近いプレイヤーが勝ち
        // ・点数が同じ場合は引き分け
        // ・２枚でブラックジャック（ナチュラルブラックジャック）と３枚以上のブラックジャックがあったらナチュラルブラックジャックの勝ち
        override fun judge(
            dealer: BJPlayer.BJDealer,
            players: List<BJPlayer>,
            completion: (result: BJJudgement.BJResult) -> Unit
        ) {
            val winners = mutableListOf<BJPlayer>()
            val losers = mutableListOf<BJPlayer>()
            val draws = mutableListOf<BJPlayer>()

            players.forEach { player ->
                if (player.isBust) {
                    losers.add(player)
                } else if (dealer.isBust) {
                    winners.add(player)
                } else if ((dealer.isNaturalBlackJack && player.isNaturalBlackJack) || (dealer.isBlackJack && player.isBlackJack)) {
                    draws.add(player)
                } else if (dealer.isNaturalBlackJack) {
                    losers.add(player)
                } else if (player.isNaturalBlackJack) {
                    winners.add(player)
                } else if (dealer.isBlackJack) {
                    losers.add(player)
                } else if (player.isBlackJack) {
                    winners.add(player)
                } else if (player.count < dealer.count) {
                    losers.add(player)
                } else if (dealer.count < player.count) {
                    winners.add(player)
                } else {
                    draws.add(player)
                }
            }

            completion(BJJudgement.BJResult(winners, losers, draws))
        }
    }

    private val playersImpl = mutableListOf<BJPlayer>()
    val players: List<BJPlayer>
        get() = playersImpl

    private val dealerImpl = BJPlayer.BJDealer()

    private val cardsImpl = mutableListOf<Card>()
    val cards: List<Card>
        get() = cardsImpl

    var turn: BJPlayer? = null
        private set

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
        if (cardsImpl.isEmpty()) {
            throw NoNewGameException()
        }

        playersImpl.forEach { player ->
            player.reset()
        }

        val initialTurn = playersImpl.first()
        turn = initialTurn

        repeat(2) { i ->
            // ディーラーのカードは１枚目だけ開く
            dealOutACardTo(dealerImpl, 0 < i)
            onHitDealer(dealerImpl)
            playersImpl.forEach { player ->
                // プレイヤーのカードは全部開く
                dealOutACardTo(player, false)
                onHitPlayer(player, playersImpl)
                onChangeTurn(initialTurn)
            }
        }
    }

    fun hit() {
        val turn = turn ?: run { throw NoNewGameException() }

        dealOutACardTo(turn, false)
        onHitPlayer(turn, playersImpl)

        if (21 <= turn.count) {
            turnToNext()
        }
    }

    fun stand() {
        val turn = turn ?: run { throw NoNewGameException() }

        onStandPlayer(turn, playersImpl)
        turnToNext()
    }

    private fun computeDealer() {
        dealerLogic.compute(
            dealerImpl,
            playersImpl,
            onHit = {
                dealOutACardTo(dealerImpl, false)
                onHitDealer(dealerImpl)
            },
            onStand = {
                onStandDealer(dealerImpl)
                judge()
            }
        )
    }

    private fun turnToNext() {
        val index = playersImpl.indexOf(turn)
        turn = when (index) {
            -1 -> {
                playersImpl.first()
            }
            playersImpl.size - 1 -> {
                dealerImpl
            }
            else -> {
                playersImpl[index + 1]
            }
        }

        turn?.let { onChangeTurn(it) }

        if (turn == dealerImpl) {
            computeDealer()
        }
    }

    private fun dealOutACardTo(player: BJPlayer, isDown: Boolean): Card? {
        if (player.count < 21) {
            cardsImpl.removeFirstOrNull()?.let { card ->
                card.isDown = isDown
                player.deal(card)
                return card
            }
        }
        return null
    }

    private fun judge() {
        turn ?: run { throw NoNewGameException() }

        judgement.judge(dealerImpl, playersImpl) { result ->
            onPlayCompletion(result)
        }
    }
}