package com.jdock.fil_rouge.user

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.snackbar.Snackbar
import com.jdock.fil_rouge.R
import com.jdock.fil_rouge.network.Api
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import com.google.modernstorage.mediastore.FileType
import com.google.modernstorage.mediastore.MediaStoreRepository
import com.google.modernstorage.mediastore.SharedPrimary
import java.util.*

class UserInfoActivity : AppCompatActivity() {

    private val viewModel : UserInfoViewModel by viewModels()
    private val mediaStore by lazy { MediaStoreRepository(this) }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        val takePictureButton = findViewById<Button>(R.id.take_picture_button)
        takePictureButton.setOnClickListener {
            launchCameraWithPermission()
        }

        val uploadPictureButton = findViewById<Button>(R.id.upload_image_button)
        uploadPictureButton.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        val avatarImageView = findViewById<ImageView>(R.id.avatar_image_view)
        viewModel.loadUser()

        lifecycleScope.launch {
                viewModel.userInfo.collect{ user ->
                avatarImageView.load(user?.avatar) {
                    error(R.drawable.ic_launcher_background)
                }
            }
        }
    }

    private lateinit var photoUri: Uri

    /*
    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { accepted ->
            if (accepted) launchCamera()
            else showExplanation()
        }
    */


    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { accepted ->
        val view = findViewById<Button>(R.id.take_picture_button)
        if (accepted) handleImage(photoUri)
        else Snackbar.make(view, "Échec!", Snackbar.LENGTH_LONG).show()
    }

    private val permissionAndCameraLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
        // pour simplifier on ne fait rien ici, il faudra que le user re-clique sur le bouton
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) handleImage(uri)
    }

    private fun launchCameraWithPermission() {
        val camPermission = Manifest.permission.CAMERA
        val permissionStatus = checkSelfPermission(camPermission)
        val isAlreadyAccepted = permissionStatus == PackageManager.PERMISSION_GRANTED
        val isExplanationNeeded = shouldShowRequestPermissionRationale(camPermission)
        val storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        when {
            mediaStore.canWriteSharedEntries() && isAlreadyAccepted -> launchCamera()
            isExplanationNeeded -> showExplanation()
            else -> permissionAndCameraLauncher.launch(arrayOf(camPermission, storagePermission))
        }
    }

    private fun showExplanation() {
        // ici on construit une pop-up système (Dialog) pour expliquer la nécessité de la demande de permission
        AlertDialog.Builder(this)
            .setMessage("🥺 On a besoin de la caméra, vraiment! 👉👈")
            .setPositiveButton("Bon, ok") { _, _ -> launchAppSettings() }
            .setNegativeButton("Nope") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun launchAppSettings() {
        // Cet intent permet d'ouvrir les paramètres de l'app (pour modifier les permissions déjà refusées par ex)
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        // ici pas besoin de vérifier avant car on vise un écran système:
        startActivity(intent)
    }

    private fun handleImage(imageUri: Uri) {
        findViewById<ImageView>(R.id.avatar_image_view).setImageURI(imageUri)
        viewModel.updateAvatar(convert(imageUri))

    }

    private fun launchCamera() {
        lifecycleScope.launch {
            photoUri = mediaStore.createMediaUri(
                filename = "picture-${UUID.randomUUID()}.jpg",
                type = FileType.IMAGE,
                location = SharedPrimary
            ).getOrThrow()
            cameraLauncher.launch(photoUri)
        }
    }

    private fun convert(uri: Uri): MultipartBody.Part {
        return MultipartBody.Part.createFormData(
            name = "avatar",
            filename = "temp.jpeg",
            body = contentResolver.openInputStream(uri)!!.readBytes().toRequestBody()
        )
    }
}