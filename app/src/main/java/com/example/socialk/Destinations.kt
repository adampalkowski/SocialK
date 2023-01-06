package com.example.socialk

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource

/**
 * Contract for information needed on every navigation destination
 */
interface Destinations {
    val icon: Int
    val route: String
    val screen: @Composable () -> Unit
}

/**
 * Social app navigation destinations
 */
object Home : Destinations {
    override val icon: Int =  R.drawable.ic_home
    override val route = "home"
    override val screen: @Composable () -> Unit = { }
}

object Map : Destinations {
    override val icon: Int = R.drawable.ic_map
    override val route = "map"
    override val screen: @Composable () -> Unit = {  }
}
object Chats : Destinations {
    override val icon: Int =R.drawable.ic_group
    override val route = "chats"
    override val screen: @Composable () -> Unit = {  }
}

object Create : Destinations {
    override val icon: Int =R.drawable.ic_add
    override val route = "create"
    override val screen: @Composable () -> Unit = {  }
}
object Profile : Destinations {
    override val icon: Int =R.drawable.ic_person
    override val route = "profile"
    override val screen: @Composable () -> Unit = {  }
}



// Screens to be displayed in the bottomBar
val bottomTabRowScreens = listOf(Home,Chats,Create,Map,Profile)
