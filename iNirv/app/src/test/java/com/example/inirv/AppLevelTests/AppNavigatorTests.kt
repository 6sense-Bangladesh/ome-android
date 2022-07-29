package com.example.inirv.AppLevelTests

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.inirv.AppLevel.AppNavigator
import com.example.inirv.Interfaces.Coordinator
import com.example.inirv.Interfaces.CoordinatorInteractor
import com.example.inirv.Interfaces.Navigator
import org.junit.After
import org.junit.Before
import org.junit.Test

class AppNavigatorTests {

    var mock_onChildFinishedCalled: Int = 0
    var mock_onChildFinishedyNavigator: Navigator? = null
    var mock_onChildFinished = { navigator: Navigator ->

        mock_onChildFinishedCalled += 1
        mock_onChildFinishedyNavigator = navigator
    }

    class MockActivity: AppCompatActivity(){

        fun getCurrentFragment(): Fragment?{
            return this.supportFragmentManager.fragments.last()
        }
    }

    inner class MockCoordinator(override val navigator: Navigator) : Coordinator{
        override fun coordinatorInteractorFinished(coordinatorInteractor: CoordinatorInteractor) {

        }

    }

    lateinit var appNavigator: AppNavigator
    lateinit var mockActivity: MockActivity

    @Before
    fun setup(){

//        mockActivity = MockActivity()
        appNavigator = AppNavigator(
            null,
            mock_onChildFinished
        )
    }

    @After
    fun tearDown(){

        mock_onChildFinishedCalled = 0
        mock_onChildFinishedyNavigator = null
    }

    @Test
    fun test_start(){

        appNavigator.start()

//        assert(mockActivity.getCurrentFragment() != null)
//        assert(mockActivity.getCurrentFragment() is LaunchFragment)
    }
    @Test
    fun test_goToScreen(){

        val mockCoordinator = MockCoordinator(appNavigator)
        appNavigator.goToScreen(mockCoordinator)

        assert(mock_onChildFinishedCalled == 1)
        assert(mock_onChildFinishedyNavigator == appNavigator)
    }
    @Test
    fun test_(){

    }

}