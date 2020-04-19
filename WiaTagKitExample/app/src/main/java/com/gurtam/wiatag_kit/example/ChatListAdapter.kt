package com.gurtam.wiatag_kit.example

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_chat.view.*

class ChatListAdapter(private val recyclerView: RecyclerView) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

    private var items = mutableListOf<ChatItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false))
    }

    override fun onBindViewHolder(holder: ChatListAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addItem(chatItem: ChatItem) {
        items.add(chatItem)
        notifyItemChanged(items.size - 1)
        recyclerView.scrollToPosition(items.size - 1);
    }

    inner class ViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {
        fun bind(item: ChatItem) {
            v.chatMessage.text = item.message
            when (item.type) {
                MessageType.INPUT -> v.chatMessageType.setImageDrawable(v.context.getDrawable(R.drawable.ic_input))
                MessageType.OUTPUT -> v.chatMessageType.setImageDrawable(v.context.getDrawable(R.drawable.ic_output))
                MessageType.COMMAND -> v.chatMessageType.setImageDrawable(v.context.getDrawable(R.drawable.ic_command))
            }
        }
    }
}
