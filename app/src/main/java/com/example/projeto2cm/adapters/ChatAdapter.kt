package com.example.projeto2cm.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto2cm.R
import com.example.projeto2cm.activities.ViewFullImageActivity
import com.example.projeto2cm.entities.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class ChatAdapter(
    context: Context,
    chatList: List<Chat>,
    imageUrl: String
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder?>() {

    private val mContext: Context
    private val chatList: List<Chat>
    private val imageUrl: String
    var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    init {
        this.chatList = chatList
        this.mContext = context
        this.imageUrl = imageUrl
    }

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImage: ImageView? = itemView.findViewById(R.id.profile_image)
        var showTextMessage: TextView? = itemView.findViewById(R.id.show_text_message)
        var leftImageView: ImageView? = itemView.findViewById(R.id.left_image_view)
        var textSeen: TextView? = itemView.findViewById(R.id.text_seen)
        var rightImageView: ImageView? = itemView.findViewById(R.id.right_image_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return if (viewType == 1) {
            val view: View =
                LayoutInflater.from(mContext)
                    .inflate(com.example.projeto2cm.R.layout.message_item_right, parent, false)
            ChatViewHolder(view)
        } else {
            val view: View =
                LayoutInflater.from(mContext)
                    .inflate(com.example.projeto2cm.R.layout.message_item_left, parent, false)
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

                holder.rightImageView!!.setOnClickListener {
                    val options = arrayOf<CharSequence>("View Full Image", "Delete Image", "Cancel")
                    var builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("Pick a option")
                    builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
                        if (which == 0) {
                            val intent = Intent(mContext, ViewFullImageActivity::class.java)
                            intent.putExtra("url", chat.getURL())
                            mContext.startActivity(intent)

                        } else if (which == 1) {
                            deleteSentMessage(position, holder)
                        }
                    })
                    builder.show()
                }

            } else if (!chat.getSender().equals(firebaseUser!!.uid)) {
                holder.showTextMessage?.visibility = View.GONE
                holder.leftImageView?.visibility = View.VISIBLE
                Picasso.get().load(chat.getURL()).into(holder.leftImageView)

                holder.leftImageView!!.setOnClickListener {
                    val options = arrayOf<CharSequence>("View Full Image", "Cancel")
                    var builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("Pick a option")
                    builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
                        if (which == 0) {
                            val intent = Intent(mContext, ViewFullImageActivity::class.java)
                            intent.putExtra("url", chat.getURL())
                            mContext.startActivity(intent)
                        }
                    })
                    builder.show()
                }

            }
        } else {
            holder.showTextMessage!!.text = chat.getMessage().toString()

            if (firebaseUser!!.uid == chat.getSender()) {
                holder.showTextMessage!!.setOnClickListener {
                    val options = arrayOf<CharSequence>("Delet Message", "Cancel")
                    var builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("Pick a option")
                    builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
                        if (which == 0) {
                            deleteSentMessage(position, holder)
                        }
                    })
                    builder.show()
                }
            }

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

    private fun deleteSentMessage(position: Int, holder: ChatAdapter.ChatViewHolder) {
        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
            .child(chatList[position].getDu().toString()).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(holder.itemView.context, "Deleted", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(
                        holder.itemView.context,
                        "Failed, Not Deleted",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

}