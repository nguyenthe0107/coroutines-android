package com.kantek.coroutines.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * "id": 1,
"name": "Leanne Graham",
"username": "Bret",
"email": "Sincere@april.biz",
"address": {
"street": "Kulas Light",
"suite": "Apt. 556",
"city": "Gwenborough",
"zipcode": "92998-3874",
"geo": {
"lat": "-37.3159",
"lng": "81.1496"
}
},
"phone": "1-770-736-8031 x56442",
"website": "hildegard.org",
"company": {
"name": "Romaguera-Crona",
"catchPhrase": "Multi-layered client-server neural-net",
"bs": "harness real-time e-markets"
}
 */
class User(
    val id: String,
    val name: String,
    @SerializedName("username")
    val userName: String,
    val email: String,
    val phone: String,
    val website: String,
    val company: Company,
    val address: Address
) : Serializable

/**
 * {
"street": "Kulas Light",
"suite": "Apt. 556",
"city": "Gwenborough",
"zipcode": "92998-3874",
"geo": {
"lat": "-37.3159",
"lng": "81.1496"
}
}
 */
class Address(
    val street: String,
    val suite: String,
    val city: String
) {
    override fun toString() = "$street, $suite, $city"
}

/**
 * {
"name": "Romaguera-Crona",
"catchPhrase": "Multi-layered client-server neural-net",
"bs": "harness real-time e-markets"
}
 */
class Company(
    val catchPhrase: String,
    val bs: String,
    val name: String
)