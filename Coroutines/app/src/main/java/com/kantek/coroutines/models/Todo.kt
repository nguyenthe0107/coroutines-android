package com.kantek.coroutines.models

/**
 * "userId": 1,
"id": 2,
"title": "quis ut nam facilis et officia qui",
"completed": false
 */
class Todo(
    val id: String,
    var title: String,
    var completed: Boolean
) {

    infix fun copy(it: Todo): Todo {
        title = it.title
        completed = it.completed
        return this
    }
}
