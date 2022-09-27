package com.example.inirv.Home.Profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.inirv.AppLevelNavGraphDirections
import com.example.inirv.LoginAndCreateAccount.CreateAccountFlow.CAPassword.CAPasswordFragmentDirections
import com.example.inirv.R

enum class ProfileGoToScreens{
    logout,
    changePassword,
    contactSupport
}

interface ProfileFragmentDelegate{

    fun logoutUserPressed()
    fun getName(): Array<String>
    fun updateName(firstName: String, lastName: String)
    fun onStart()
}

class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private val viewModel: ViewModel
    private var delegate: ProfileFragmentDelegate? = null
    private var profileFragmentButtonPressed: Boolean = false

    init {
        viewModel = ProfileViewModel(_sharedPreferences = activity?.getSharedPreferences("omePreferences", Context.MODE_PRIVATE))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onStart() {
        super.onStart()

        profileFragmentButtonPressed = false

        if (viewModel is ProfileViewModel){

            this.delegate = viewModel

            // Setup the different observers

            // Screen
            val screensObserver = Observer<ProfileGoToScreens>{ screen ->

                val action = when(screen){
                    ProfileGoToScreens.changePassword -> CAPasswordFragmentDirections.actionGlobalCaPasswordFragment()
                    ProfileGoToScreens.logout -> AppLevelNavGraphDirections.actionGlobalLaunchFragment()
                    ProfileGoToScreens.contactSupport -> ProfileFragmentDirections.actionStoveTabActionProfileToContactSupport()
                    else -> return@Observer
                }

                activity?.findNavController(R.id.nav_host_fragment)?.navigate(action)
            }

            viewModel.goToScreens.observe(this, screensObserver)


            // First Name
            val firstNameObserver = Observer<String>{ firstName ->

                val firstNameEditText =
                    this.view?.findViewById<EditText>(R.id.profile_fragment_first_name_edit_text)
                firstNameEditText?.setText(firstName)
            }

            viewModel.firstName.observe(this, firstNameObserver)


            // Last Name
            val lastNameObserver = Observer<String>{ lastName ->

                val lastNameEditText =
                    this.view?.findViewById<EditText>(R.id.profile_fragment_last_name_edit_text)
                lastNameEditText?.setText(lastName)
            }

            viewModel.lastName.observe(this, lastNameObserver)


            // Email
            val emailObserver = Observer<String>{ email ->

                val emailTextView =
                    this.view?.findViewById<TextView>(R.id.profile_fragment_actual_email_label)
                emailTextView?.setText(email)
            }

            viewModel.email.observe(this, emailObserver)

            // Errors
            val errorObserver = Observer<String>{ error ->

                // TODO: Do something with the error

                // Reset button
                profileFragmentButtonPressed = false
            }

            viewModel.errorMessage.observe(this, errorObserver)

            viewModel.onStart()

        }

        // Setup the button on clicks
        // Logout Button
        this.view?.findViewById<ImageButton>(R.id.profile_fragment_logout_button)?.let { logoutButton ->

            logoutButton.setOnClickListener {

                if (profileFragmentButtonPressed){
                    return@setOnClickListener
                }

                profileFragmentButtonPressed = true

                this.delegate?.logoutUserPressed()
            }
        }

        // Contact Support Button
        this.view?.findViewById<ImageButton>(R.id.profile_fragment_contact_support_button)?.let { contactSupportButton ->

            contactSupportButton.setOnClickListener {

                if (profileFragmentButtonPressed){
                    return@setOnClickListener
                }

                profileFragmentButtonPressed = true

                val action = ProfileFragmentDirections.actionStoveTabActionProfileToContactSupport()
//                activity?.findNavController(R.id.nav_host_fragment)?.navigate(action)
                val localNavHost = requireParentFragment().findNavController() //findFragmentById(R.id.profile_tab_nav_host_fragment) as NavHostFragment
                localNavHost.navigate(action)
            }
        }

        // Change Password Table Row pressed
        this.view?.findViewById<TableRow>(R.id.profile_fragment_table_row_change_password)?.let { changePasswordRow ->

            changePasswordRow.setOnClickListener {

//                // TODO: Need to fix this
//                val action = CAPasswordFragmentDirections.actionGlobalCaPasswordFragment()
//                val localNavHost = requireParentFragment().findNavController() //findFragmentById(R.id.profile_tab_nav_host_fragment) as NavHostFragment
//                localNavHost.navigate(action)
            }
        }

        // First Name Edit Text
        this.view?.findViewById<EditText>(R.id.profile_fragment_first_name_edit_text)?.let { firstNameET ->

            firstNameET.setOnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    this.delegate?.updateName(
                        firstName = firstNameET.toString(),
                        lastName = delegate?.getName()?.get(1) ?: ""
                    )
                }
            }

        }

        // Last Name Edit Text
        this.view?.findViewById<EditText>(R.id.profile_fragment_last_name_edit_text)?.let { lastNameET ->

            lastNameET.setOnFocusChangeListener { view, hasFocus ->

                if (!hasFocus){
                    this.delegate?.updateName(
                        firstName = delegate?.getName()?.get(0) ?: "",
                        lastName = lastNameET.toString()
                    )
                }
            }

        }
    }
}