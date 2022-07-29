package com.example.inirv.Interfaces

interface CoordinatorInteractor {

    var onFinished: ((CoordinatorInteractor) -> Unit)? // Lambda to tell the coordinator the presenter is done
}