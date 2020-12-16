package com.example.projeto2cm.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto2cm.R
import com.example.projeto2cm.entities.User
import com.example.projeto2cm.activities.ChatActivity
import com.example.projeto2cm.utils.NavigationManager
import com.squareup.picasso.Picasso

class UserAdapter(context: Context, user: List<User>, isChatCheck: Boolean) :
    RecyclerView.Adapter<UserAdapter.ViewHolder?>() {

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
        Picasso.get().load(user2.getProfile()).placeholder(R.drawable.profile_pic).into(holder.profileimg)

        holder.itemView.setOnClickListener {
            val options = arrayOf<CharSequence>(
                "Enviar Mensagem",
                "Visitar Perfil"
            )
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle("Escolha uma das seguintes opções:")
            builder.setItems(options, DialogInterface.OnClickListener{ dialog, position ->
                if (position == 0){
                    val intent = Intent(context, ChatActivity::class.java)
                    intent.putExtra("visit_id", user2.getUID())
                    context.startActivity(intent)
                }
                if (position == 1){
                    val m = (context as AppCompatActivity).supportFragmentManager
                    NavigationManager.goToProfileFragment(m)
                }
            })
            builder.show()
        }
    }

    override fun getItemCount(): Int {
        return user.size
    }
}