package com.example.projeto2cm.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.projeto2cm.R
import com.example.projeto2cm.entities.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso


class ProfileFragment : Fragment() {

    private lateinit var textInputLayout: View

    var refUser: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null
    private val RequestCode = 438
    private var imageUrl: Uri? = null
    private var storageRef: StorageReference? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val steps: Array<String> = arrayOf("6 000", "10 000", "12 000")

        val adapterSteps = ArrayAdapter(
            requireContext(),
            R.layout.dropdown,
            steps
        )

        val editTextFilledExposedDropdownSteps: AutoCompleteTextView =
            view.findViewById<AutoCompleteTextView>(R.id.steps_dropdown)
        editTextFilledExposedDropdownSteps.setAdapter(adapterSteps)

        val genre: Array<String> = arrayOf("Masculino", "Feminino", "Outro")

        val adapterGenre = ArrayAdapter(
            requireContext(),
            R.layout.dropdown,
            genre
        )

        val editTextFilledExposedDropdownGenre: AutoCompleteTextView =
            view.findViewById<AutoCompleteTextView>(R.id.genre_dropdown)
        editTextFilledExposedDropdownGenre.setAdapter(adapterGenre)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUser = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        refUser!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user: User? = snapshot.getValue(User::class.java)

                    var name = view.findViewById(R.id.profile_name) as EditText?
                    val date: EditText? = view.findViewById(R.id.profile_date) as EditText?
                    val weight: EditText? = view.findViewById(R.id.profile_weight) as EditText?
                    val height: EditText? = view.findViewById(R.id.profile_height) as EditText?
                    val pic: ImageView? = view.findViewById(R.id.profile_pic) as ImageView?

                    val name1 = user?.getName()
                    val date1 = user?.getDate()
                    val weight1 = user?.getWeight()
                    val height1 = user?.getHeight()
                    val pic1 = user?.getProfile()
                    val genre1 = user?.getGenre()
                    val steps1 = user?.getSteps()

                    Picasso.get().load(pic1).into(pic)
                    name?.setText(name1)
                    date?.setText(date1)
                    weight?.setText(weight1.toString())
                    height?.setText(height1.toString())
                    if(!genre1.equals("genero")){
                        editTextFilledExposedDropdownGenre.setText(genre1,false)
                    }
                    if(!steps1.equals("0")){
                        editTextFilledExposedDropdownSteps.setText(steps1,false)
                    }
                    Log.e("USER", "$name1")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


        val save: Button = view.findViewById(R.id.save_btn)
        save.setOnClickListener {
            val name = view.findViewById(R.id.profile_name) as EditText?
            val date: EditText? = view.findViewById(R.id.profile_date) as EditText?
            val weight: EditText? = view.findViewById(R.id.profile_weight) as EditText?
            val height: EditText? = view.findViewById(R.id.profile_height) as EditText?
            val editTextFilledExposedDropdownSteps: AutoCompleteTextView =
                view.findViewById<AutoCompleteTextView>(R.id.steps_dropdown)
            val editTextFilledExposedDropdownGenre: AutoCompleteTextView =
                view.findViewById<AutoCompleteTextView>(R.id.genre_dropdown)

            val mapName = HashMap<String, Any>()
            mapName["name"] = name?.text.toString()
            refUser?.updateChildren(mapName)

            val mapDate = HashMap<String, Any>()
            mapDate["date"] = date?.text.toString()
            refUser?.updateChildren(mapDate)

            val mapWeight = HashMap<String, Any>()
            mapWeight["weight"] = weight?.text.toString()
            refUser?.updateChildren(mapWeight)

            val mapHeight = HashMap<String, Any>()
            mapHeight["height"] = height?.text.toString()
            refUser?.updateChildren(mapHeight)

            val mapSteps = HashMap<String, Any>()
            mapSteps["steps"] = editTextFilledExposedDropdownSteps.text.toString()
            refUser?.updateChildren(mapSteps)

            val mapGenre = HashMap<String, Any>()
            mapGenre["genre"] = editTextFilledExposedDropdownGenre.text.toString()
            println(mapGenre)
            println(editTextFilledExposedDropdownGenre.text.toString())
            refUser?.updateChildren(mapGenre)
        }

        val editProfilePic: ImageView = view.findViewById(R.id.edit_profile_pic)
        val profilePic: ImageView = view.findViewById(R.id.profile_pic)

        storageRef = FirebaseStorage.getInstance().reference.child("User Images")

        editProfilePic.setOnClickListener {
            changeImage()
        }

        profilePic.setOnClickListener {
            changeImage()
        }

        return view
    }

    private fun changeImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCode && resultCode == Activity.RESULT_OK && data?.data != null) {
            imageUrl = data.data
            Toast.makeText(context, "Upload...", Toast.LENGTH_LONG).show()
            uploadImage()
        }
    }

    private fun uploadImage() {
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("Uploading image, please wait...")
        progressBar.show()

        if (imageUrl != null) {
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")
            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUrl!!)
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri?>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()
                    val map = HashMap<String, Any>()
                    map["profile"] = url
                    refUser!!.updateChildren(map)
                    progressBar.dismiss()
                }
            }
        }
    }

}