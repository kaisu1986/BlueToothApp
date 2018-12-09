package jp.co.mofumofu.pictureChainGame

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class ConnectPlayerInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var titleView: TextView = itemView.findViewById(R.id.title)
    var detailView: TextView = itemView.findViewById(R.id.detail)
}

class ConnectPlayerInfoRecycleViewAdapter : RecyclerView.Adapter<ConnectPlayerInfoViewHolder>() {

    private var mList = ArrayList<WifiDirectContext.ConnectPlayerInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnectPlayerInfoViewHolder {
        val inflate = LayoutInflater.from(parent.context).inflate(R.layout.item_player_info, parent, false)
        return ConnectPlayerInfoViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: ConnectPlayerInfoViewHolder, position: Int) {
        val item = mList.elementAt(position)
        holder.titleView.text = item.ipAddress
        holder.detailView.text = item.userName
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun update(list: MutableCollection<WifiDirectContext.ConnectPlayerInfo>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }
}