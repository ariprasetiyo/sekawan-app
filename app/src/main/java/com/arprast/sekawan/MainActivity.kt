package com.arprast.sekawan

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.method.MovementMethod
import android.util.Log
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.arprast.sekawan.httpClient.SekawanBEApi
import com.arprast.sekawan.httpClient.SekawanBERestAdapter
import com.arprast.sekawan.model.UserInterfacing
import com.arprast.sekawan.paymo.Request
import com.arprast.sekawan.paymo.SecurityUtils
import com.arprast.sekawan.paymo.SecurityUtils.encryptionKeyGenerator
import com.arprast.sekawan.paymo.SecurityUtils.hmacSHA256
import com.arprast.sekawan.paymo.VerifiedDomain
import com.arprast.sekawan.paymo.model.AuthCredRequest
import com.arprast.sekawan.paymo.request.GetTokenRequest
import com.arprast.sekawan.paymo.request.GetTokenRequestDetail
import com.arprast.sekawan.paymo.response.TokenResponse
import com.arprast.sekawan.repository.AccountRepository
import com.arprast.sekawan.service.LoginService
import com.arprast.sekawan.type.GenderType
import com.arprast.sekawan.type.UserInterfaceType
import com.arprast.sekawan.util.GsonHelper
import com.arprast.sekawan.util.PreferanceVariable
import com.arprast.sekawan.util.*
import com.arprastandroid.R
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textview.MaterialTextView
import com.rengwuxian.materialedittext.MaterialEditText
import io.realm.Realm
import io.realm.RealmConfiguration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

