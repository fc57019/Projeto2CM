package com.example.projeto2cm.activities

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto2cm.R
import com.example.projeto2cm.adapters.ChatAdapter
import com.example.projeto2cm.entities.Chat
import com.example.projeto2cm.entities.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso

class ChatActivity : AppCompatActivity() {

    var userIdVisit: String = ""
    var fireBaseUser: FirebaseUser? = null
    var chatAdapter: ChatAdapter? = null
    var chatList: List<Chat>? = null
    lateinit var recyclerViewChats: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        intent = intent
        userIdVisit = intent.getStringExtra("visit_id").toString()
        fireBaseUser = FirebaseAuth.getInstance().currentUser

        recyclerViewChats = findViewById(R.id.recycler_view_chats)
        recyclerViewChats.setHasFixedSize(true)
        var linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recyclerViewChats.layoutManager = linearLayoutManager

        val reference = FirebaseDatabase.getInstance().reference
            .child("Users").child(userIdVisit)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user: User? = snapshot.getValue(User::class.java)

                val usernameChat = findViewById<TextView>(R.id.username_chat)
                usernameChat.text = user!!.getName()
                val profilePic = findViewById<ImageView>(R.id.profile_pic)
                Picasso.get().load(user.getProfile()).into(profilePic)

                getAllMessages(fireBaseUser!!.uid, userIdVisit, user.getProfile())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        val sendMessageBtn = findViewById<ImageView>(R.id.send_message_btn)
        val textMsg = findViewById<EditText>(R.id.text_message)

        sendMessageBtn.setOnClickListener {
            val msg = textMsg.text.toString()
            if (msg == "") {
                Toast.makeText(this, "Por favor escreva uma mensagem: ", Toast.LENGTH_LONG).show()
            } else {
                sendMessageToUser(fireBaseUser?.uid, userIdVisit, msg)
            }
            textMsg.setText("")
        }

        val attachFile = findViewById<ImageView>(R.id.attach_image_file_btn)

        attachFile.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Selecione uma Imagem"), 438)
        }
    }

    private fun getAllMessages(senderId: String, receiverId: String, receiverImageUrl: String?) {
        chatList = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (chatList as ArrayList<Chat>).clear()
                for (i in snapshot.children) {
                    val chat = i.getValue(Chat::class.java)
                    if (chat!!.getReceiver().equals(senderId) && chat.getSender()
                            .equals(receiverId) || chat.getReceiver()
                            .equals(receiverId) && chat.getSender().equals(senderId)
                    ) {
                        (chatList as ArrayList<Chat>).add(chat)
                    }
                    chatAdapter = ChatAdapter(
                        this@ChatActivity,
                        (chatList as ArrayList<Chat>),
                        receiverImageUrl!!
                    )
                    recyclerViewChats.adapter = chatAdapter

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun sendMessageToUser(senderId: String?, receiverId: String?, msg: String) {
        val reference = FirebaseDatabase.getInstance().reference
        val msgKey = reference.push().key

        val messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"] = senderId
        messageHashMap["message"] = msg
        messageHashMap["receiver"] = receiverId
        messageHashMap["isseen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messagerId"] = msgKey

        reference.child("Chats")
            .child(msgKey!!)
            .setValue(messageHashMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val chatsListReference = FirebaseDatabase.getInstance()
                        .reference
                        .child("ChatList")
                        .child(fireBaseUser!!.uid)
                        .child(userIdVisit)

                    //implement the push notifications using fcm
                    chatsListReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (!snapshot.exists()) {
                                chatsListReference.child("id").setValue(userIdVisit)
                            }
                            val chatsListReceiverReference = FirebaseDatabase.getInstance()
                                .reference
                                .child("ChatList")
                                .child(userIdVisit)
                                .child(fireBaseUser!!.uid)
                            chatsListReceiverReference.child("id").setValue(fireBaseUser!!.uid)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })

                    val reference = FirebaseDatabase.getInstance().reference
                        .child("Users").child(fireBaseUser!!.uid)
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 438 && resultCode == RESULT_OK && data != null && data!!.data != null) {
            val loadingBar = ProgressDialog(this)
            loadingBar.setMessage("Por favor espere, a enviar mensagem...")
            loadingBar.show()

            val fileUri = data.data
            val storageReference = FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref = FirebaseDatabase.getInstance().reference
            val msgPushId = ref.push().key
            val filePath = storageReference.child("$msgPushId.jpg")

            var uploadTask: StorageTask<*>
            uploadTask = filePath.putFile(fileUri!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val messageHashMap = HashMap<String, Any?>()
                    messageHashMap["sender"] = fireBaseUser!!.uid
                    messageHashMap["message"] = "enviei uma mensagem"
                    messageHashMap["receiver"] = userIdVisit
                    messageHashMap["isseen"] = false
                    messageHashMap["url"] = url
                    messageHashMap["messagerId"] = msgPushId

                    ref.child("Chats").child(msgPushId!!).setValue(messageHashMap)

                    loadingBar.dismiss()
                }
            }
        }
    }
}