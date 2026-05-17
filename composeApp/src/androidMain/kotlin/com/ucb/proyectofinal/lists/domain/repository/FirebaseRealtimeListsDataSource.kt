package com.ucb.proyectofinal.lists.domain.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ucb.proyectofinal.lists.domain.model.ContentItem
import com.ucb.proyectofinal.lists.domain.model.ContentList
import com.ucb.proyectofinal.lists.domain.model.ContentType
import com.ucb.proyectofinal.lists.domain.model.vo.ItemId
import com.ucb.proyectofinal.lists.domain.model.vo.ItemTitle
import com.ucb.proyectofinal.lists.domain.model.vo.ListId
import com.ucb.proyectofinal.lists.domain.model.vo.ListName
import com.ucb.proyectofinal.lists.domain.model.vo.Rating
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

actual class FirebaseRealtimeListsDataSource actual constructor() {

    private val root = FirebaseDatabase.getInstance().reference

    actual fun observeLists(userId: String): Flow<List<ContentList>> = callbackFlow {
        val ref = root.child("users").child(userId).child("lists")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = snapshot.children.mapNotNull { listSnapshot ->
                    val id = listSnapshot.key ?: return@mapNotNull null
                    val name = listSnapshot.child("name").getValue(String::class.java) ?: return@mapNotNull null
                    val type = listSnapshot.child("type").getValue(String::class.java) ?: return@mapNotNull null
                    val description = listSnapshot.child("description").getValue(String::class.java).orEmpty()
                    val coverImageUrl = listSnapshot.child("coverImageUrl").getValue(String::class.java)
                    val isPublic = listSnapshot.child("isPublic").getValue(Boolean::class.java) ?: true
                    val itemCount = listSnapshot.child("itemCount").getValue(Int::class.java)
                        ?: listSnapshot.child("items").childrenCount.toInt()
                    val contentType = runCatching { ContentType.valueOf(type) }.getOrNull() ?: return@mapNotNull null
                    ContentList(
                        id = ListId(id),
                        name = ListName(name),
                        type = contentType,
                        itemCount = itemCount,
                        description = description,
                        coverImageUrl = coverImageUrl,
                        isPublic = isPublic
                    )
                }
                trySend(lists.sortedByDescending { it.id.value })
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    actual fun observeItems(userId: String, listId: String): Flow<List<ContentItem>> = callbackFlow {
        val ref = root.child("users").child(userId).child("lists").child(listId).child("items")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { itemSnapshot ->
                    itemSnapshot.toContentItem(listId)
                }
                trySend(items.sortedByDescending { it.id.value })
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    actual suspend fun createList(userId: String, list: ContentList) {
        root.child("users").child(userId).child("lists").child(list.id.value).updateChildren(
            mapOf(
                "id" to list.id.value,
                "name" to list.name.value,
                "type" to list.type.name,
                "description" to list.description,
                "coverImageUrl" to list.coverImageUrl,
                "isPublic" to list.isPublic,
                "itemCount" to list.itemCount,
                "createdAt" to System.currentTimeMillis(),
                "updatedAt" to System.currentTimeMillis()
            )
        ).await()
    }

    actual suspend fun addItem(userId: String, item: ContentItem) {
        val listRef = root.child("users").child(userId).child("lists").child(item.listId.value)
        listRef.child("items").child(item.id.value).setValue(item.toMap()).await()
        val count = listRef.child("items").get().await().childrenCount.toInt()
        listRef.child("itemCount").setValue(count).await()
    }

    actual suspend fun updateList(userId: String, list: ContentList) {
        root.child("users").child(userId).child("lists").child(list.id.value).updateChildren(
            mapOf(
                "name" to list.name.value,
                "description" to list.description,
                "coverImageUrl" to list.coverImageUrl,
                "isPublic" to list.isPublic,
                "updatedAt" to System.currentTimeMillis()
            )
        ).await()
    }

    actual suspend fun updateItem(userId: String, item: ContentItem) {
        root.child("users").child(userId)
            .child("lists")
            .child(item.listId.value)
            .child("items")
            .child(item.id.value)
            .setValue(item.toMap())
            .await()
    }

    actual suspend fun deleteList(userId: String, listId: String) {
        root.child("users").child(userId).child("lists").child(listId).removeValue().await()
    }

    actual suspend fun deleteItem(userId: String, listId: String, itemId: String) {
        val listRef = root.child("users").child(userId).child("lists").child(listId)
        listRef.child("items").child(itemId).removeValue().await()
        val count = listRef.child("items").get().await().childrenCount.toInt()
        listRef.child("itemCount").setValue(count).await()
    }

    private fun ContentItem.toMap(): Map<String, Any?> = mapOf(
        "id" to id.value,
        "listId" to listId.value,
        "title" to title.value,
        "type" to type.name,
        "seen" to seen,
        "rating" to rating?.value,
        "updatedAt" to System.currentTimeMillis()
    )

    private fun DataSnapshot.toContentItem(defaultListId: String): ContentItem? {
        val id = key ?: return null
        val title = child("title").getValue(String::class.java) ?: return null
        val type = child("type").getValue(String::class.java) ?: return null
        val seen = child("seen").getValue(Boolean::class.java) ?: false
        val ratingValue = child("rating").getValue(Int::class.java)
        val listId = child("listId").getValue(String::class.java) ?: defaultListId
        val contentType = runCatching { ContentType.valueOf(type) }.getOrNull() ?: return null

        return ContentItem(
            id = ItemId(id),
            listId = ListId(listId),
            title = ItemTitle(title),
            type = contentType,
            seen = seen,
            rating = ratingValue?.let { Rating(it) }
        )
    }
}
