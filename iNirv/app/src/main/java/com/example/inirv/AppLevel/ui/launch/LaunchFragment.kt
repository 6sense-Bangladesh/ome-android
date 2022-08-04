package com.example.inirv.AppLevel.ui.launch

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import com.example.inirv.AppLevel.AppNavigatorScreen
import com.example.inirv.R

class LaunchFragment(
    val _viewModel: ViewModel? = null
) : Fragment() {

    companion object {
//        fun newInstance(_viewModel: ViewModel?) = LaunchFragment(_viewModel = _viewModel)
        fun newInstance()= LaunchFragment()
    }

    private var viewModel: ViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        viewModel = LaunchViewModel(
            onFinished= null,
            _sharedPreferences = activity?.getSharedPreferences("omePreferences", Context.MODE_PRIVATE))

        val navController = activity?.findNavController(R.id.nav_host_fragment)
//        val navController = this.findNavController()

        val screenObserver = Observer<AppNavigatorScreen>{ screen ->

            // Determine what fragment to go to
            val fragment: Fragment?
//            val action = LaunchFragmentDirections.actionLaunchFragmentToLoginNavGraph()
//            navController.navigate(action)
            val action: NavDirections
            when(screen){
                AppNavigatorScreen.welcome -> {

                    action = LaunchFragmentDirections.actionLaunchFragmentToLoginNavGraph()
                }
                AppNavigatorScreen.home -> {

                    action = LaunchFragmentDirections.actionLaunchFragmentToOuterStoveTabNavGraph()
                }
                AppNavigatorScreen.knobInstallationOne -> {

                    action = LaunchFragmentDirections.actionLaunchFragmentToSetupANewDeviceNavGraph()
                }
                AppNavigatorScreen.stoveBrand -> {

                    action = LaunchFragmentDirections.actionLaunchFragmentToInitialSetupNavGraph()
                }
                else -> {
                    action = LaunchFragmentDirections.actionLaunchFragmentToLoginNavGraph()
                }
            }

            navController?.navigate(action)

//            activity?.supportFragmentManager?.beginTransaction()?.replace(
//                R.id.container
//                , fragment
//            )?.commitNow()
        }

        (viewModel as LaunchViewModel).screen.observe(viewLifecycleOwner, screenObserver)

        return inflater.inflate(R.layout.fragment_launch, container, false)
    }

    override fun onStart() {
        super.onStart()

        if (viewModel is LaunchViewModel){

            // Start the view model
            (viewModel as LaunchViewModel).setup()
        }
    }

}