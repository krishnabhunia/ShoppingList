package com.krishna.shoppinglist.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM shopping_items WHERE listId = :listId ORDER BY category, position, id")
    fun getItemsForList(listId: Long): Flow<List<ShoppingItem>>

    @Query("SELECT COUNT(*) FROM shopping_items WHERE listId = :listId")
    suspend fun countForList(listId: Long): Int

    @Query("SELECT COUNT(*) FROM shopping_items WHERE listId = :listId AND isDone = 1")
    suspend fun doneCountForList(listId: Long): Int

    @Insert
    suspend fun insert(item: ShoppingItem): Long

    @Update
    suspend fun update(item: ShoppingItem)

    @Delete
    suspend fun delete(item: ShoppingItem)

    @Query("DELETE FROM shopping_items WHERE listId = :listId")
    suspend fun deleteAllForList(listId: Long)
}
