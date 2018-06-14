package com.kotadev.search.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.kotadev.search.data.HotPepperData
import com.kotadev.search.data.Shop
import com.kotadev.search.repository.SearchRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate

@Controller
class SearchController(val searchRepository: SearchRepository) {

    @GetMapping("/")
    fun root(model: Model): String {
        val describe= "丸の内近辺のレストラン情報を検索しますよ。 by HOT PEPPER グルメ"
        model.addAttribute("describe", describe)
        // @Controllerの場合は、↓のreturnをそのまま値としてroot.htmlへ飛ばすようになっている
        return "root"
    }

    @RequestMapping(value = ["/result"], method = arrayOf(RequestMethod.POST))
    fun jsonSearch(model: Model,
                       @RequestParam("searchtext") searchtext: String?): String {

        // 簡易入力チェック
        if (searchRepository.doNullCheck(searchtext)) {
            model.addAttribute("errorMessage", "キーワードは1文字以上入力してください。")
            return root(model)
        }

        // API呼び出し用URL
        val inputURL = "https://webservice.recruit.co.jp/hotpepper/gourmet/v1/?key=ac54721d172afde7&small_area=X030&keyword=$searchtext&format=json"

        var hashedMap:LinkedHashMap<Int, Shop> = linkedMapOf()

        // API呼び出し
        hashedMap = searchRepository.getAPI(inputURL) as LinkedHashMap<Int, Shop>

        // リスト0件の場合は再入力を促す
        if (hashedMap.count() == 0) {
            model.addAttribute("errorMessage", "お探しのお店は丸の内近辺にございません。ジャンルまたは店舗名を入力ください。")
            model.addAttribute("searchedText", searchtext)
            return root(model)
        }

        // リスト1件以上の場合は結果表示
        model.addAttribute("resultText", hashedMap)
        model.addAttribute("searchedText", searchtext)
        return root(model)
    }

}