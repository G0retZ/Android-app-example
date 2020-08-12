package com.example.app

import java.util.*

data class LocationsResponse(
  val brand: Brand? = null
)

data class Brand(val shops: List<Shop>)

data class Shop(
  val id: String = "",
  val phone: String? = "",
  val name: String? = "",
  val email: String? = "",
  val website: String? = "",
  val heading: String? = "",
  val location: Location? = null,
  val address: Address? = null,
  val photos: List<Photo> = emptyList()
)

data class Location(
  var lat: Double? = 0.0,
  var long: Double? = 0.0
)

data class Address(
  var street: String = "",
  var street2: String? = "",
  var city: String = "",
  var state: String = "",
  var zip: String = ""
)

data class Photo(
  val url: String = "",
  val thumbnail: String = "",
  val createdAt: Date = Date()
)