package app.ikd9684.android.study.blackjack_sample.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.ikd9684.android.study.blackjack_sample.logic.BJJudgement
import app.ikd9684.android.study.blackjack_sample.logic.BlackJack
import app.ikd9684.android.study.blackjack_sample.models.BJPlayer
import app.ikd9684.android.study.commons.models.Card

class BlackJackViewModel : ViewModel() {

    private val blackJack = BlackJack(
        onHitPlayer = { player, players ->
            playerImpl.postValue(player)
            playersImpl.postValue(players)
        },
        onStandPlayer = { player, players ->
            playerImpl.postValue(player)
            playersImpl.postValue(players)
        },
        onHitDealer = { dealer ->
            dealerImpl.postValue(dealer)
        },
        onStandDealer = { dealer ->
            dealerImpl.postValue(dealer)
        },
        onChangeTurn = { turn ->
            turnImpl.postValue(turn)
        },
        onPlayCompletion = { result ->
            result.winners.forEach { it.plusNumberOfWins() }
            result.losers.forEach { it.plusNumberOfLosses() }
            result.draws.forEach { it.plusNumberOfDraws() }

            resultImpl.postValue(result)
        },
        onResetCards = { cards ->
            resetCardsImpl.postValue(cards)
        },
    )

    private val dealerImpl = MutableLiveData<BJPlayer>()
    val dealer: LiveData<BJPlayer>
        get() = dealerImpl

    private val playerImpl = MutableLiveData<BJPlayer>()
    val player: LiveData<BJPlayer>
        get() = playerImpl

    private val playersImpl = MutableLiveData<List<BJPlayer>>()
    val players: LiveData<List<BJPlayer>>
        get() = playersImpl

    private val turnImpl = MutableLiveData<BJPlayer>()
    val turn: LiveData<BJPlayer>
        get() = turnImpl

    private val resultImpl = MutableLiveData<BJJudgement.BJResult>()
    val result: LiveData<BJJudgement.BJResult>
        get() = resultImpl

    val cards: List<Card>
        get() = blackJack.cards

    private val resetCardsImpl = MutableLiveData<List<Card>>()
    val resetCards: LiveData<List<Card>>
        get() = resetCardsImpl

    var dealerName: String
        set(value) {
            blackJack.dealerName = value
        }
        get() = blackJack.dealerName


    fun startNewGame(playersNameList: List<String>) {
        blackJack.startNewGame(playersNameList)
    }

    fun startNextPlay() {
        blackJack.startNextPlay()
    }

    fun hit() {
        blackJack.hit()
    }

    fun stand() {
        blackJack.stand()
    }
}
