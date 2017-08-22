package me.imzack.app.end.view.adapter

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import me.imzack.app.end.R
import me.imzack.app.end.common.Constant
import me.imzack.app.end.model.DataManager
import me.imzack.app.end.model.bean.Type
import me.imzack.app.end.util.ResourceUtil
import me.imzack.app.end.util.StringUtil
import me.imzack.app.end.view.widget.CircleColorView

class TypeSearchListAdapter(private val mTypeSearchList: List<Type>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    var mOnTypeItemClickListener: ((typeListPos: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            when (viewType) {
                Constant.VIEW_TYPE_HEADER -> HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.header_list_type_search, parent, false))
                Constant.VIEW_TYPE_ITEM -> ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_type_search, parent, false))
                else -> throw IllegalArgumentException("The argument \"viewType\" cannot be \"$viewType\"")
            }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Constant.VIEW_TYPE_HEADER -> {
                val headerViewHolder = holder as HeaderViewHolder
                headerViewHolder.mTypeSearchCountText.text = ResourceUtil.getQuantityString(R.string.text_type_search, R.plurals.text_type_count, mTypeSearchList.size)
            }
            Constant.VIEW_TYPE_ITEM -> {
                val itemViewHolder = holder as ItemViewHolder
                val (_, name, markColor, markPattern, _, hasMarkPattern) = mTypeSearchList[position - 1]
                itemViewHolder.mTypeMarkIcon.setFillColor(Color.parseColor(markColor))
                itemViewHolder.mTypeMarkIcon.setInnerIcon(if (hasMarkPattern) ResourceUtil.getDrawable(markPattern!!) else null)
                itemViewHolder.mTypeMarkIcon.setInnerText(StringUtil.getFirstChar(name))
                itemViewHolder.mTypeNameText.text = name
                itemViewHolder.itemView.setOnClickListener { mOnTypeItemClickListener?.invoke(DataManager.getTypeLocationInTypeList(mTypeSearchList[itemViewHolder.layoutPosition - 1].code)) }
            }
        }
    }

    override fun getItemCount() = mTypeSearchList.size + 1

    override fun getItemViewType(position: Int) = if (position == 0) Constant.VIEW_TYPE_HEADER else Constant.VIEW_TYPE_ITEM

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.text_type_search_count)
        lateinit var mTypeSearchCountText: TextView

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.ic_type_mark)
        lateinit var mTypeMarkIcon: CircleColorView
        @BindView(R.id.text_type_name)
        lateinit var mTypeNameText: TextView

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
