package com.example.garudasakti

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
import com.example.garudasakti.models.UpdatePasswordResponse
import com.example.garudasakti.retro.MainInterface
import com.example.garudasakti.retro.RetrofitConfig
import retrofit2.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UpdatePasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UpdatePasswordFragment : Fragment() {

    private val token: String by lazy {
        requireActivity().getSharedPreferences("user_prefs", AppCompatActivity.MODE_PRIVATE)
            .getString("auth_token", "") ?: ""
    }


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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_update_password, container, false)

        val buttonUpdatePassword = view.findViewById<Button>(R.id.buttonSaveUpdatePassword)
        buttonUpdatePassword.setOnClickListener {
            updatePassword(view)
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
         * @return A new instance of fragment UpdatePasswordFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UpdatePasswordFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun updatePassword(view: View){

        // Setup Retrofit
        val retrofit = RetrofitConfig().getRetrofitClientInstance()
        val apiInterface = retrofit.create(MainInterface::class.java)

        val currentPassword = view.findViewById<EditText>(R.id.editCurrentPassword).text.toString()
        val newPassword = view.findViewById<EditText>(R.id.editNewPassword).text.toString()
        val confirmPassword = view.findViewById<EditText>(R.id.editConfirmPassword).text.toString()

        if(confirmPassword == "" || currentPassword == "" || newPassword == ""){
            Toast.makeText(requireContext(), "Harap isi semua field diatas", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != confirmPassword) {
            Toast.makeText(requireContext(), "Ketik ulang password baru harus sama", Toast.LENGTH_SHORT).show()
            return
        }

        val passwordData = mapOf(
            "current_password" to currentPassword,
            "new_password" to newPassword,
            "confirm_password" to confirmPassword
        )

        apiInterface.updatePassword("Bearer $token", passwordData).enqueue(object :
            Callback<UpdatePasswordResponse> {
            override fun onResponse(
                call: Call<UpdatePasswordResponse>,
                response: Response<UpdatePasswordResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                        requireActivity().findViewById<View>(R.id.fragment_container_profil).visibility = View.GONE
                    }
                } else {
                    Toast.makeText(requireContext(), "Gagal memperbarui password", Toast.LENGTH_SHORT).show()
                    Log.d("Error", response.toString())
                    Log.d("Error", response.body().toString())
                    requireActivity().findViewById<View>(R.id.fragment_container_profil).visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<UpdatePasswordResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                requireActivity().findViewById<View>(R.id.fragment_container_profil).visibility = View.GONE
            }
        })

    }

}