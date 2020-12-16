package com.example.projeto2cm.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto2cm.R
import com.example.projeto2cm.entities.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso

class ChatAdapter(
    context: Context,
    chatList: List<Chat>,
    imageUrl: String
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder?>() {

    private val context: Context
    private val chatList: List<Chat>
    private val imageUrl: String
    var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    init {
        this.chatList = chatList
        this.context = context
        this.imageUrl = imageUrl
    }

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImage: ImageView? = null
        var showTextMessage: TextView? = null
        var leftImageView: ImageView? = null
        var textSeen: TextView? = null
        var rightImageView: ImageView? = null

        init {
            profileImage = itemView.findViewById(R.id.profile_image)
            showTextMessage = itemView.findViewById(R.id.show_text_message)
            leftImageView = itemView.findViewById(R.id.left_image_view)
            textSeen = itemView.findViewById(R.id.text_seen)
            rightImageView = itemView.findViewById(R.id.right_image_view)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return if (viewType == 1) {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.message_item_right, parent, false)
            ChatViewHolder(view)
        } else {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.message_item_left, parent, false)
            ChatViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat: Chat = chatList[position]
        Picasso.get().load(imageUrl).into(holder.profileImage)

        // image messages
        if (chat.getMessage().equals("enviei uma mensagem") && !chat.getURL().equals("")) {
            //image message - direita
            if (chat.getSender().equals(firebaseUser!!.uid)) {
                holder.showTextMessage?.visibility = View.GONE
                holder.rightImageView?.visibility = View.VISIBLE
                Picasso.get().load(chat.getURL()).into(holder.rightImageView)
            } else if (!chat.getSender().equals(firebaseUser!!.uid)) {
                holder.showTextMessage?.visibility = View.GONE
                holder.leftImageView?.visibility = View.VISIBLE
                Picasso.get().load(chat.getURL()).into(holder.leftImageView)
            }
        } else {
            holder.showTextMessage!!.text = chat.getMessage().toString()
            Log.e("dssdsfsfsd", chat.getMessage().toString())
        }

        // sent an seen message
        if (position == chatList.size - 1) {
            if (chat.isIsSeen() == true) {
                holder.textSeen!!.text = "Seen"
                if (chat.getMessage().equals("enviei uma mensagem") && !chat.getURL().equals("")) {
                    val lp: RelativeLayout.LayoutParams? =
                        holder.textSeen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0, 245, 10, 0)
                    holder.textSeen!!.layoutParams = lp
                }
            } else {
                holder.textSeen!!.text = "Sent"
                if (chat.getMessage().equals("enviei uma mensagem") && !chat.getURL().equals("")) {
                    val lp: RelativeLayout.LayoutParams? =
                        holder.textSeen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0, 245, 10, 0)
                    holder.textSeen!!.layoutParams = lp
                }
            }
        } else {
            holder.textSeen!!.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatList[position].getSender().equals(firebaseUser!!.uid)) {
            1
        } else {
            0
        }
    }
}