package com.example.inirv.Interfaces

interface Presenter: CoordinatorInteractor {
//    var onFinished: ((Presenter) -> Unit) // Lambda to tell the coordinator the presenter is done
    fun setup() //Action the presenter should take to setup its properties
//    fun finished()  // Action the presenter should tell it's coordinator it's done
}