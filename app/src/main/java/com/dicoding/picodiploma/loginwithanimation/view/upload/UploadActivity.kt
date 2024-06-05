package com.dicoding.picodiploma.loginwithanimation.view.upload

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityUploadBinding
import com.dicoding.picodiploma.loginwithanimation.di.StateResult
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding

    private var imageUri: Uri? = null

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val viewModel by viewModels<UploadViewModel>{
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (!allPermissionGranted()) {
            requestPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }

        binding.galleryButton.setOnClickListener { gallery() }
        binding.cameraButton.setOnClickListener { camera() }
        binding.uploadButton.setOnClickListener { uploadImg(0.0, 0.0) }
        binding.locationSwitch.setOnCheckedChangeListener { _,isChecked ->
            if (isChecked) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            val locLat = location.latitude
                            val locLon = location.longitude
                            toast("Location $locLat, $locLon")
                            binding.uploadButton.setOnClickListener { uploadImg(locLat, locLon) }
                        } else {
                            toast(getString(R.string.location_unavailable))
                        }
                    }
                } else {
                    toast(getString(R.string.permission_denied_no_location))
                }
            } else {
                toast(getString(R.string.without_location))
                binding.uploadButton.setOnClickListener { uploadImg(0.0, 0.0) }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocation = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocation = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        when {
            fineLocation -> { toast(getString(R.string.using_precise_location)) }
            coarseLocation -> { toast(getString(R.string.using_approximate_location)) }
            else -> { toast(getString(R.string.permission_denied)) }
        }
    }

    private fun allPermissionGranted() =
        ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun gallery() {
        launchGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun camera() {
        imageUri = getImageUri(this)
        launchCamera.launch(imageUri)
    }

    private fun uploadImg(lat: Double, lon:Double) {
        imageUri?.let { uri ->
            val fileImage = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImg: ${fileImage.path}")
            val description = binding.descEdit.text.toString()
            val latLng = LatLng(lat, lon)
            viewModel.uploadImg(fileImage, description, lat, lon).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is StateResult.Loading -> {
                            loading(true)
                        }
                        is StateResult.Success -> {
                            if (latLng != LatLng(0.0,0.0)) {
                                toast(getString(R.string.uploaded_with_location))
                            } else {
                                toast(getString(R.string.uploaded_without_location))
                            }
                            loading(false)
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        is StateResult.Error -> {
                            toast(result.error)
                            loading(false)
                        }
                    }
                }
            }
        } ?: toast(getString(R.string.NoImg))
    }

    private val launchGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            showImg()
        } else {
            Log.d("Photo Picker", "No Media Selected")
        }
    }

    private val launchCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImg()
        }
    }

    private fun showImg() {
        imageUri?.let {
            Log.d("Image URI", "showImg: $it")
            binding.previewImage.setImageURI(it)
        }
    }

    private fun loading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}