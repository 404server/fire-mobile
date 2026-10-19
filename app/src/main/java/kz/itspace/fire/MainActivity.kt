package kz.itspace.fire

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import kz.itspace.fire.ui.DispatchScreen
import kz.itspace.fire.ui.theme.FireTheme

class MainActivity : ComponentActivity() {

    private val vm: DispatchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FireTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DispatchScreen(vm, Modifier.padding(innerPadding))
                }
            }
        }
    }
}
