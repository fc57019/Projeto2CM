package com.example.projeto2cm.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto2cm.R
import com.example.projeto2cm.entities.User
import com.squareup.picasso.Picasso


class AchievementFamilyAdapter(context: Context, user: List<User>, isChatCheck: Boolean) :
    RecyclerView.Adapter<AchievementFamilyAdapter.ViewHolder?>() {

    private val context: Context
    private val user: List<User>
    private val isChatCheck: Boolean

    init {
        this.context = context
        this.user = user
        this.isChatCheck = isChatCheck
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userNametxt: TextView = itemView.findViewById(R.id.search_user)
        var profileimg: ImageView = itemView.findViewById(R.id.profile_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user2: User = user[position]
        holder.userNametxt.text = user2.getName()
        Picasso.get().load(user2.getProfile()).placeholder(R.drawable.profile_pic)
            .into(holder.profileimg)

        holder.itemView.setOnClickListener {
            val name = user2.getName()
            val participantID = user2.getUID()
            val intent = Intent("participanteID")
            intent.putExtra("name", name)
            intent.putExtra("participanteID", participantID)
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }

    }

    override fun getItemCount(): Int {
        return user.size
    }
}