package com.krishna.shoppinglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.krishna.shoppinglist.ui.screens.ListDetailScreen
import com.krishna.shoppinglist.ui.screens.ListsHomeScreen
import com.krishna.shoppinglist.ui.screens.SettingsScreen
import com.krishna.shoppinglist.ui.screens.SplashScreen
import com.krishna.shoppinglist.ui.theme.ShoppingListTheme
import com.krishna.shoppinglist.viewmodel.ShoppingViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: ShoppingViewModel = viewModel()
            val darkMode by viewModel.darkMode.collectAsState()

            ShoppingListTheme(darkTheme = darkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavHost(viewModel)
                }
            }
        }
    }
}

@Composable
private fun AppNavHost(viewModel: ShoppingViewModel) {
    val navController: NavHostController = rememberNavController()
    val showSplash by viewModel.showSplash.collectAsState()

    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(showSplash = showSplash) {
                navController.navigate("home") { popUpTo("splash") { inclusive = true } }
            }
        }

        composable("home") {
            val lists by viewModel.lists.collectAsState()
            ListsHomeScreen(
                lists = lists,
                itemCountFor = { listId -> viewModel.listSummary(listId) },
                onOpenList = { listId -> navController.navigate("list/$listId") },
                onCreateList = { name -> viewModel.addList(name) },
                onOpenSettings = { navController.navigate("settings") }
            )
        }

        composable("list/{listId}") { backStackEntry ->
            val listId = backStackEntry.arguments?.getString("listId")?.toLongOrNull() ?: return@composable
            val lists by viewModel.lists.collectAsState()
            val list = lists.find { it.id == listId }
            val items by viewModel.itemsForList(listId).collectAsState(initial = emptyList())

            if (list != null) {
                ListDetailScreen(
                    list = list,
                    items = items,
                    onBack = { navController.popBackStack() },
                    onToggleItem = { item -> viewModel.toggleItem(item) },
                    onAddItem = { name, category -> viewModel.addItem(listId, name, category) },
                    onRename = { newName -> viewModel.renameList(list, newName) },
                    onDuplicate = { viewModel.duplicateList(list) },
                    onDelete = { viewModel.deleteList(list); navController.popBackStack() }
                )
            }
        }

        composable("settings") {
            val darkMode by viewModel.darkMode.collectAsState()
            val showSplashPref by viewModel.showSplash.collectAsState()
            val allowEdit by viewModel.allowEdit.collectAsState()

            SettingsScreen(
                darkMode = darkMode,
                showSplash = showSplashPref,
                allowEdit = allowEdit,
                onBack = { navController.popBackStack() },
                onDarkModeChange = { viewModel.setDarkMode(it) },
                onShowSplashChange = { viewModel.setShowSplash(it) },
                onAllowEditChange = { viewModel.setAllowEdit(it) }
            )
        }
    }
}
