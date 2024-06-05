package com.dicoding.picodiploma.loginwithanimation.view.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailBinding
import com.dicoding.picodiploma.loginwithanimation.di.StateResult
import com.dicoding.picodiploma.loginwithanimation.retrofit.response.Story
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory

class DetailActivity : AppCompatActivity() {
    private val viewModel by viewModels<DetailViewModel>{
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra("EXTRA_ID").toString()

        viewModel.getDetail(id).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is StateResult.Loading -> {
                        loading(true)
                    }
                    is StateResult.Success -> {
                        loading(false)
                        result.data.message?.let { toast(it) }
                        result.data.story?.let { setDetail(it) }
                    }
                    is StateResult.Error -> {
                        loading(false)
                        result.error
                    }
                }
            }
        }
    }

    private fun setDetail(user: Story) {
        binding.detailName.text = user.name
        binding.detailDesc.text = user.description
        Glide.with(binding.root.context)
            .load(user.photoUrl)
            .into(binding.detailPhoto)
    }

    private fun loading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}