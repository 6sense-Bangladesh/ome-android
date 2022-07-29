package com.example.inirv.Interfaces

interface Coordinator {
    val navigator: Navigator
    fun coordinatorInteractorFinished(coordinatorInteractor: CoordinatorInteractor)
}