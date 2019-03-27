package com.kantek.coroutines.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * "userId": 1,
"id": 2,
"title": "quis ut nam facilis et officia qui",
"completed": false
 */
@Entity
class Todo(
    @PrimaryKey
    val id: Int,
    val userId: String,
    var title: String,
    var completed: Boolean
) {

    infix fun copy(it: Todo): Todo {
        title = it.title
        completed = it.completed
        return this
    }
}
