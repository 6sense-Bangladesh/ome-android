@file:Suppress("CONTEXT_RECEIVERS_DEPRECATED", "MemberVisibilityCanBePrivate")
package com.ome.app.ui.base.navigation

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.ome.app.R
import com.ome.app.ui.dashboard.settings.add_knob.burner.SelectBurnerFragmentParams
import com.ome.app.ui.dashboard.settings.add_knob.direction.DirectionSelectionFragmentParams
import com.ome.app.ui.dashboard.settings.add_knob.wifi.ConnectToWifiParams
import com.ome.app.ui.stove.StoveSetupBurnersArgs
import com.ome.app.ui.stove.StoveSetupTypeArgs
import com.ome.app.utils.fromJson
import com.ome.app.utils.isTrue
import com.ome.app.utils.toJson
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

sealed class Screens<T>(val destination: String) {
    data object Dashboard : Screens<Unit>("dashboard")
    data object StoveBrand : Screens<Unit>("stove_brand")
    data object StoveType : Screens<StoveSetupTypeArgs>("stove_type")
    data object StoveLayout : Screens<StoveSetupBurnersArgs>("stove_layout")

    data object SelectBurnerPosition : Screens<SelectBurnerFragmentParams>("knob_position")
    data object ConnectToWifi : Screens<ConnectToWifiParams>("connect_wifi")
    data object DirectionSelection : Screens<DirectionSelectionFragmentParams>("knob_orientation")
}

object DeepNavGraph {
    const val NAV_ARG = "nav_arg"

    /** //Sample
    Screens.Dashboard.navigateFromLibraryModule(
    UserBasicInfo("54546", "Agustin Frye", 2, 1306, 2)
    )
     */
    context(Fragment)
    @SuppressLint("RestrictedApi")
    fun <T> Screens<T>.navigate(data: T? = null, popUpToInclusive: Boolean = false) {
        findNavController().navigate(
            NavDeepLinkRequest.Builder.fromUri(Uri.parse(
                getDeepLinkUrl(data)
            )).build(), NavOptions.Builder().apply {
                popUpToInclusive.isTrue {
                    findNavController().currentBackStack.value.firstOrNull()?.destination?.id?.let {
                        setPopUpTo(it, inclusive = true)
                    }
                }
                setEnterAnim(R.anim.slide_in_right)
                setExitAnim(R.anim.slide_out_left)
                setPopEnterAnim(R.anim.slide_in_left)
                setPopExitAnim(R.anim.slide_out_right)
            }.build(), null
        )
    }

    @SuppressLint("RestrictedApi")
    fun <T> Screens<T>.navigate(navController: NavController?, data: T? = null, popUpToInclusive: Boolean = false) {
        navController?.navigate(
            NavDeepLinkRequest.Builder.fromUri(Uri.parse(getDeepLinkUrl(data))).build(), NavOptions.Builder().apply {
                popUpToInclusive.isTrue {
                    navController.currentBackStack.value.firstOrNull()?.destination?.id?.let {
                        setPopUpTo(it, inclusive = true)
                    }
                }
                setEnterAnim(R.anim.slide_in_right)
                setExitAnim(R.anim.slide_out_left)
                setPopEnterAnim(R.anim.slide_in_left)
                setPopExitAnim(R.anim.slide_out_right)
            }.build(), null
        )
    }

    fun <T> Screens<T>.getDeepLinkUrl(data: T? = null): String {
        return "ome://navigation/$destination/${data?.encode() ?: "no_data"}"
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun <T> T.encode(): String {
        return Base64.UrlSafe.encode(toJson().toByteArray())
//        return URLEncoder.encode(toJson(), "UTF-8")
    }

    /** //Sample
    Screens.Dashboard.getData(arguments)?.also {
    viewModel.userBasicInfo = it
    }
     */
    @OptIn(ExperimentalEncodingApi::class)
    @Suppress("UnusedReceiverParameter")
    inline fun <reified T> Screens<T>.getData(arguments: Bundle?): T? {
        val data= arguments?.getString(NAV_ARG)?.let {
            try {
                if (it != "no_data")
                    Base64.UrlSafe.decode(it).decodeToString().fromJson<T>()
                else null
            } catch (e: Exception) { null}
        }
        return data
    }

}


