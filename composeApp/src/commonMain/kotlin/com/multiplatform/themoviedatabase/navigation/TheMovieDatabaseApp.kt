package com.multiplatform.themoviedatabase.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.multiplatform.themoviedatabase.screens.landing.LandingScreen
import com.multiplatform.themoviedatabase.screens.splash.SplashScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic


@Composable
fun TheMovieDatabaseApp(
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Screens.Splash::class, Screens.Splash.serializer())
                    subclass(Screens.Landing::class, Screens.Landing.serializer())
                }
            }
        },
        elements = listOf(Screens.Splash).toTypedArray()
    )


    Scaffold { innerPadding ->
        NavDisplay(
            modifier = modifier.padding(innerPadding),
            backStack = backStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(removeViewModelStoreOnPop = { false })
            ),
            entryProvider = { navKey ->
                when(navKey) {
                    is Screens.Splash -> {
                        NavEntry(navKey) {
                            SplashScreen {
                                backStack.add(Screens.Landing)
                            }
                        }
                    }
                    is Screens.Landing -> {
                        // we don't want to go back to splash after auto navigation earlier from app launch
                        backStack.remove(Screens.Splash)
                        NavEntry(navKey) {
                            LandingScreen()
                        }
                    }
                    else -> error("Unknown NavKey ---> $navKey")
                }
            }
        )
    }
}