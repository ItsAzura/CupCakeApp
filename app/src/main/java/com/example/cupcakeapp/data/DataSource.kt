package com.example.cupcakeapp.data

import com.example.cupcakeapp.R

object DataSource {
    //tạo một danh sách chứa các flavors
    val flavors = listOf(
        R.string.vanilla,
        R.string.chocolate,
        R.string.red_velvet,
        R.string.salted_caramel,
        R.string.coffee
    )
    //Tạo một danh sách chứa các cặp pair gồm  quantityOptions và số tài nguyên tương ứng với nó
    val quantityOptions = listOf(
        Pair(R.string.one_cupcake, 1),
        Pair(R.string.six_cupcakes, 6),
        Pair(R.string.twelve_cupcakes, 12),
        Pair(R.string.twentyfour_cupcakes, 24),
        Pair(R.string.sixtyfour_cupcakes, 64)
    )
}