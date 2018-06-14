package com.kotadev.search.repository

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.kotadev.search.data.HotPepperData
import com.kotadev.search.data.Shop
import org.hibernate.validator.internal.xml.ContainerElementTypePath.root
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Repository
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate

@Repository
class SearchRepository {

    // 簡易入力チェック
    fun doNullCheck(string: String?): Boolean {
        return null.equals(string)  || "".equals(string)
    }

    // API取得
    fun getAPI(string: String):HashMap<Int, Shop> {
        // RestTemplateによるAPI発行
        val restTemplate = RestTemplate()
        val resultText = restTemplate.getForObject(string, String::class.java)

        // JSONオブジェクトマッピング(Jackson)
        val mapper = jacksonObjectMapper()
        val hotPepperJson = mapper.readValue<HotPepperData>(resultText.toString())
        val hashedMap:LinkedHashMap<Int, Shop> = linkedMapOf()

        // 検索ヒット件数分のshop情報をMapに格納
        for (i in 1..Integer.parseInt(hotPepperJson.results.results_returned)){
            hashedMap.set(i, hotPepperJson.results.shop[i-1])
        }
        return hashedMap
    }

}