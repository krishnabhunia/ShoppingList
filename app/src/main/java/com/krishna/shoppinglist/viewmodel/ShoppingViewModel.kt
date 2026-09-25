package com.krishna.shoppinglist.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.krishna.shoppinglist.data.AppDatabase
import com.krishna.shoppinglist.data.PreferencesManager
import com.krishna.shoppinglist.data.ShoppingItem
import com.krishna.shoppinglist.data.ShoppingList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShoppingViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val listDao = db.listDao()
    private val itemDao = db.itemDao()
    private val prefs = PreferencesManager(application)

    val lists: StateFlow<List<ShoppingList>> = listDao.getAllLists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val darkMode: StateFlow<Boolean> = prefs.darkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val showSplash: StateFlow<Boolean> = prefs.showSplash
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val allowEdit: StateFlow<Boolean> = prefs.allowEdit
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun itemsForList(listId: Long): Flow<List<ShoppingItem>> = itemDao.getItemsForList(listId)

    fun addList(name: String) = viewModelScope.launch {
        if (name.isNotBlank()) listDao.insert(ShoppingList(name = name.trim()))
    }

    fun renameList(list: ShoppingList, newName: String) = viewModelScope.launch {
        if (newName.isNotBlank()) listDao.update(list.copy(name = newName.trim()))
    }

    fun deleteList(list: ShoppingList) = viewModelScope.launch {
        itemDao.deleteAllForList(list.id)
        listDao.delete(list)
    }

    fun duplicateList(list: ShoppingList) = viewModelScope.launch {
        val newId = listDao.insert(
            list.copy(id = 0, name = "${list.name} (copy)", createdAt = System.currentTimeMillis())
        )
        itemDao.getItemsForList(list.id).first().forEach { item ->
            itemDao.insert(item.copy(id = 0, listId = newId))
        }
    }

    fun addItem(listId: Long, name: String, category: String = "Other") = viewModelScope.launch {
        if (name.isNotBlank()) itemDao.insert(ShoppingItem(listId = listId, name = name.trim(), category = category))
    }

    fun toggleItem(item: ShoppingItem) = viewModelScope.launch {
        itemDao.update(item.copy(isDone = !item.isDone))
    }

    fun deleteItem(item: ShoppingItem) = viewModelScope.launch {
        itemDao.delete(item)
    }

    fun setDarkMode(value: Boolean) = viewModelScope.launch { prefs.setDarkMode(value) }
    fun setShowSplash(value: Boolean) = viewModelScope.launch { prefs.setShowSplash(value) }
    fun setAllowEdit(value: Boolean) = viewModelScope.launch { prefs.setAllowEdit(value) }

    suspend fun listSummary(listId: Long): Pair<Int, Int> {
        return Pair(itemDao.countForList(listId), itemDao.doneCountForList(listId))
    }

    companion object {
        /** Static starter dictionary used for the quick-add suggestion dropdown. */
        private val commonItems = mapOf(
            "Dairy" to listOf("Milk", "Eggs", "Cheese", "Butter", "Yogurt", "Paneer"),
            "Bakery" to listOf("Bread", "Buns", "Cake", "Cookies"),
            "Vegetables" to listOf("Onions", "Tomatoes", "Potatoes", "Spinach", "Carrots"),
            "Fruits" to listOf("Apples", "Apple juice", "Apple cider", "Bananas", "Oranges", "Grapes"),
            "Pantry" to listOf("Rice", "Cooking oil", "Sugar", "Salt", "Flour", "Tea", "Coffee"),
            "Household" to listOf("Detergent", "Dish soap", "Tissues", "Trash bags")
        )

        fun suggestionsFor(query: String): List<Pair<String, String>> {
            if (query.isBlank()) return emptyList()
            val q = query.trim().lowercase()
            return commonItems.flatMap { (category, items) ->
                items.filter { it.lowercase().startsWith(q) }.map { it to category }
            }.take(6)
        }
    }
}
