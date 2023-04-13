/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.wearosiot.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.material.*
import com.example.wearosiot.R
import com.example.wearosiot.presentation.theme.WearosiotTheme
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class MainActivity : ComponentActivity() {
    var tmp =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
            FirebaseApp.initializeApp(this)
        }
    }
    
    object NavRoute {
        const val Inicio = "Inicio"
        const val SCREEN_2 = "screen2"
        const val SCREEN_3 = "screen3"
    }
    
    @Composable
    fun WearApp() {
        WearosiotTheme {
            val listState = rememberScalingLazyListState()
            Scaffold(timeText = {
                if (!listState.isScrollInProgress) {
                    TimeText()
                    
                }
            },
                vignette = {
                    Vignette(vignettePosition = VignettePosition.Top)
                },
                positionIndicator = {
                    PositionIndicator(scalingLazyListState = listState)
                }
            ) {
                ScalingLazyColumn(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                )
                {
                    
                    item {
                        val navController = rememberSwipeDismissableNavController()
                        SwipeDismissableNavHost(
                            navController = navController,
                            startDestination = NavRoute.Inicio
                        ) {
                            composable(NavRoute.Inicio) {
                                Inicio(navController)
                            }
                            composable(NavRoute.SCREEN_2) {
                                Screen2(navigation = navController)
                            }
                            composable(NavRoute.SCREEN_3) {
                                Screen3(navigation = navController)
                            }
                        }
                        
                    }
                }
            }
        }
    }
    
    @Composable
    fun Inicio(navigation: NavController) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(35.dp))
            Spacer(modifier = Modifier.height(5.dp))
            Chip(
                label = { Text(text = "Led") },
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                onClick = { navigation.navigate("screen2") },
                colors = ChipDefaults.imageBackgroundChipColors(
                    backgroundImagePainter = painterResource(id = R.drawable.lamp)
                )
            )
            Chip(
                label = { Text(text = "Temperatura") },
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                onClick = { navigation.navigate("screen3") },
                colors = ChipDefaults.imageBackgroundChipColors(
                    backgroundImagePainter = painterResource(id = R.drawable.temp)
                )
            )
            Spacer(modifier = Modifier.height(35.dp))
        }
    }
    
    @Composable
    fun Screen3(navigation: NavController) {
        getTemperature()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            
            Spacer(modifier = Modifier.height(35.dp))
            Text(text = "Temperatura")
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = tmp, textAlign = TextAlign.Center)
            print("TEMPERATURA"+tmp)
        }
    }
    
    @Composable
    fun Screen2(navigation: NavController) {
        
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {
            Button(onClick = { turnOnLed() }) {
                Text(text = "ON")
                
            }
            Button(onClick = { turnOffLed() }) {
                Text(text = "OFF")
                
            }
            
            
        }
    }
    
    fun turnOnLed() {
        val database = Firebase.database("https://esp8266-demo-9c927-default-rtdb.firebaseio.com/")
        val myRef = database.getReference("IoT1/FirebaseIOT/")
        myRef.child("Led_Status").setValue("1")
    }
    
    fun turnOffLed() {
        val database = Firebase.database("https://esp8266-demo-9c927-default-rtdb.firebaseio.com/")
        val myRef = database.getReference("IoT1/FirebaseIOT/")
        myRef.child("Led_Status").setValue("0")
    }
    fun getTemperature() {
        val database = Firebase.database("https://esp8266-demo-9c927-default-rtdb.firebaseio.com/")
        val myRef = database.getReference("IoT1/FirebaseIOT/temperatura")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                tmp= dataSnapshot.value.toString()
            }
            
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                println("Failed to read value.")
            }
            
        })
        
    }
}

@Composable
fun Greeting(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = stringResource(R.string.hello_world, greetingName)
    )
}
