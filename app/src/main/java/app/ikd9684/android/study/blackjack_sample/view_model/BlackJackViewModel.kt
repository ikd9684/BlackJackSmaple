package app.ikd9684.android.study.blackjack_sample.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.ikd9684.android.study.blackjack_sample.logic.BlackJack
import app.ikd9684.android.study.blackjack_sample.model.Card
import app.ikd9684.android.study.blackjack_sample.model.Player

class BlackJackViewModel : ViewModel() {

    private val blackJack = BlackJack()

    private val playersImpl = MutableLiveData<List<BlackJack.BJPlayer>>()
    val players: LiveData<List<BlackJack.BJPlayer>>
        get() = playersImpl

    private val dealerImpl = MutableLiveData<BlackJack.BJPlayer>()
    val dealer: LiveData<BlackJack.BJPlayer>
        get() = dealerImpl

    val turn: BlackJack.BJPlayer
        get() = blackJack.turn

    val cards: List<Card>
        get() = blackJack.cards

    fun startNewGame(playersNameList: List<String>) {
        blackJack.completionComputeDealer = { dealer ->
            dealerImpl.postValue(dealer)
        }
        blackJack.startNewGame(playersNameList)
        playersImpl.postValue(blackJack.players)
    }

    fun startNextPlay() {
        blackJack.startNextPlay()
        playersImpl.postValue(blackJack.players)
    }

    fun hit() {
        blackJack.hit()
        playersImpl.postValue(blackJack.players)
    }

    fun stand() {
        blackJack.stand()
        playersImpl.postValue(blackJack.players)
    }

    fun judgeTheWinner(): Player? {
        return blackJack.judgeTheWinner()
    }
}
