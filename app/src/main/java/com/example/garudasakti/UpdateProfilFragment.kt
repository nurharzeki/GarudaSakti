package com.example.garudasakti

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.example.garudasakti.models.UpdateProfilResponse
import com.example.garudasakti.retro.MainInterface
import com.example.garudasakti.retro.RetrofitConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UpdateProfilFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UpdateProfilFragment : Fragment() {

    private val token: String by lazy {
        requireActivity().getSharedPreferences("user_prefs", AppCompatActivity.MODE_PRIVATE)
            .getString("auth_token", "") ?: ""
    }
    private val customer_name: String by lazy {
        requireActivity().getSharedPreferences("user_prefs", AppCompatActivity.MODE_PRIVATE)
            .getString("customer_name", "") ?: ""
    }
    private val customer_username: String by lazy {
        requireActivity().getSharedPreferences("user_prefs", AppCompatActivity.MODE_PRIVATE)
            .getString("customer_username", "") ?: ""
    }
    private val customer_email: String by lazy {
        requireActivity().getSharedPreferences("user_prefs", AppCompatActivity.MODE_PRIVATE)
            .getString("customer_email", "") ?: ""
    }

    private lateinit var editTextName: EditText
    private lateinit var editTextUsername: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var buttonSave: Button

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_update_profil, container, false)

        editTextName = view.findViewById(R.id.editNamaUpdateProfil)
        editTextUsername = view.findViewById(R.id.editUsernameUpdateProfil)
        editTextEmail = view.findViewById(R.id.editEmailUpdateProfil)
        buttonSave = view.findViewById(R.id.buttonSimpanUpdateProfil)

        editTextName.setText(customer_name)
        editTextUsername.setText(customer_username)
        editTextEmail.setText(customer_email)

        buttonSave.setOnClickListener {
            updateProfil()
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UpdateProfilFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UpdateProfilFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun updateProfil() {
        val retrofit = RetrofitConfig().getRetrofitClientInstance()
        val apiInterface = retrofit.create(MainInterface::class.java)

        val profilData = mapOf(
            "name" to editTextName.text.toString(),
            "username" to editTextUsername.text.toString(),
            "email" to editTextEmail.text.toString()
        )

        apiInterface.updateProfil("Bearer $token", profilData).enqueue(object :
            Callback<UpdateProfilResponse> {
            override fun onResponse(
                call: Call<UpdateProfilResponse>,
                response: Response<UpdateProfilResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.errors == null) {
                            // Jika update berhasil, tutup fragment
                            val userName = response.body()?.data?.name
                            val sharedPreferences = requireContext().getSharedPreferences("user_prefs", AppCompatActivity.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("customer_name", userName)
                            editor.apply()
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                            parentFragmentManager.popBackStack()

                            val intent = Intent(requireContext(), ProfilActivity::class.java)
                            startActivity(intent)

                            requireActivity().findViewById<View>(R.id.fragment_container_profil).visibility = View.GONE
                        } else {
                            // Tampilkan error dari server
                            it.errors.forEach { (field, errors) ->
                                Toast.makeText(requireContext(), errors.joinToString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Gagal memperbarui profil : ${response.code()}", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                    Log.d("UpdateProfilResponse", response.toString())
                    Log.d("UpdateProfilBody", response.body().toString())
                }
            }

            override fun onFailure(call: Call<UpdateProfilResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



}