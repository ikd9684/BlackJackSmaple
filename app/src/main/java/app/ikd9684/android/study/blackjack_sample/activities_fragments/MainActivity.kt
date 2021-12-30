package app.ikd9684.android.study.blackjack_sample.activities_fragments

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import app.ikd9684.android.study.blackjack_sample.R
import app.ikd9684.android.study.blackjack_sample.activities_fragments.commons.BaseRecyclerViewAdapter
import app.ikd9684.android.study.blackjack_sample.databinding.ActivityMainBinding
import app.ikd9684.android.study.blackjack_sample.databinding.LayoutCardListItemBinding
import app.ikd9684.android.study.blackjack_sample.model.Card
import app.ikd9684.android.study.blackjack_sample.view_model.BlackJackViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val bj: BlackJackViewModel by viewModels()

    private val dealersCardListAdapter = CardListAdapter()
    private val playersCardListAdapter = CardListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        binding.btnNew.isEnabled = true
        binding.btnNext.isEnabled = false
        binding.btnHit.isEnabled = false
        binding.btnStand.isEnabled = false

        binding.btnNew.setOnClickListener {
            Log.d("■", "= New ===============================")

            dealersCardListAdapter.clearItem()
            playersCardListAdapter.clearItem()

            val player1Name = "プレイヤー1"
            bj.startNewGame(listOf(player1Name))

            binding.tvDealer.text = getString(R.string.label_dealer)
            binding.tvPlayer1.text = player1Name

            binding.btnNew.isEnabled = false
            binding.btnNext.isEnabled = false
            binding.btnHit.isEnabled = true
            binding.btnStand.isEnabled = true
        }

        binding.btnNext.setOnClickListener {
            Log.d("■", "- Next ------------------------------")

            dealersCardListAdapter.clearItem()
            playersCardListAdapter.clearItem()

            bj.startNextPlay()

            binding.btnNew.isEnabled = true
            binding.btnNext.isEnabled = false
            binding.btnHit.isEnabled = true
            binding.btnStand.isEnabled = true
        }
        binding.btnHit.setOnClickListener {
            bj.hit()
        }
        binding.btnStand.setOnClickListener {
            bj.stand()
        }

        binding.rvDealersCards.adapter = dealersCardListAdapter
        binding.rvPlayersCards.adapter = playersCardListAdapter

        bj.dealer.observe(this) { dealer ->
            Log.d("■", "- Dealer -------------------------")
            Log.d("■", "cards=${bj.cards}")
            Log.d("■", "dealer=${dealer}")

            dealersCardListAdapter.addDifference(dealer.cards)
        }

        bj.player.observe(this) { player ->
            Log.d("■", "- Player -------------------------")
            Log.d("■", "dealer=${bj.dealer.value}")
            Log.d("■", "player=$player")

            playersCardListAdapter.addDifference(player.cards)
        }

        bj.players.observe(this) { players ->
            Log.d("■", "- Players -------------------------")
            Log.d("■", "cards=${bj.cards}")
            Log.d("■", "dealer=${bj.dealer.value}")
            Log.d("■", "players=$players")
        }

        bj.turn.observe(this) { turn ->
            Log.d("■", "- Turn -------------------------")
            Log.d("■", "dealer=${bj.dealer.value}")
            Log.d("■", "players=${bj.players.value}")
            Log.d("■", "turn=$turn")

            binding.tvStatus.text = getString(R.string.label_turn, turn.name)
        }

        bj.result.observe(this) { result ->
            Log.d("■", "- Finish -------------------------")
            Log.d("■", "dealer=${bj.dealer.value}")
            Log.d("■", "winner=${result.winners}")
            Log.d("■", "loser=${result.losers}")
            Log.d("■", "draws=${result.draws}")

            val player1Name = bj.players.value?.firstOrNull()?.name ?: ""
            val player1Result = when {
                result.winners.any { it.name == player1Name } -> getString(R.string.result_win)
                result.losers.any { it.name == player1Name } -> getString(R.string.result_lose)
                else -> getString(R.string.result_draw)
            }

            dealersCardListAdapter.apply {
                notifyItemRangeChanged(0, itemCount)
            }

            binding.tvStatus.text =
                getString(R.string.result_placeholder, player1Name, player1Result)

            binding.btnNew.isEnabled = true
            binding.btnNext.isEnabled = true
            binding.btnHit.isEnabled = false
            binding.btnStand.isEnabled = false
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

//            holder.binding.includeCard.ivCard.setOnClickListener {
//                card.isDown = card.isDown.not()
//                holder.binding.includeCard.ivCard.setImageDrawable(card.getDrawable(holder.context))
//            }
        }
    }
}
