package com.example.projeto2cm.activities

import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.projeto2cm.R
import com.example.projeto2cm.dialogs.AchievementDialog
import com.example.projeto2cm.entities.User
import com.example.projeto2cm.fragments.*
import com.example.projeto2cm.utils.NavigationManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Scope
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.DataSourcesRequest
import com.google.android.gms.fitness.request.OnDataPointListener
import com.google.android.gms.fitness.request.SensorRequest
import com.google.android.gms.fitness.result.DataSourcesResult
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit


/*
Quando carrega num elemento da familia vamos bustsr essa pessoa a firebase e adicionamos na classe achievements esse achievement.
Depois na pagina principal metemos o array achievements na recyclerview. (Random idea)
 */

var STEPS: Long? = 0L!!
var DISTANCE: String? = ""
var ALTURA: String? = ""
var fitnessOptions: FitnessOptions = FitnessOptions.builder()
    .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE, FitnessOptions.ACCESS_READ)
    .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ).build()

class MainActivity : AppCompatActivity(), OnDataPointListener, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {


    private val REQUEST_OAUTH = 1
    private var count: Int = 0
    private val AUTH_PENDING = "auth_state_pending"
    private var authInProgress = false
    private var mApiClient: GoogleApiClient? = null
    var refUser: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //googleSignin()

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUser = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)


        if (!screenRotated(savedInstanceState)) {
            NavigationManager.goToHomeFragment(supportFragmentManager)
        }

        mApiClient = GoogleApiClient.Builder(this)
            .addApi(Fitness.HISTORY_API)
            .addApi(Fitness.RECORDING_API)
            .addApi(Fitness.SENSORS_API)
            .addScope(Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
            .addScope(Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build()

        val bottomNavigationView =
            findViewById<BottomNavigationView>(R.id.bottom_navigation)?.setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.ic_health -> {
                        NavigationManager.goToHealthFragment(supportFragmentManager)
                    }
                    R.id.ic_messages -> {
                        NavigationManager.goToMessageFragment(supportFragmentManager)
                    }
                    R.id.ic_home -> {
                        NavigationManager.goToHomeFragment(supportFragmentManager)
                    }
                    R.id.ic_profile -> {
                        NavigationManager.goToProfileFragment(supportFragmentManager)
                    }
                    R.id.ic_settings -> {
                        NavigationManager.goToSettingFragment(supportFragmentManager)
                    }
                }
                true
            }

        val logout: ImageButton = findViewById(R.id.logout_btn)
        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(applicationContext, SplashScreen::class.java))
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            createAchievement()
        }

    }


    private fun createAchievement() {
        AchievementDialog().show(supportFragmentManager, "AchievementDialog")
    }

    fun googleSignin() {
        if (!GoogleSignIn.hasPermissions(
                GoogleSignIn.getLastSignedInAccount(this),
                fitnessOptions
            )
        ) {
            GoogleSignIn.requestPermissions(
                this,  // your activity
                2,
                GoogleSignIn.getLastSignedInAccount(this),
                fitnessOptions
            )
        } else {

        }
    }

    override fun onStart() {
        super.onStart()
        mApiClient!!.connect()
    }

    override fun onStop() {
        super.onStop()
        Fitness.SensorsApi.remove(mApiClient, this)
            .setResultCallback { status ->
                if (status.isSuccess) {
                    mApiClient!!.disconnect()
                }
            }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(AUTH_PENDING, authInProgress)
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    private fun screenRotated(savedInstanceState: Bundle?): Boolean {
        return savedInstanceState != null
    }


    override fun onDataPoint(p0: DataPoint) {
        for (field in p0.dataType.fields) {
            val value: Value = p0.getValue(field)
            runOnUiThread {
                val startTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault())
                val endTime = LocalDateTime.now().atZone(ZoneId.systemDefault())

                val datasource = DataSource.Builder()
                    .setAppPackageName("com.google.android.gms")
                    .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                    .setType(DataSource.TYPE_DERIVED)
                    .setStreamName("estimated_steps")
                    .build()

                val request = DataReadRequest.Builder()
                    .aggregate(datasource)
                    .bucketByTime(1, TimeUnit.DAYS)
                    .setTimeRange(
                        startTime.toEpochSecond(),
                        endTime.toEpochSecond(),
                        TimeUnit.SECONDS
                    )
                    .build()

                Fitness.getHistoryClient(
                    this,
                    GoogleSignIn.getAccountForExtension(this, fitnessOptions)
                )
                    .readData(request)
                    .addOnSuccessListener { response ->
                        val totalSteps = response.buckets
                            .flatMap { it.dataSets }
                            .flatMap { it.dataPoints }
                            .sumBy { it.getValue(Field.FIELD_STEPS).asInt() }
                        Log.e("TAG", "Total steps: $totalSteps")
                        Log.e("111111", "Field: " + field.name.toString() + " Value: " + value)

                        STEPS = totalSteps.toLong()
                        stepsView?.text = STEPS.toString() + " Daily Steps"
                        stepsView2?.text = STEPS.toString() + " Daily Steps"

                        passosDados?.text =
                            STEPS.toString() + " / " + progressMaxTemp!!.toInt().toString()
                        getDailyDistance()
                    }
            }
        }

    }

    private fun getDailyDistance() {
        refUser!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user: User? = snapshot.getValue(User::class.java)
                    var height1 = user?.getHeight().toString()
                    Log.e("height1", height1)
                    Log.e(
                        "height1",
                        (height1 == "0.0" || height1 == "0" || height1 == "").toString()
                    )


                    if (height1 == "0.0" || height1 == "0" || height1 == "") {
                        var passos = STEPS.toString().toDouble()
                        var calc: Double = (passos * (0.43 * 1.73))
                        var convertToKm: Double = calc / 1000
                        var convert =
                            BigDecimal(convertToKm).setScale(3, RoundingMode.HALF_UP)
                        if (convert.toFloat() < 1.0) {
                            var mapdistance = HashMap<String, Any>()
                            mapdistance["distance"] = convert.toString()
                            refUser?.updateChildren(mapdistance)
                            DISTANCE = convert.toString()
                            //distanceView?.text = DISTANCE.toString() + " Km"
                            Log.e(
                                "(height1 == \"0.0\" || height1 == \"0\" || height1 == \"\")",
                                DISTANCE.toString()
                            )
                            distanceView?.text = DISTANCE.toString() + " Km"
                        } else {
                            var convert2 = BigDecimal(convertToKm).setScale(1, RoundingMode.HALF_UP)
                            var mapdistance = HashMap<String, Any>()
                            mapdistance["distance"] = convert.toString()
                            refUser?.updateChildren(mapdistance)
                            DISTANCE = convert2.toString()
                            distanceView?.text = DISTANCE.toString() + " Km"
                        }
                    } else {
                        var passos = STEPS.toString().toDouble()
                        var calc: Double = (passos * (0.43 * height1.toDouble()!!))
                        var convertToKm: Double = calc / 1000
                        var convert =
                            BigDecimal(convertToKm).setScale(3, RoundingMode.HALF_UP)

                        if (convert.toFloat() < 1.0) {
                            var mapdistance = HashMap<String, Any>()
                            mapdistance["distance"] = convert.toString()
                            refUser?.updateChildren(mapdistance)
                            DISTANCE = convert.toString()
                            //distanceView?.text = DISTANCE.toString() + " Km"
                            Log.e("else", DISTANCE.toString())
                            distanceView?.text = DISTANCE.toString() + " Km"
                        } else {
                            var convert2 = BigDecimal(convertToKm).setScale(1, RoundingMode.HALF_UP)
                            var mapdistance = HashMap<String, Any>()
                            mapdistance["distance"] = convert.toString()
                            refUser?.updateChildren(mapdistance)
                            DISTANCE = convert2.toString()
                            distanceView?.text = DISTANCE.toString() + " Km"
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onConnected(p0: Bundle?) {
        val dataSourceRequest = DataSourcesRequest.Builder()
            .setDataTypes(DataType.TYPE_STEP_COUNT_CUMULATIVE)
            .setDataSourceTypes(DataSource.TYPE_RAW)
            .build()

        val dataSourcesResultCallback =
            ResultCallback<DataSourcesResult> { dataSourcesResult ->
                for (dataSource in dataSourcesResult.dataSources) {
                    if (DataType.TYPE_STEP_COUNT_CUMULATIVE == dataSource.dataType) {
                        registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_CUMULATIVE)
                    }
                }
            }

        Fitness.SensorsApi.findDataSources(mApiClient, dataSourceRequest)
            .setResultCallback(dataSourcesResultCallback)

        Fitness.getRecordingClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
            .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
            .addOnSuccessListener {
                Log.i("TAG", "Subscription was successful!")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "There was a problem subscribing ", e)
            }

        Fitness.getHistoryClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
            .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener { result ->
                val totalSteps =
                    result.dataPoints.firstOrNull()?.getValue(Field.FIELD_STEPS)?.asInt() ?: 0
                // Do something with totalSteps
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "There was a problem getting steps.", e)
            }
    }

    private fun registerFitnessDataListener(dataSource: DataSource, dataType: DataType) {
        val request = SensorRequest.Builder()
            .setDataSource(dataSource)
            .setDataType(dataType)
            .setSamplingRate(3, TimeUnit.SECONDS)
            .build()

        Fitness.SensorsApi.add(mApiClient, request, this)
            .setResultCallback { status ->
                if (status.isSuccess) {
                    Log.e("GoogleFit", "SensorApi successfully added")
                }
            }
    }


    override fun onConnectionSuspended(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        if (!authInProgress) {
            try {
                authInProgress = true
                p0.startResolutionForResult(this@MainActivity, REQUEST_OAUTH)
            } catch (e: SendIntentException) {
            }
        } else {
            Log.e("GoogleFit", "authInProgress")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_OAUTH) {
            authInProgress = false
            if (resultCode == RESULT_OK) {
                if (!mApiClient!!.isConnecting && !mApiClient!!.isConnected) {
                    mApiClient!!.connect()
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.e("GoogleFit", "RESULT_CANCELED")
            }
        } else {
            Log.e("GoogleFit", "requestCode NOT request_oauth")
        }
    }

}

