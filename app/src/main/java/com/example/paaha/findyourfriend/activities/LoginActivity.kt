package com.example.paaha.findyourfriend.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_login.*
import android.Manifest.permission
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import com.example.paaha.findyourfriend.R
import com.example.paaha.findyourfriend.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class LoginActivity : AppCompatActivity() {

    private val REQUEST_PERMISSIONS_CODE = 100

    private var mAuth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()
    }

    override fun onStart() {
        super.onStart()
        checkUser()
    }

    private fun init() {
        mAuth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener {
            if (isValid()) {
                logIn()
            }
        }

        registrationTextView.setOnClickListener {
            val intent = RegistrationActivity.newIntent(this)
            startActivity(intent)
        }

        logOutButton.setOnClickListener {
            logOut()
        }

        mapButton.setOnClickListener {
            if (permissionIsGranted(permission.ACCESS_COARSE_LOCATION) || permissionIsGranted(permission.ACCESS_FINE_LOCATION)) {
                startActivity(MapsActivity.newIntent(this))
            } else {
                requestGeoPermissions()
            }
        }
    }

    private fun logIn() {
        mAuth?.signInWithEmailAndPassword(emailEditText.text.toString().trim(), passwordEditText.text.toString().trim())
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    checkUser()
                } else {
                    isFailed(it)
                }
            }
    }

    private fun logOut() {
        mAuth?.signOut()
        updateUI(null)
    }

    private fun updateUI(user: User?) {
        if (user != null) {

            nameTextView.text = user.name
            emailTextView.text = user.email

            authorizedLayout.visibility = View.GONE
            notAuthorizedLayout.visibility = View.VISIBLE
        } else {
            authorizedLayout.visibility = View.VISIBLE
            notAuthorizedLayout.visibility = View.GONE
        }
    }

    private fun isValid(): Boolean {
        var result = true
        if (!emailEditText.text.toString().matches("[a-zA-Z0-9]+(.[a-zA-Z0-9]*)*@[a-zA-Z0-9]*.[a-zA-Z]{2,}".toRegex())) {
            emailEditText.error = getString(R.string.not_valid_email)
            result = false
        } else {
            emailEditText.error = null
        }
        if (passwordEditText.text.toString().isEmpty()) {
            passwordEditText.error = getString(R.string.input_password)
            result = false
        } else {
            passwordEditText.error = null
        }
        return result
    }


    private fun checkGeoPermissions() =
        (!permissionIsGranted(permission.ACCESS_FINE_LOCATION)
                && !permissionIsGranted(permission.ACCESS_COARSE_LOCATION))

    private fun permissionIsGranted(permissions: String) =
        ContextCompat.checkSelfPermission(this, permissions) == PackageManager.PERMISSION_GRANTED

    private fun requestGeoPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,permission.ACCESS_FINE_LOCATION)
            || ActivityCompat.shouldShowRequestPermissionRationale(this, permission.ACCESS_FINE_LOCATION)
        ) {

            AlertDialog.Builder(this)
                .setTitle("We can't show map without your locations")
                .setPositiveButton("Ok") { dialog, _ -> mapButton.callOnClick()}
                .setNegativeButton("cancel") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()

        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(
                    arrayOf(
                        permission.ACCESS_FINE_LOCATION,
                        permission.ACCESS_COARSE_LOCATION
                    ), REQUEST_PERMISSIONS_CODE
                )
            }
        }
    }

    private fun runtimePermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= 23 && checkGeoPermissions()) {
            requestPermissions(
                arrayOf(
                    permission.ACCESS_FINE_LOCATION,
                    permission.ACCESS_COARSE_LOCATION
                ), REQUEST_PERMISSIONS_CODE
            )

            return true
        }
        return false
    }



    private fun checkUser(){
        currentUser = FirebaseAuth.getInstance()?.currentUser
        if (currentUser != null) {
            val ref = FirebaseDatabase.getInstance().getReference("/users").child(currentUser!!.uid)
            var user: User?
            ref.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onCancelled(p0: DatabaseError) { }

                override fun onDataChange(p0: DataSnapshot) {
                    user = p0.getValue(User::class.java)
                    user?.let { updateUI(user) }
                }
            })

        } else {
            updateUI(null)
        }
    }


    private fun isFailed(task: Task<AuthResult>) {
        Toast.makeText(this, getString(R.string.failed_login), Toast.LENGTH_SHORT).show()
        try {
            if (task.exception != null) {
                throw task.exception!!
            }
        } catch (e: FirebaseAuthWeakPasswordException) {
            passwordEditText.error = e.message
            passwordEditText.requestFocus()
        } catch (e: FirebaseAuthUserCollisionException) {
            emailEditText.error = e.message
            emailEditText.requestFocus()
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            emailEditText.error = e.message
            emailEditText.requestFocus()
        } catch (e: Exception) {
            Log.e(RegistrationActivity::class.java.name, e.message)
        }
    }

    companion object {
        fun newIntent(packageContext: Context): Intent {
            val intent = Intent(packageContext, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            return intent
        }
    }
}
