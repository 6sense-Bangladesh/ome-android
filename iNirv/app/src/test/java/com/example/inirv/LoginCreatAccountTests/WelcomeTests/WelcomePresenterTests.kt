package com.example.inirv.LoginCreatAccountTests.WelcomeTests

class WelcomePresenterTests {

//    class MockWelcomePresenterDelegate: WelcomePresenterDelegate{
//
//        override fun showUpdateAlertView(versionNumber: String) {
//
//        }
//
//    }
//
//    class MockWelcomeCoordinator(navigator: Navigator) : WelcomeCoordinator(navigator) {
//
//        var coordinatorInteractorFinishedCalled: Int = 0
//        var coordinatorInteractorFinishedCoordinatorInteractor: CoordinatorInteractor? = null
//        override fun coordinatorInteractorFinished(coordinatorInteractor: CoordinatorInteractor) {
//            coordinatorInteractorFinishedCalled += 1
//            coordinatorInteractorFinishedCoordinatorInteractor = coordinatorInteractor
//        }
//    }
//
//    lateinit var welcomePresenter: WelcomePresenter
//    lateinit var mockWelcomeCoordinator: WelcomeCoordinator
//
//    @MockK
//    lateinit var mockWC: WelcomeCoordinator
//
//    @Before
//    fun setup(){
//
//        MockKAnnotations.init(this)
//
//        mockWelcomeCoordinator = MockWelcomeCoordinator(navigator = WelcomeNavigator(null, null))
//        welcomePresenter = WelcomePresenter(onFinished = mockWelcomeCoordinator::coordinatorInteractorFinished)
//    }
//
//    @After
//    fun tearDown(){
//
//    }
//
//    @Test
//    fun test_setup(){
//
//        welcomePresenter = WelcomePresenter(
//            _btnPressed = true,
//            _loginBtnPressed = true,
//            onFinished = mockWelcomeCoordinator::coordinatorInteractorFinished)
//
//        welcomePresenter.setup()
//
//        assert(!welcomePresenter.btnPressed)
//        assert(!welcomePresenter.loginBtnPressed)
//
//    }
//
//    @Test
//    fun test_buttonPressedLogin(){
//
//        welcomePresenter.buttonPressed(true)
//
//        assert(welcomePresenter.btnPressed)
//        assert((mockWelcomeCoordinator as MockWelcomeCoordinator).coordinatorInteractorFinishedCalled == 1)
//        assert((mockWelcomeCoordinator as MockWelcomeCoordinator)
//            .coordinatorInteractorFinishedCoordinatorInteractor == welcomePresenter)
//
//    }
//
//    @Test
//    fun test_buttonPressedSignUp(){
//
//        welcomePresenter.buttonPressed(false)
//
//        assert(welcomePresenter.btnPressed)
//        assert((mockWelcomeCoordinator as MockWelcomeCoordinator).coordinatorInteractorFinishedCalled == 1)
//        assert((mockWelcomeCoordinator as MockWelcomeCoordinator)
//            .coordinatorInteractorFinishedCoordinatorInteractor == welcomePresenter)
//    }
//
//    @Test
//    fun test_(){
//
//    }
}