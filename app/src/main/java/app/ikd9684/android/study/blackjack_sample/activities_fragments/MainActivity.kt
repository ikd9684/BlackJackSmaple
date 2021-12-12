package app.ikd9684.android.study.blackjack_sample.activities_fragments

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.ikd9684.android.study.blackjack_sample.R
import app.ikd9684.android.study.blackjack_sample.activities_fragments.commons.BaseRecyclerViewAdapter
import app.ikd9684.android.study.blackjack_sample.databinding.ActivityMainBinding
import app.ikd9684.android.study.blackjack_sample.databinding.LayoutCardListItemBinding
import app.ikd9684.android.study.blackjack_sample.model.Card

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val cardListAdapter = CardListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.btnSort.setOnClickListener {
            sortAscending()
        }
        binding.btnShuffle.setOnClickListener {
            shuffle()
        }
        binding.btnOpen.setOnClickListener {
            allFaceDown(false)
        }
        binding.btnDown.setOnClickListener {
            allFaceDown(true)
        }

        binding.rvCardList.layoutManager = GridLayoutManager(this, 5, RecyclerView.VERTICAL, false)
        binding.rvCardList.adapter = cardListAdapter

        cardListAdapter.rowData = Card.newCardList()
    }

    private fun sortAscending() {
        cardListAdapter.apply {
            rowData = rowData.sortedWith(
                compareBy({
                    it.suit
                }, {
                    it.number
                })
            )
        }
    }

    private fun shuffle() {
        cardListAdapter.apply {
            rowData = rowData.shuffled()
        }
    }

    private fun allFaceDown(isDown: Boolean) {
        cardListAdapter.rowData.forEach { card ->
            card.isDown = isDown
        }
        cardListAdapter.notifyItemRangeChanged(0, cardListAdapter.rowData.size)
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
