package com.example.inirv.StoveTabTests.StoveTests

import android.content.SharedPreferences
import com.example.inirv.Home.Stove.StoveViewModel
import com.example.inirv.managers.KnobManager
import com.example.inirv.managers.RESTCmdType
import com.example.inirv.managers.RESTMethodType
import com.example.inirv.managers.UserManager
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class StoveVMTests {

    @MockK
    lateinit var mock_userManager: UserManager

    @MockK
    lateinit var mock_knobManager: KnobManager

    @MockK
    lateinit var mock_sharedPreferences: SharedPreferences

    lateinit var stoveVM: StoveViewModel

    @Before
    fun setup(){

        MockKAnnotations.init(this, relaxed = true)

        stoveVM = StoveViewModel(
            mock_sharedPreferences,
            mock_knobManager,
            mock_userManager
        )

    }

    @Test
    fun test_resetAutoShutOffVars(){

        stoveVM.resetAutoShutOffVars()

        verify { mock_sharedPreferences.edit().putBoolean("autoShutOffNotifReceived", false) }
    }

    @Test
    fun test_getAllKnobs(){

        stoveVM.getAllKnobs()

        verify { mock_knobManager.sendRestCommand(
            commandType = RESTCmdType.ALLKNOBS,
            methodType = RESTMethodType.GET)
        }
    }

    @Test
    fun test_safetyLockPressedOn(){

        val isOn: Boolean = true
        val macID: String = "TestMacID"

        stoveVM.safetyLockPressed(
            isOn = isOn,
            macID = macID
        )

        val params: MutableMap<String, Any> = mutableMapOf(
            Pair("macID", macID)
        )

        verify {
            mock_knobManager.sendRestCommand(
                params = params,
                commandType = RESTCmdType.SAFETYLOCKOFF,
                methodType = RESTMethodType.POST
            )
        }
    }

    @Test
    fun test_safetyLockPressedOff(){

        val isOn: Boolean = false
        val macID: String = "TestMacID"

        stoveVM.safetyLockPressed(
            isOn = isOn,
            macID = macID
        )

        val params: MutableMap<String, Any> = mutableMapOf(
            Pair("macID", macID)
        )

        verify {
            mock_knobManager.sendRestCommand(
                params = params,
                commandType = RESTCmdType.SAFETYLOCKON,
                methodType = RESTMethodType.POST
            )
        }
    }

    @Test
    fun test_(){

    }

}