package app.ikd9684.android.study.blackjack_sample.activities_fragments

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import app.ikd9684.android.study.blackjack_sample.R
import app.ikd9684.android.study.blackjack_sample.activities_fragments.commons.BaseRecyclerViewAdapter
import app.ikd9684.android.study.blackjack_sample.databinding.ActivityMainBinding
import app.ikd9684.android.study.blackjack_sample.databinding.LayoutCardListItemBinding
import app.ikd9684.android.study.blackjack_sample.logic.BJJudgement
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
        binding.rvPlayersCards.adapter = playersCardListAdapter

        bj.dealer.observe(this) { dealer ->
            binding.btnNew.isEnabled = false
            binding.btnNext.isEnabled = false
            binding.btnHit.isEnabled = false
            binding.btnStand.isEnabled = false

            dealersCardListAdapter.addDifference(dealer.cards)
        }

        bj.player.observe(this) { player ->
            binding.btnNew.isEnabled = true
            binding.btnNext.isEnabled = false
            binding.btnHit.isEnabled = true
            binding.btnStand.isEnabled = true

            playersCardListAdapter.addDifference(player.cards)
        }

        bj.turn.observe(this) { turn ->
            gameStarted = true

            binding.btnNew.isEnabled = true
            binding.btnNext.isEnabled = false
            binding.btnHit.isEnabled = (turn.name != bj.dealerName)
            binding.btnStand.isEnabled = (turn.name != bj.dealerName)

            binding.tvStatus.text = getString(R.string.label_turn, turn.name)
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

    private fun getResultString(player: BJPlayer): String {
        return when {
            player.isNaturalBlackJack -> getString(R.string.result_natural_blackjack)
            player.isBlackJack -> getString(R.string.result_blackjack)
            player.count <= 21 -> "${player.count}"
            else -> getString(R.string.result_bust)
        }
    }

    private fun onClickBtnNew() {
        dealersCardListAdapter.clearItem()
        playersCardListAdapter.clearItem()

        val player1Name = "プレイヤー1"

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

    private fun handleResultNotify(result: BJJudgement.BJResult) {
        dealersCardListAdapter.apply {
            notifyItemRangeChanged(0, itemCount)
        }

        val dealerName = bj.dealer.value?.name ?: ""
        val dealerResult = bj.dealer.value?.let { getResultString(it) }
        binding.tvDealerResult.text =
            getString(R.string.result_placeholder, dealerName, dealerResult)

        val player1Name = bj.players.value?.firstOrNull()?.name ?: ""
        val player1Result = bj.players.value?.firstOrNull()?.let { getResultString(it) }
        binding.tvPlayer1Result.text =
            getString(R.string.result_placeholder, player1Name, player1Result)

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
}
