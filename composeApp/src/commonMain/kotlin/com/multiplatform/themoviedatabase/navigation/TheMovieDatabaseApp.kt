package com.multiplatform.themoviedatabase.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.multiplatform.data.repository.MovieRepositoryImpl
import com.multiplatform.themoviedatabase.screens.detail.DetailsScreen
import com.multiplatform.themoviedatabase.screens.landing.LandingScreen
import com.multiplatform.themoviedatabase.screens.landing.LandingViewModel
import com.multiplatform.themoviedatabase.screens.landing.states.LandingState
import com.multiplatform.themoviedatabase.screens.splash.SplashScreen
import com.multiplatform.themoviedatabase.screens.splash.SplashViewModel
import com.multiplatform.themoviedatabase.screens.splash.states.SplashState
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

    val movieRepository = MovieRepositoryImpl()

    // TODO: refactor and inject the repository using D.I
    val splashViewModel: SplashViewModel = viewModel {
        SplashViewModel(
            movieRepository = movieRepository,
            initialState = SplashState.initial()
        )
    }

    val landingViewModel: LandingViewModel = viewModel {
        LandingViewModel(
            movieRepository = movieRepository,
            initialState = LandingState.initial()
        )
    }

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
                            SplashScreen(
                                state = splashViewModel.state,
                                onDataLoaded = { backStack.add(Screens.Landing) }
                            )
                        }
                    }
                    is Screens.Landing -> {
                        // we don't want to go back to splash after auto navigation earlier from app launch
                        backStack.remove(Screens.Splash)
                        NavEntry(navKey) {
                            LandingScreen(
                                landingState = landingViewModel.state,
                                onMediaClicked = { movie ->
                                    backStack.add(Screens.MovieDetails(movie))
                                },
                                initialConfig = {
                                    landingViewModel.setInitialData(splashViewModel.state.movieData)
                                },
                                loadNext = { landingViewModel.loadNext() }
                            )
                        }
                    }
                    is Screens.MovieDetails -> {
                        NavEntry(navKey) {
                            DetailsScreen(navKey.movie)
                        }
                    }
                    else -> error("Unknown NavKey ---> $navKey")
                }
            }
        )
    }
}