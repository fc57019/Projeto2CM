package com.example.projeto2cm.activities

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso

class ChatActivity : AppCompatActivity() {

    lateinit var recyclerViewChats: RecyclerView
    var userIdVisit: String = ""
    var fireBaseUser: FirebaseUser? = null
    var chatAdapter: ChatAdapter? = null
    var mchatList: List<Chat>? = null
    var reference: DatabaseReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recyclerViewChats = findViewById(R.id.recycler_view_chats)
        recyclerViewChats.setHasFixedSize(true)
        var linearLayoutManager = LinearLayoutManager(applicationContext)
        recyclerViewChats.layoutManager = linearLayoutManager


        intent = intent
        userIdVisit = intent.getStringExtra("visit_id").toString()
        fireBaseUser = FirebaseAuth.getInstance().currentUser


        reference = FirebaseDatabase.getInstance().reference
            .child("Users").child(userIdVisit)
        reference!!.addValueEventListener(object : ValueEventListener {
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
                Toast.makeText(
                    this@ChatActivity,
                    "Por favor escreva uma mensagem: ",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                sendMessageToUser(fireBaseUser?.uid, userIdVisit, msg)
                Log.e("dsfsdfsfsfsf", "$msg")
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
        seenMessage(userIdVisit)
    }

    private fun getAllMessages(senderId: String, receiverId: String?, receiverImageUrl: String?) {
        mchatList = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")
        Log.e("reference", reference.toString())

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                Log.e("arraSize antes do clear", mchatList!!.size.toString())
                (mchatList as ArrayList<Chat>).clear()
                Log.e("arraSize depois do clear", mchatList!!.size.toString())

                for (i in snapshot.children) {
                    val chat = i.getValue(Chat::class.java)
                    Log.e(
                        "condicao1", "${chat!!.getReceiver().equals(senderId)}"
                    )
                    Log.e(
                        "condicao2", "${chat.getSender().equals(receiverId)}"
                    )
                    Log.e(
                        "condicao3", "${chat.getReceiver().equals(receiverId)}"
                    )
                    Log.e(
                        "condicao4", "${chat.getSender().equals(senderId)}"
                    )
                    Log.e(
                        "chat!!.getReceiver()", "${chat!!.getReceiver()} lolo"
                    )
                    Log.e(
                        "senderId", "${senderId}"
                    )
                    Log.e(
                        "chat.getSender()", "${chat.getSender()} lolo"
                    )
                    Log.e(
                        "receiverId", "${receiverId}"
                    )


                    if (chat!!.getReceiver().equals(senderId) && chat.getSender()
                            .equals(receiverId) || chat.getReceiver()
                            .equals(receiverId) && chat.getSender().equals(senderId)
                    ) {
                        (mchatList as ArrayList<Chat>).add(chat)
                    }
                    Log.e("array Size", mchatList!!.size.toString())
                    //Log.e("array", "${mchatList.toString()}")
                    recyclerViewChats.layoutManager =
                        LinearLayoutManager(applicationContext)
                    chatAdapter = ChatAdapter(
                        applicationContext,
                        (mchatList as ArrayList<Chat>),
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

    var seenListener: ValueEventListener? = null
    private fun seenMessage(userId: String) {
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")
        seenListener = reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    val chat = i.getValue(Chat::class.java)
                    if (chat!!.getReceiver().equals(fireBaseUser!!.uid) && chat!!.getSender()
                            .equals(userId)
                    ) {
                        val hashMap = HashMap<String, Any>()
                        hashMap["isseen"] = true
                        i.ref.updateChildren(hashMap)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onPause() {
        super.onPause()
        reference!!.removeEventListener(seenListener!!)
    }
}