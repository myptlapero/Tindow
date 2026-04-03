package com.vjpro.tindow.ui.navigation

/** All navigation destinations in the app */
sealed class TindowRoute(val route: String) {
    data object Home : TindowRoute("home")
    data object Topic : TindowRoute("topic")
    data object Input : TindowRoute("input")
    data object Swipe : TindowRoute("swipe")
    data object Result : TindowRoute("result")
}
