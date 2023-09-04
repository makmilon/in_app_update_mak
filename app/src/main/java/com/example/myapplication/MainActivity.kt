package com.example.myapplication

import android.content.Intent
import android.content.IntentSender
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.OnSuccessListener
import com.google.android.play.core.tasks.Task
import java.lang.RuntimeException

class MainActivity : AppCompatActivity() {


    companion object {
        const val UPDATE_CODE = 22
    }

    private lateinit var appUpdateManager: AppUpdateManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inAppUpdate()

    }

    //start in appUpdate

    private fun inAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask: Task<AppUpdateInfo> = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener(OnSuccessListener<AppUpdateInfo> { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.FLEXIBLE,
                        this,
                        UPDATE_CODE
                    )
                } catch (e: IntentSender.SendIntentException) {
                    throw RuntimeException(e)
                }
            }
        })

        appUpdateManager.registerListener(listener)
    }

    private val listener = InstallStateUpdatedListener { installState ->
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            popUp()
        }
    }

    private fun popUp() {
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content),
            "App Update Almost Done",
            Snackbar.LENGTH_INDEFINITE
        )

        snackbar.setAction("Reload") {
            appUpdateManager.completeUpdate()
        }
        snackbar.setTextColor(Color.parseColor("#FF0000"))
        snackbar.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == UPDATE_CODE) {
            if (resultCode != RESULT_OK) {
                // Handle the failure case if needed
            }
        }
    }

    //end in appUpdate
}