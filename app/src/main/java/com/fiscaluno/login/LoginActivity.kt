package com.fiscaluno.login

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.fiscaluno.R
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.FacebookCallback
import com.facebook.login.widget.LoginButton
import com.facebook.CallbackManager
import android.content.Intent
import android.util.Log
import com.facebook.FacebookSdk
import org.json.JSONObject
import com.facebook.GraphRequest
import com.fiscaluno.helper.PreferencesManager
import com.fiscaluno.model.Student
import com.fiscaluno.view.IntroActivity
import com.fiscaluno.view.MainActivity
import org.json.JSONArray


class LoginActivity : AppCompatActivity() {

    lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FacebookSdk.sdkInitialize(applicationContext)

        setContentView(R.layout.activity_login)

        //TODO: Move login logic to presenter
        val loginButton = findViewById<LoginButton>(R.id.login_button)
        loginButton.setReadPermissions("email", "public_profile", "user_hometown")

        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                val request = GraphRequest.newMeRequest(
                        loginResult.accessToken
                ) { jsonObject, response ->
                    //TODO: Get user info here
                    Log.d("JsonObject", jsonObject.toString())
                    val student = Student()
                    student.fbId = jsonObject.get("id") as String?
                    student.birthday = jsonObject.get("birthday") as String?
                    student.city = (jsonObject.get("hometown") as JSONObject).get("name") as String? //TODO
                    student.email = jsonObject.get("email") as String?
                    student.gender = jsonObject.get("gender") as String?
                    student.name = jsonObject.get("name") as String?
                    student.nacionality = (jsonObject.get("hometown") as JSONObject).get("name") as String? //TODO
                    student.fbInstitutionName = ""
                    PreferencesManager(this@LoginActivity).user = student
                    if (PreferencesManager(this@LoginActivity).haveSeenIntro) {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    } else {
                        startActivity(Intent(this@LoginActivity, IntroActivity::class.java))
                    }
                }
                val parameters = Bundle()
                parameters.putString("fields", "id,email,name,link,education,birthday,about,gender,hometown,work")
                request.parameters = parameters
                request.executeAsync()
            }

            override fun onCancel() {
                // App code
            }

            override fun onError(exception: FacebookException) {
                // App code
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

}
