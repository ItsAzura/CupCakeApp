package com.example.cupcakeapp.data

//Tạo ra một lớp data lưu trữ trạng thái giao hàng
//Vì dùng data class nên dễ dàng create, copy và so sánh các trạng thái này
data class OrderUiState (
    //Các trạng thuộc tính của nó:
    val quantity: Int = 0,
    val flavor: String = "",
    val date: String = "",
    val price: String = "",
    val pickupOptions: List<String> = listOf()
)