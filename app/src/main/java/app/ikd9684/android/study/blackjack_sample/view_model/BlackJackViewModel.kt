package app.ikd9684.android.study.blackjack_sample.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.ikd9684.android.study.blackjack_sample.model.Card

class BlackJackViewModel : ViewModel() {

    private var cardListImpl = MutableLiveData<List<Card>>()
    val cardList: LiveData<List<Card>>
        get() = cardListImpl

    init {
        cardListImpl.value = Card.newCardList()
    }

    fun sortAscending() {
        cardListImpl.value?.sortedWith(
            compareBy({
                it.suit
            }, {
                it.number
            })
        )?.let { newList ->
            cardListImpl.postValue(newList)
        }
    }

    fun shuffle() {
        cardListImpl.value?.shuffled()?.let { newList ->
            cardListImpl.postValue(newList)
        }
    }

    fun setAllFace(isDown: Boolean) {
        cardListImpl.value?.onEach { card ->
            card.isDown = isDown
        }?.let { newList ->
            cardListImpl.postValue(newList)
        }
    }
}
