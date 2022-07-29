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

//    @Test
//    fun test_turnOffPressed(){
//
//        val testMACIDs: List<String> = listOf(
//            "TestMacID1",
//            "TestMacID2",
//            "TestMacID3",
//            "TestMacID4"
//        )
//
//        val knobsList: List<Knob> = listOf(
//            Knob(
//                macID = testMACIDs[0],
//                safetyLockEnabled = false,
//                stoveID = "",
//                userID = "",
//                firmwareVersion = "",
//                stovePosition = 0,
//                ipAddress = "",
//                currLevel = -2
//            ),
//            Knob(
//                macID = testMACIDs[1],
//                safetyLockEnabled = false,
//                stoveID = "",
//                userID = "",
//                firmwareVersion = "",
//                stovePosition = 0,
//                ipAddress = "",
//                currLevel = -2
//            ),
//            Knob(
//                macID = testMACIDs[2],
//                safetyLockEnabled = false,
//                stoveID = "",
//                userID = "",
//                firmwareVersion = "",
//                stovePosition = 0,
//                ipAddress = "",
//                currLevel = -2
//            ),
//            Knob(
//                macID = testMACIDs[3],
//                safetyLockEnabled = false,
//                stoveID = "",
//                userID = "",
//                firmwareVersion = "",
//                stovePosition = 0,
//                ipAddress = "",
//                currLevel = -2
//            )
//        )
//
//        every { mock_knobManager.knobs } returns knobsList
//
//        stoveVM.turnOffPressed()
//
//        verify {
//            mock_knobManager.sendRestCommand(
//                mutableMapOf(
//                    Pair("macID",knobsList[0].mMacID),
//                    Pair("level", knobsList[0].mAngles[0])
//                ),
//                commandType = RESTCmdType.KNOBANGLE,
//                methodType = RESTMethodType.POST
//            )
//        }
//
//        verify {
//            mock_knobManager.sendRestCommand(
//                mutableMapOf(
//                    Pair("macID",knobsList[1].mMacID),
//                    Pair("level", knobsList[1].mAngles[0])
//                ),
//                commandType = RESTCmdType.KNOBANGLE,
//                methodType = RESTMethodType.POST
//            )
//        }
//
//        verify {
//            mock_knobManager.sendRestCommand(
//                mutableMapOf(
//                    Pair("macID",knobsList[2].mMacID),
//                    Pair("level", knobsList[2].mAngles[0])
//                ),
//                commandType = RESTCmdType.KNOBANGLE,
//                methodType = RESTMethodType.POST
//            )
//        }
//
//        verify {
//            mock_knobManager.sendRestCommand(
//                mutableMapOf(
//                    Pair("macID",knobsList[3].mMacID),
//                    Pair("level", knobsList[3].mAngles[0])
//                ),
//                commandType = RESTCmdType.KNOBANGLE,
//                methodType = RESTMethodType.POST
//            )
//        }
//    }

    @Test
    fun test_(){

    }

}