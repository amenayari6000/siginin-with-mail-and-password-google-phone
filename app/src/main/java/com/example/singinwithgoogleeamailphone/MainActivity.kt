package com.example.singinwithgoogleeamailphone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>
    private lateinit var signInButton: Button

    private val mAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser

        if (user != null) {
            val intent = Intent(this@MainActivity,MainActivity2::class.java)
            startActivity(intent)
            finish()
        } else {
            // Find the sign-in button by its ID
            signInButton = findViewById(R.id.sign_in)

            // Set click listener for the sign-in button

            signInButton.setOnClickListener {
                val signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setIsSmartLockEnabled(false)
                    .setAvailableProviders(providers)
                    .build()

                signInLauncher.launch(signInIntent)
            }
        }
    }

    private val providers = listOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.PhoneBuilder().build()
    )

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        mFirebaseAuth = FirebaseAuth.getInstance()

        signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) { result ->
            onSignInResult(result)
        }
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val  progress_sign_in:ProgressBar=findViewById(R.id.progress_sign_in)
        progress_sign_in.visibility=View.VISIBLE
        val response = result.idpResponse
        if (result.resultCode == Activity.RESULT_OK) {
            progress_sign_in.visibility=View.INVISIBLE
            // Authentication successful, redirect to home screen
            val intent = Intent(this@MainActivity,MainActivity2::class.java)
            startActivity(intent)
            finish()
        } else {
            progress_sign_in.visibility=View.INVISIBLE
            // Handle authentication failure
            if (response != null) {
                if (response.error?.errorCode == ErrorCodes.NO_NETWORK) {
                    showToast("No network. Please connect to the internet.")
                } else if (response.error?.errorCode == ErrorCodes.UNKNOWN_ERROR) {
                    showToast("Registration error. Please try again.")
                }
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        mFirebaseAuth.addAuthStateListener(mAuthStateListener)
    }

    public override fun onPause() {
        super.onPause()
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener)
    }


    private fun showToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }
}









