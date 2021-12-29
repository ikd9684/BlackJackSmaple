package app.ikd9684.android.study.blackjack_sample.activities_fragments

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.ikd9684.android.study.blackjack_sample.R
import app.ikd9684.android.study.blackjack_sample.activities_fragments.commons.BaseRecyclerViewAdapter
import app.ikd9684.android.study.blackjack_sample.databinding.ActivityMainBinding
import app.ikd9684.android.study.blackjack_sample.databinding.LayoutCardListItemBinding
import app.ikd9684.android.study.blackjack_sample.model.Card
import app.ikd9684.android.study.blackjack_sample.view_model.BlackJackViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val bj: BlackJackViewModel by viewModels()

    private val cardListAdapter = CardListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.btnSort.setOnClickListener {
            bj.sortAscending()
        }
        binding.btnShuffle.setOnClickListener {
            bj.shuffle()
        }
        binding.btnOpen.setOnClickListener {
            bj.setAllFace(false)
        }
        binding.btnDown.setOnClickListener {
            bj.setAllFace(true)
        }

        binding.rvCardList.layoutManager = GridLayoutManager(this, 5, RecyclerView.VERTICAL, false)
        binding.rvCardList.adapter = cardListAdapter

        bj.cardList.observe(this) { cardList ->
            cardListAdapter.rowData = cardList
        }

        bj.cardList.value?.let { cardListAdapter.rowData = it }
    }

    class CardListAdapter : BaseRecyclerViewAdapter<Card, LayoutCardListItemBinding>(
        R.layout.layout_card_list_item
    ) {
        override fun onBindViewHolder(
            holder: RowViewHolder<LayoutCardListItemBinding>,
            position: Int
        ) {
            val card = rowData[position]

            holder.binding.includeCard.card = card
            holder.binding.includeCard.ivCard.setImageDrawable(card.getDrawable(holder.context))

            holder.binding.includeCard.ivCard.setOnClickListener {
                card.isDown = card.isDown.not()
                holder.binding.includeCard.ivCard.setImageDrawable(card.getDrawable(holder.context))
            }
        }
    }
}
