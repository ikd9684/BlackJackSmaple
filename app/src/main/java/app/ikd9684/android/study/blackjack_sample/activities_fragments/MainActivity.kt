package app.ikd9684.android.study.blackjack_sample.activities_fragments

import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import app.ikd9684.android.study.blackjack_sample.R
import app.ikd9684.android.study.blackjack_sample.activities_fragments.commons.BaseRecyclerViewAdapter
import app.ikd9684.android.study.blackjack_sample.databinding.ActivityMainBinding
import app.ikd9684.android.study.blackjack_sample.databinding.LayoutCardListItemBinding
import app.ikd9684.android.study.blackjack_sample.logic.BJJudgement
import app.ikd9684.android.study.blackjack_sample.models.BJHand
import app.ikd9684.android.study.blackjack_sample.models.BJPlayer
import app.ikd9684.android.study.blackjack_sample.view_models.BlackJackViewModel
import app.ikd9684.android.study.commons.models.Card


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val bj: BlackJackViewModel by viewModels()

    private val dealersCardListAdapter = CardListAdapter()
    private val playersCardListAdapter = CardListAdapter()

    private var gameStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        binding.btnNew.isEnabled = true
        binding.btnNext.isEnabled = false
        binding.btnHit.isEnabled = false
        binding.btnStand.isEnabled = false

        bj.dealerName = getString(R.string.label_dealer)

        binding.btnNew.setOnClickListener {
            onClickBtnNew()
        }

        binding.btnNext.setOnClickListener {
            onClickBtnNext()
        }
        binding.btnHit.setOnClickListener {
            onCLickBtnHit()
        }
        binding.btnStand.setOnClickListener {
            onClickBtnStand()
        }

        binding.rvDealersCards.adapter = dealersCardListAdapter
        binding.rvDealersCards.addItemDecoration(CardItemDecorator(-40, 4))
        binding.rvPlayersCards.adapter = playersCardListAdapter
        binding.rvPlayersCards.addItemDecoration(CardItemDecorator(-40, 4))

        bj.dealer.observe(this) { dealer ->
            handleDealerNotify(dealer)
        }

        bj.player.observe(this) { player ->
            handlePlayerNotify(player)
        }

        bj.turn.observe(this) { turn ->
            handleTurnNotify(turn)
        }

        bj.result.observe(this) { result ->
            handleResultNotify(result)
        }

        bj.resetCards.observe(this) {
            if (gameStarted) {
                Toast.makeText(this, R.string.toast_message_reset_cards, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getResultString(hand: BJHand?): String {
        hand ?: return ""
        return when {
            hand.isNaturalBlackJack -> getString(R.string.result_natural_blackjack)
            hand.isBlackJack -> getString(R.string.result_blackjack)
            hand.count <= 21 -> "${hand.count}"
            else -> getString(R.string.result_bust)
        }
    }

    private fun onClickBtnNew() {
        dealersCardListAdapter.clearItem()
        playersCardListAdapter.clearItem()

        val player1Name = "???????????????1"

        binding.tvDealer.text = getString(R.string.label_dealer)
        binding.tvPlayer1.text = player1Name

        binding.tvDealerResult.text = ""
        binding.tvPlayer1Result.text = ""

        binding.tvCumulativeMatchResult.text = ""

        binding.btnNew.isEnabled = false
        binding.btnNext.isEnabled = false
        binding.btnHit.isEnabled = true
        binding.btnStand.isEnabled = true

        bj.startNewGame(listOf(player1Name))
    }

    private fun onClickBtnNext() {
        dealersCardListAdapter.clearItem()
        playersCardListAdapter.clearItem()

        binding.tvDealerResult.text = ""
        binding.tvPlayer1Result.text = ""

        binding.btnNew.isEnabled = true
        binding.btnNext.isEnabled = false
        binding.btnHit.isEnabled = true
        binding.btnStand.isEnabled = true

        bj.startNextPlay()
    }

    private fun onCLickBtnHit() {
        binding.btnNew.isEnabled = false
        binding.btnNext.isEnabled = false
        binding.btnHit.isEnabled = false
        binding.btnStand.isEnabled = false

        bj.hit()
    }

    private fun onClickBtnStand() {
        binding.btnNew.isEnabled = false
        binding.btnNext.isEnabled = false
        binding.btnHit.isEnabled = false
        binding.btnStand.isEnabled = false

        bj.stand()
    }

    private fun handleDealerNotify(dealer: BJPlayer) {
        binding.btnNew.isEnabled = false
        binding.btnNext.isEnabled = false
        binding.btnHit.isEnabled = false
        binding.btnStand.isEnabled = false

        dealersCardListAdapter.addDifference(dealer.hands.first().cards)
    }

    private fun handlePlayerNotify(player: BJPlayer) {
        binding.btnNew.isEnabled = true
        binding.btnNext.isEnabled = false
        binding.btnHit.isEnabled = true
        binding.btnStand.isEnabled = true

        playersCardListAdapter.addDifference(player.hands.first().cards)
    }

    private fun handleTurnNotify(turn: BJPlayer) {
        gameStarted = true

        binding.btnNew.isEnabled = true
        binding.btnNext.isEnabled = false
        binding.btnHit.isEnabled = (turn.name != bj.dealerName)
        binding.btnStand.isEnabled = (turn.name != bj.dealerName)

        binding.tvStatus.text = getString(R.string.label_turn, turn.name)
    }

    private fun handleResultNotify(result: BJJudgement.BJResult) {
        val dealer = bj.dealer.value
        val dealerName = dealer?.name ?: ""
        val dealerFirstHand = dealer?.hands?.first()
        val dealerResult = getResultString(dealerFirstHand)

        val player1 = bj.players.value?.firstOrNull()
        val player1Name = player1?.name ?: ""
        val player1FirstHand = player1?.hands?.first()
        val player1Result = getResultString(player1FirstHand)

        binding.tvPlayer1Result.text =
            getString(R.string.result_placeholder, player1Name, player1Result)

        val showMatchResult = {
            val player1MatchResult = when {
                result.winners.any { it.name == player1Name } -> getString(R.string.match_result_win)
                result.losers.any { it.name == player1Name } -> getString(R.string.match_result_lose)
                else -> getString(R.string.match_result_draw)
            }
            binding.tvStatus.text =
                getString(R.string.match_result_placeholder, player1Name, player1MatchResult)

            binding.tvCumulativeMatchResult.text =
                getString(
                    R.string.match_result_cumulative,
                    bj.players.value?.firstOrNull()?.numberOfWins,
                    bj.players.value?.firstOrNull()?.numberOfLosses,
                    bj.players.value?.firstOrNull()?.numberOfDraws,
                )

            binding.btnNew.isEnabled = true
            binding.btnNext.isEnabled = true
            binding.btnHit.isEnabled = false
            binding.btnStand.isEnabled = false
        }

        if (player1FirstHand?.isBust?.not() == true) {
            // ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            // ???????????????????????????????????????????????????????????????
            Handler(Looper.getMainLooper()).postDelayed({
                dealersCardListAdapter.notifyItemChanged(1)

                binding.tvDealerResult.text =
                    getString(R.string.result_placeholder, dealerName, dealerResult)

                showMatchResult()
            }, 500)
        } else {
            showMatchResult()
        }
    }

    class CardListAdapter : BaseRecyclerViewAdapter<Card, LayoutCardListItemBinding>(
        R.layout.layout_card_list_item
    ) {
        override fun onBindViewHolder(
            holder: RowViewHolder<LayoutCardListItemBinding>,
            position: Int
        ) {
            val card = getItem(position)

            holder.binding.includeCard.card = card
            holder.binding.includeCard.ivCard.setImageDrawable(card.getDrawable(holder.context))
        }
    }

    class CardItemDecorator(
        private val xOffset: Int,
        private val yOffset: Int
    ) : ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            if (position != 0) {
                outRect.left = xOffset
                outRect.top = yOffset * position
            }
        }
    }
}
