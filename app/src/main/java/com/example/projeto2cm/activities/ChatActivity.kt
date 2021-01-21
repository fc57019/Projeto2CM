package com.example.projeto2cm.activities

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto2cm.R
import com.example.projeto2cm.adapters.ChatAdapter
import com.example.projeto2cm.entities.Chat
import com.example.projeto2cm.entities.User
import com.example.projeto2cm.interfaces.APIService
import com.example.projeto2cm.notifications.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatActivity : AppCompatActivity() {

    lateinit var recyclerViewChats: RecyclerView
    var userIdVisit: String = ""
    var fireBaseUser: FirebaseUser? = null
    var chatAdapter: ChatAdapter? = null
    var mchatList: List<Chat>? = null
    var reference: DatabaseReference? = null

    var notify = false
    var apiService: APIService? = null

    var linearLayoutManager: LinearLayoutManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recyclerViewChats = findViewById(R.id.recycler_view_chats)
        linearLayoutManager = LinearLayoutManager(applicationContext)
        //linearLayoutManager.reverseLayout = true
        //linearLayoutManager.stackFromEnd = true
        recyclerViewChats.layoutManager = linearLayoutManager

        apiService =
            Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)

        intent = intent
        userIdVisit = intent.getStringExtra("visit_id").toString()
        fireBaseUser = FirebaseAuth.getInstance().currentUser


        reference = FirebaseDatabase.getInstance().reference
            .child("Users").child(userIdVisit)
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user: User? = snapshot.getValue(User::class.java)

                val usernameChat = findViewById<TextView>(R.id.username_chat)
                usernameChat?.text = user!!.getName()
                var m = user!!.getHeight()
                Log.e("ChatActivity", m.toString())
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
            notify = true
            val msg = textMsg.text.toString()
            if (msg == "") {
                Toast.makeText(
                    this@ChatActivity,
                    "Por favor escreva uma mensagem: ",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                sendMessageToUser(fireBaseUser?.uid, userIdVisit, msg)
            }
            textMsg.setText("")
        }

        val attachFile = findViewById<ImageView>(R.id.attach_image_file_btn)

        attachFile.setOnClickListener {
            notify = true
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Selecione uma Imagem"), 438)
        }
        seenMessage(userIdVisit)


        val logout: ImageButton = findViewById(R.id.logout_btn)
        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(applicationContext, SplashScreen::class.java))
            finish()
        }

        val btnBack: ImageButton = findViewById(R.id.back_btn2)
        btnBack.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }

    }

    private fun getAllMessages(senderId: String, receiverId: String?, receiverImageUrl: String?) {
        mchatList = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                (mchatList as ArrayList<Chat>).clear()

                for (i in snapshot.children) {
                    val chat = i.getValue(Chat::class.java)

                    if (chat!!.getReceiver().equals(senderId) && chat.getSender()
                            .equals(receiverId) || chat.getReceiver()
                            .equals(receiverId) && chat.getSender().equals(senderId)
                    ) {
                        (mchatList as ArrayList<Chat>).add(chat)
                    }
                    recyclerViewChats.layoutManager = LinearLayoutManager(applicationContext)
                    chatAdapter = ChatAdapter(
                        this@ChatActivity,
                        (mchatList as ArrayList<Chat>),
                        receiverImageUrl!!
                    )
                    recyclerViewChats.adapter = chatAdapter
                }
                var x = mchatList?.size!!
                recyclerViewChats.smoothScrollToPosition(x)

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
        messageHashMap["du"] = msgKey.toString()

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

                }
            }

        val userReference = FirebaseDatabase.getInstance().reference
            .child("Users").child(fireBaseUser!!.uid)
        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (notify) {
                    sendNotification(receiverId, user!!.getName(), msg)
                }
                notify = false
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun sendNotification(receiverId: String?, userName: String?, msg: String) {
        Log.e("Send notification", "${receiverId}, $userName, $msg")
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val query = ref.orderByKey().equalTo(receiverId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val token: Token? = dataSnapshot.getValue(Token::class.java)
                    val data = Data(
                        fireBaseUser!!.uid,
                        R.mipmap.ic_launcher,
                        "$userName: $msg",
                        "New Message",
                        userIdVisit
                    )

                    val sender = Sender(data!!, token!!.getToken().toString())
                    apiService!!.sendNotification(sender)
                        .enqueue(object : Callback<MyResponse> {
                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: Response<MyResponse>
                            ) {
                                if (response.code() == 200) {
                                    if (response.body()!!.success !== 1) {
                                        Toast.makeText(
                                            this@ChatActivity,
                                            "Failed, Nothing happend.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {
                                TODO("Not yet implemented")
                            }
                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
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
                    messageHashMap["du"] = msgPushId.toString()

                    ref.child("Chats").child(msgPushId!!).setValue(messageHashMap)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                loadingBar.dismiss()
                                val reference = FirebaseDatabase.getInstance().reference
                                    .child("Users").child(fireBaseUser!!.uid)
                                reference.addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val user = snapshot.getValue(User::class.java)
                                        if (notify) {
                                            sendNotification(
                                                userIdVisit,
                                                user!!.getName(),
                                                "enviei uma mensagem"
                                            )
                                        }
                                        notify = false
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }
                                })
                            }
                        }


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