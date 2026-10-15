package com.github.ryjen.jokeapp.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.github.ryjen.jokeapp.domain.usecase.GetUserLocale
import com.github.ryjen.jokeapp.ui.navigation.Router
import com.github.ryjen.jokeapp.ui.theme.ThemeColors
import com.github.ryjen.jokeapp.ui.theme.ThemeDimensions
import org.koin.androidx.compose.inject

/*
  Use in footers to get bottom navigation
 */

@Composable
fun TabBar(router: Router) {
    val currentRoute = router.currentRoute()

    val getUserLocale: GetUserLocale by inject()

    BottomNavigation(
        // Apply the safe navigation area once to the whole bar, not once per tab.
        windowInsets = BottomNavigationDefaults.windowInsets,
        backgroundColor = ThemeColors.bottomBar,
        contentColor = ThemeColors.onBottomBar
    ) {
        router.navTabs.forEach { (route, tab) ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = tab.icon(),
                        modifier = Modifier.size(ThemeDimensions.icons.small),
                        contentDescription = null
                    )
                },
                label = {
                    Text(stringResource(tab.title).uppercase(getUserLocale()))
                },
                selected = currentRoute == route,
                onClick = {
                    router.routeTo(route)
                },
                alwaysShowLabel = true,
                selectedContentColor = ThemeColors.material.secondary,
                unselectedContentColor = LocalContentColor.current
            )
        }
    }
}
