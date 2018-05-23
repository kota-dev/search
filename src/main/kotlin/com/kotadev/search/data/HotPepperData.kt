package com.kotadev.search.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown=true)
data class HotPepperData(val results: Results)

@JsonIgnoreProperties(ignoreUnknown=true)
data class Results(val results_returned: String, val shop: List<Shop>)

@JsonIgnoreProperties(ignoreUnknown=true)
data class Shop(val name: String, val address: String, val mobile_access: String, val open: String, val urls: URLS)

@JsonIgnoreProperties(ignoreUnknown=true)
data class URLS(val pc: String)
