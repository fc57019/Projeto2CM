package com.example.projeto2cm.activities

import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projeto2cm.R
import com.example.projeto2cm.entities.User
import com.example.projeto2cm.fragments.distanceView
import com.example.projeto2cm.fragments.stepsView
import com.example.projeto2cm.fragments.stepsView2
import com.example.projeto2cm.utils.NavigationManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Scope
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.request.DataSourcesRequest
import com.google.android.gms.fitness.request.OnDataPointListener
import com.google.android.gms.fitness.request.SensorRequest
import com.google.android.gms.fitness.result.DataSourcesResult
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

var STEPS: Long? = 0L!!
var DISTANCE: String? = ""
var ALTURA: String? = ""

class MainActivity : AppCompatActivity(), OnDataPointListener, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {


    private var midnight: LocalTime = LocalTime.MIDNIGHT

    //private var today: LocalTime = LocalDate.now(Clock.systemDefaultZone())
    //private var todayMidnight: LocalDateTime = LocalDateTime.of(today, midnight)
    private var horaAtual: LocalTime? = null
    private val REQUEST_OAUTH = 1
    private var count: Int = 0
    private val AUTH_PENDING = "auth_state_pending"
    private var authInProgress = false
    private var mApiClient: GoogleApiClient? = null
    var refUser: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null
    var x = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUser = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        var height1 = ""
        refUser!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    val user: User? = snapshot.getValue(User::class.java)
                    height1 = user?.getHeight().toString()

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

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })



        if (!screenRotated(savedInstanceState)) {
            NavigationManager.goToHomeFragment(supportFragmentManager)
        }

        mApiClient = GoogleApiClient.Builder(this)
            .addApi(Fitness.SENSORS_API)
            .addScope(Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
            .addScope(Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build()

        val bottomNavigationView =
            findViewById<BottomNavigationView>(R.id.bottom_navigation)?.setOnNavigationItemSelectedListener {
                checkMidnight()
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


    }


    fun checkMidnight() {
        var horaAtual: String = LocalTime.now().toString() // hora atual
        var dateAtual: String = LocalDate.now().toString() // data autal
        var temp = dateAtual.split("-")
        var nextDate = temp[0] + "-" + temp[1] + "-" + (temp[2].toInt() + 1) // criar nova data

        if (horaAtual >= "00:00:00.000" && dateAtual != nextDate) {
            x = 500
            dateAtual = nextDate
        }
        Log.e("wwww1", (x).toString())
        Log.e("wwww2", (horaAtual).toString())
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


                Toast.makeText(
                    applicationContext,
                    "Field: " + field.name.toString() + " Value: " + value,
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("111111", "Field: " + field.name.toString() + " Value: " + value)
                STEPS = value.toString().toLong()
                stepsView?.text = STEPS.toString() + " Daily Steps"
                stepsView2?.text = STEPS.toString() + " Daily Steps"

                val mapDaily = HashMap<String, Any>()
                mapDaily["dailySteps"] = STEPS.toString()
                refUser?.updateChildren(mapDaily)

            }
        }
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


        /*val dataSourceRequest1 = DataSourcesRequest.Builder()
            .setDataTypes(DataType.TYPE_DISTANCE_DELTA)
            .setDataSourceTypes(DataSource.TYPE_RAW)
            .build()

        val dataSourcesResultCallback1 =
            ResultCallback<DataSourcesResult> { dataSourcesResult ->
                for (dataSource in dataSourcesResult.dataSources) {
                    if (DataType.TYPE_DISTANCE_DELTA == dataSource.dataType) {
                        registerFitnessDataListener(dataSource, DataType.TYPE_DISTANCE_DELTA)
                    }
                }
            }

       Fitness.SensorsApi.findDataSources(mApiClient, dataSourceRequest1)
            .setResultCallback(dataSourcesResultCallback1)*/
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

