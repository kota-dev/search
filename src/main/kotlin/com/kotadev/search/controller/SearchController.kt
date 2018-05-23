package com.kotadev.search.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.kotadev.search.data.HotPepperData
import com.kotadev.search.data.Shop
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpSession
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.client.RestTemplate

@Controller
class SearchController {

    @GetMapping("/")
    fun root(session: HttpSession, model: Model): String {
        val describe= "丸の内近辺のレストラン情報を検索します。 by HOT PEPPER グルメ"
        model.addAttribute("describe", describe)
        return "root"
    }

    @RequestMapping(value = ["/result"], method = arrayOf(RequestMethod.POST))
    fun zipcodeConfirm(session: HttpSession, model: Model,
                       @RequestParam("searchtext") searchtext: String?): String {

        // 簡易入力チェック
        if (null.equals(searchtext)  || "".equals(searchtext)) {
            model.addAttribute("errorMessage", "キーワードは1文字以上入力してください。")
            return root(session, model)
        }

        // RestTemplateによるAPI発行
        val restTemplate = RestTemplate()
        val inputURL:String = "https://webservice.recruit.co.jp/hotpepper/gourmet/v1/?key=ac54721d172afde7&small_area=X030&keyword=$searchtext&format=json"
        val resultText = restTemplate.getForObject(inputURL, String::class.java)

        // JSONオブジェクトマッピング(Jackson)
        val mapper = jacksonObjectMapper()
        val hotPepperJson = mapper.readValue<HotPepperData>(resultText.toString())

        // ミュータブルリスト定義
        val mutablelist: MutableList<Shop> = mutableListOf()

        // 検索ヒット件数分のshop情報をミュータブルリストに格納
        for (i in 1..Integer.parseInt(hotPepperJson.results.results_returned)){
            mutablelist.add(hotPepperJson.results.shop[i-1])
        }

        // リストのミュータブル解除
        val list: List<Shop> = mutablelist

        // リスト0件の場合は再入力を促す
        if (list.count() == 0) {
            model.addAttribute("errorMessage", "お探しのお店は丸の内近辺にございません。ジャンルまたは店舗名を入力ください。")
            model.addAttribute("searchedText", searchtext)
            return root(session, model)
        }

        // リスト1件以上の場合は結果表示
        model.addAttribute("resultText", list)
        model.addAttribute("searchedText", searchtext)
        return root(session, model)
    }

}