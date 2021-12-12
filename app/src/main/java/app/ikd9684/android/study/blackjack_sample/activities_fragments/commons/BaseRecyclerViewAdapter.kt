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

    var rowData: List<M> = listOf()
        set(value) {
            field = value
            notifyItemRangeChanged(0, value.size)
        }

    class RowViewHolder<B : ViewDataBinding>(val binding: B, val context: Context) :
        RecyclerView.ViewHolder(binding.root)

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