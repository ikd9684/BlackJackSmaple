package app.ikd9684.android.study.blackjack_sample.activities_fragments.commons

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<M, B : ViewDataBinding>(@LayoutRes private val resId: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class RowViewHolder<B : ViewDataBinding>(val binding: B, val context: Context) :
        RecyclerView.ViewHolder(binding.root)

    private val rowData = mutableListOf<M>()

    fun getItem(position: Int): M = rowData[position]

    fun clearItem() {
        val itemCount = rowData.size
        rowData.clear()
        notifyItemRangeRemoved(0, itemCount)
    }

    fun addItem(item: M?) {
        item ?: return

        val index = rowData.size
        rowData.add(item)
        notifyItemInserted(index)
    }

    fun addItem(index: Int, item: M?) {
        item ?: return

        rowData.add(index, item)
        notifyItemInserted(index)
    }

    fun addAllItem(list: List<M>) {
        val positionStart = rowData.size
        val itemCount = list.size
        rowData.addAll(list)
        notifyItemRangeInserted(positionStart, itemCount)
    }

    fun addDifference(list: List<M>) {
        val difference = list.filter { rowData.contains(it).not() }
        addAllItem(difference)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<B>(
            LayoutInflater.from(parent.context),
            resId,
            parent,
            false
        )
        return RowViewHolder(binding, parent.context)
    }

    abstract fun onBindViewHolder(holder: RowViewHolder<B>, position: Int)

    final override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        @Suppress("UNCHECKED_CAST")
        onBindViewHolder(holder as RowViewHolder<B>, position)
    }

    override fun getItemCount(): Int {
        return rowData.size
    }
}