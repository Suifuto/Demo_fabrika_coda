package com.example.demofabrikacoda.data

sealed class ControllAction {
    object Start : ControllAction()
    object Stop : ControllAction()
    object Pause : ControllAction()
}