open class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val gson = GsonHelper.createGson()
    private val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
    private val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
    private var config: SharedPreferences? = null
    var sekawanBEApi: SekawanBEApi? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initConfigPreference()
        initRealm()
        initData()
        initRestAdapter()

        if (true) {
            openLogin()
        } else {
            openHome()
        }
    }

    private fun openLogin() {
        setContentView(R.layout.fragment_login)

        // sign in
        val buttonLogin = findViewById<Button>(R.id.login_sign_in)
        buttonLogin.setOnClickListener {
            val inputUsernameView = findViewById<MaterialEditText>(R.id.login_input_username_or_email)
            val inputPasswordView = findViewById<MaterialEditText>(R.id.login_input_password)
            val inputUsername = inputUsernameView.text!!.toString()
            val inputPassword = inputPasswordView.text!!.toString()
            Log.d(
                PreferanceVariable.DEBUG_NAME,"username : $inputUsername dan password : $inputPassword"
            )
            if (initGetToken(inputUsername, inputPassword)) {
                openHome()
            }
        }

        // signup
        val text = findViewById<MaterialTextView>(R.id.login_sign_up)
        text.movementMethod = (object : LoginService.TextViewLinkHandler(),
            MovementMethod {
            override fun onLinkClick(url: String?) {
                openSignUp()
            }
        })
    }

    private fun openSignUp() {
        setContentView(R.layout.fragment_signup)
        setGenderList()
    }


    private fun setGenderList() {
        val adapter = ArrayAdapter(
            this,
            R.layout.account_type_dropdown_menu,
            GenderType.values()
        )
        val editTextFilledExposedDropdown = findViewById<AutoCompleteTextView>(R.id.signup_gender)
        editTextFilledExposedDropdown.setAdapter(adapter)
    }

    private fun openHome() {
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_share,
                R.id.nav_send,
                R.id.nav_facebook,
                R.id.nav_twitter,
                R.id.nav_instagram,
                R.id.nav_youtube,
                R.id.nav_account
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun initRealm() {
        Realm.init(this)
        val configuration = RealmConfiguration.Builder()
            .name("arprast.db")
            .schemaVersion(0)
            .build()
        Realm.setDefaultConfiguration(configuration)
    }

    private fun initData() {
        val userInterfacing = UserInterfacing()
        userInterfacing.menuId = UserInterfaceType.SHOW_CREDENTIAL.stringValue
        userInterfacing.isDisabled = true
        AccountRepository().insertUpdateUserInterfacing(userInterfacing)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initConfigPreference() {
        val securePreferences = EncryptedSharedPreferences.create(
            "secure_prefs",
            mainKeyAlias,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        with(securePreferences.edit()) {
            putString(
                config_server_verified_domain,
                "{\"verifiedDomains\":[\"localhost\",\"127.0.0.0\"]}"
            )
            putString(config_server_url, "http://192.168.1.4:8083")
            putInt(config_server_request_timeout, 10000)
            putString(config_server_encryption_key, get( "qLOdOR-4VOW4YmCL6RMRxbUf1RmvcOZdkGyYl6rODu8="))
            putString(config_server_client_id, get( "6qWS-aAK0ZfNPklxo2Gi2_o232ugoEdILPKF4hf5w2U="))
            putString(config_server_client_key_pattern,get("JKSqQ0w9-tvl0CKE1YXJBg=="))
            putString( config_server_client_key, get("GHVHjia2QaEUGy45vpH9wDgvItiLDj0xhVbZVmklgiDLMghdzwoO5DGlJ5Szvpd81CHyPWBtRspCMyRxRk5Ub1hsIuuqe0P3xFLe-29RrNHeZDkS54rjFtaAGHrzImaHGcsUWPDU5e5WmdX6zff-Fw=="))
            commit()
        }

        this.config = securePreferences
    }

    private fun initRestAdapter() {
        val verifiedDomain = config!!.getString(config_server_verified_domain, null)!!
        val verifiedDomains = gson.fromJson(verifiedDomain, VerifiedDomain::class.java)
        this.sekawanBEApi =
            SekawanBERestAdapter(config!!, gson, verifiedDomains.verifiedDomains).getInterface()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun initGetToken(phoneNo: String, password: String) : Boolean {

        val requestTime = System.currentTimeMillis()
        val encryptionKey = config!!.getString(config_server_encryption_key, null)!!
        val clientId = config!!.getString(config_server_client_id, null)!!
        val clientKeyPattern = config!!.getString(config_server_client_key_pattern, null)!!
        val clientKey = config!!.getString(config_server_client_key, null)!!
        val getTokenRequest = buildGetTokenRequest(phoneNo, password, encryptionKey, requestTime)

        //async process
        val tokenResponse = sekawanBEApi!!.getToken(
            getTokenRequest.requestId,
            clientId,
            buildHMACSHA256GetTokenRequest(clientId, clientKeyPattern, clientKey, getTokenRequest),
            getTokenRequest
        )

        var isSuccessLogin = false
        tokenResponse.enqueue(object : Callback<TokenResponse?> {

            override fun onResponse(
                call: Call<TokenResponse?>,
                response: Response<TokenResponse?>
            ) {
                Log.d(PreferanceVariable.DEBUG_NAME, "token response : $response")
                isSuccessLogin = true
            }

            override fun onFailure(call: Call<TokenResponse?>, t: Throwable) {
                Log.d(PreferanceVariable.DEBUG_NAME, "error response get token", t)
            }

        })
        return isSuccessLogin
    }

    private fun buildHMACSHA256GetTokenRequest(clientId : String, clientKeyPattern : String, clientKey : String, getTokenRequest: GetTokenRequest) : String{
        val secretKeySHA256 = clientId + clientKeyPattern + clientKey
        return hmacSHA256(secretKeySHA256, gson.toJson(getTokenRequest))
    }

    private fun buildGetTokenRequest(phoneNo : String, password : String, encryptionKey: String, requestTime : Long ) : GetTokenRequest{

        val authReq = AuthCredRequest()
        authReq.phoneNo = phoneNo
        authReq.password = password
        val authReqInJson = gson.toJson(authReq).toString()

        var cred: String? = null
        val encryptionKeyFinal = encryptionKeyGenerator(encryptionKey, requestTime)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cred = SecurityUtils.encryptedAES128(encryptionKeyFinal, authReqInJson)
        }

        val getTokenRequestDetail = GetTokenRequestDetail()
        getTokenRequestDetail.cred = cred

        val getTokenRequest = GetTokenRequest()
        getTokenRequest.requestId = UUID.randomUUID().toString()
        getTokenRequest.type = Request.TYPE_GENERATE_TOKEN
        getTokenRequest.requestTime = requestTime
        getTokenRequest.body = getTokenRequestDetail
        return getTokenRequest
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun get(text : String) : String{
        return SecurityUtils.decryptAES128(CREATED_BY, text)
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.container, fragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }
}
