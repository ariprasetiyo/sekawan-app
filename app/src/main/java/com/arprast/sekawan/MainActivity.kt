package com.arprast.sekawan

import android.os.Bundle
import android.text.method.MovementMethod
import android.util.Log
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.arprast.sekawan.model.UserInterfacing
import com.arprast.sekawan.repository.AccountRepository
import com.arprast.sekawan.service.LoginService
import com.arprast.sekawan.type.GenderType
import com.arprast.sekawan.type.UserInterfaceType
import com.arprast.sekawan.util.PreferanceVariable
import com.arprastandroid.R
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textview.MaterialTextView
import com.rengwuxian.materialedittext.MaterialEditText
import io.realm.Realm
import io.realm.RealmConfiguration


open class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRealm()
        initData()

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
            val inputUsername = findViewById<MaterialEditText>(R.id.login_input_username_or_email)
            val inputPassword = findViewById<MaterialEditText>(R.id.login_input_password)
            Log.d(
                PreferanceVariable.DEBUG_NAME,
                "username : ${inputUsername.text} dan password : ${inputPassword.text}"
            )
            if (inputUsername.text!!.toString() == ("ari12345678") && inputPassword.text!!.toString() == ("ari12345678")) {
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

    private fun openSignUp(){
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.container, fragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }
}
