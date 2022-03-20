package com.mkao.camerax

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.mkao.camerax.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_gallery, R.id.navigation_camera
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
    //This function handles the user’s response to the
    //permissions request

    // if return values of true, then the recreate method will reload  all conditions met
    // activity because the necessary user permissions have been granted.
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions:
        Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode, permissions,
            grantResults
        )
        if (!CameraPermissionHelper.hasCameraPermission(this) ||
            !CameraPermissionHelper.hasStoragePermission(this)
        ) {
            CameraPermissionHelper.requestPermissions(this)
        } else recreate()
    }


    //The CameraPermissionHelper object’s  permissions have been granted
    // if the user happens to deny any of the permissions (returns false) then the cameraPermissionHelper  request permissions again
    object CameraPermissionHelper {
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
        private const val READ_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE

        fun hasCameraPermission(activity: Activity): Boolean {
            return ContextCompat.checkSelfPermission(
                activity,
                CAMERA_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun hasStoragePermission(activity: Activity): Boolean {
            return ContextCompat.checkSelfPermission(
                activity,
                READ_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun requestPermissions(activity: Activity) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, CAMERA_PERMISSION)) {
                AlertDialog.Builder(activity).apply {
                    setMessage(activity.getString(R.string.permission_required))
                    setPositiveButton(activity.getString(R.string.ok)) { _, _ ->
                        ActivityCompat.requestPermissions(
                            activity,
                            arrayOf(CAMERA_PERMISSION, READ_PERMISSION),
                            1
                        )
                    }
                    show()
                }
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(CAMERA_PERMISSION, READ_PERMISSION),
                    1
                )
            }
        }
    }

    fun prepareContentValues(): ContentValues {
        val timeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())
        val imageFileName = "image_$timeStamp"
        return ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM")

        }
    }
}
