package com.rejowan.numberconverter.presentation.home.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import com.rejowan.numberconverter.R
import com.rejowan.numberconverter.presentation.navigation.Screen

data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val contentDescription: String
)

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    currentDestination: NavDestination?
) {
    val items = listOf(
        BottomNavItem(
            route = Screen.Converter.route,
            title = stringResource(R.string.title_converter),
            selectedIcon = Icons.Filled.Calculate,
            unselectedIcon = Icons.Outlined.Calculate,
            contentDescription = stringResource(R.string.title_converter)
        ),
        BottomNavItem(
            route = Screen.Learn.route,
            title = stringResource(R.string.title_learn),
            selectedIcon = Icons.AutoMirrored.Filled.MenuBook,
            unselectedIcon = Icons.AutoMirrored.Outlined.MenuBook,
            contentDescription = stringResource(R.string.title_learn)
        ),
        BottomNavItem(
            route = Screen.Practice.route,
            title = stringResource(R.string.title_practice),
            selectedIcon = Icons.Filled.Quiz,
            unselectedIcon = Icons.Outlined.Quiz,
            contentDescription = stringResource(R.string.title_practice)
        )
    )

    NavigationBar {
        items.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.Converter.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.contentDescription
                    )
                },
                label = { Text(text = item.title) }
            )
        }
    }
}
