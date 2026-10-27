package kz.itspace.fire

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kz.itspace.fire.ui.CallScreen
import kz.itspace.fire.ui.DispatchScreen
import kz.itspace.fire.ui.NewCallScreen
import kz.itspace.fire.ui.theme.FireTheme

class MainActivity : ComponentActivity() {

    private val vm: DispatchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FireTheme {
                val nav = rememberNavController()
                NavHost(nav, startDestination = "calls") {
                    composable("calls") {
                        DispatchScreen(
                            vm = vm,
                            onOpenCall = { id -> nav.navigate("call/$id") },
                            onNewCall = { nav.navigate("new") },
                        )
                    }
                    composable("call/{id}", arguments = listOf(navArgument("id") { type = NavType.IntType })) { entry ->
                        CallScreen(vm, id = entry.arguments!!.getInt("id"), onBack = { nav.navigateUp() })
                    }
                    composable("new") {
                        NewCallScreen(
                            onSave = { address, incident, phone, note ->
                                val id = vm.addAlarm(address, incident, phone, note)
                                nav.navigate("call/$id") { popUpTo("calls") }
                            },
                            onBack = { nav.navigateUp() },
                        )
                    }
                }
            }
        }
    }
}
