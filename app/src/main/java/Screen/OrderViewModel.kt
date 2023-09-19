package Screen

import androidx.lifecycle.ViewModel
import com.example.cupcakeapp.data.OrderUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val PRICE_PER_CUPCAKE = 3.00
private const val PRICE_FOR_SAME_DAY_PICKUP = 6.00

class OrderViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(OrderUiState(pickupOptions = pickupOptions()))
    //Lưu giữa trạng thái giao diện của người dùng
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()
    //Khi _uiState thay đổi thì uiState sẽ cập nhật giá trị tương đương

    //Hàm cập nhật trạng thái _uiState bằng cách thay đổi số lượng và price dựa trên số lượng cake
    fun setQuantity(numberCupcakes: Int) {
        //Nhận tham số là số lượng cake kiểu số nguyên
        _uiState.update { currentState ->
            //Cập nhật tráng thái
            currentState.copy(
                //tạo bản sao của trạng thái hiện tại
                quantity = numberCupcakes,
                //Cập nhật số lượng
                price = calculatePrice(quantity = numberCupcakes)
                //Tính toán giá dựa vào số lượng
            )
        }
    }

    /**
     * Set the [desiredFlavor] of cupcakes for this order's state.
     * Only 1 flavor can be selected for the whole order.
     * Hàm lấy giá trị về hương vị
     */
    fun setFlavor(desiredFlavor: String) {
        //Tham số đầu vào là String
        _uiState.update { currentState ->
            //Cập Nhật trạng thái
            currentState.copy(flavor = desiredFlavor)
            //Tạo bản sao của trạng thái hiện tại
        }
    }

    /**
     * Set the [pickupDate] for this order's state and update the price
     * Hàm lấy giá trị date
     */
    fun setDate(pickupDate: String) {
        //Tham số đầu vào là 1 String
        _uiState.update { currentState ->
            //Cập nhâth trạng thái
            currentState.copy(
                //Tạo bản sao của trạng thái hiện tại
                date = pickupDate,
                //cập nhật ngày
                price = calculatePrice(pickupDate = pickupDate)
                //tính toán giá dựa vào ngày
            )
        }
    }

    /**
     * Reset the order state
     * Hàm reset cả trình
     */
    fun resetOrder() {
        _uiState.value = OrderUiState(pickupOptions = pickupOptions())
    }

    /**
     * Returns the calculated price based on the order details.
     * Hàm trả về giá đã tính toán dựa trên số lượng và ngày người dùng chọn
     */
    private fun calculatePrice(
        quantity: Int = _uiState.value.quantity,
        //Lấy số lượng
        pickupDate: String = _uiState.value.date
        //Lấy ngày
    ): String {
        var calculatedPrice = quantity * PRICE_PER_CUPCAKE
        //Tính toán price dựa trên số lượng
        if (pickupOptions()[0] == pickupDate) { //Nếu và dùng trong 1 ngày
            calculatedPrice += PRICE_FOR_SAME_DAY_PICKUP
        }
        val formattedPrice = NumberFormat.getCurrencyInstance().format(calculatedPrice)
        //Định dạng số tiền tính toán thành 1 chuỗi tiền tệ
        return formattedPrice
        //Trả về số tiền đã định dạng
    }

    /**
     * Returns a list of date options starting with the current date and the following 3 dates.
     * Hàm để lưu trữ các option
     */
    private fun pickupOptions(): List<String> {
        val dateOptions = mutableListOf<String>()
        //Tạo danh sách để lưu trữ các ngày tuỳ chọn
        val formatter = SimpleDateFormat("E MMM d", Locale.getDefault())
        //Định dạng ngày tháng
        val calendar = Calendar.getInstance()
        //Lấy một thể hiện của lịch
        repeat(4) {
            dateOptions.add(formatter.format(calendar.time))
            calendar.add(Calendar.DATE, 1)
        }
        //Thềm này hiện tại và 3 ngày liên tiếp

        return dateOptions
        //trả về 4 ngày gần nhất là ngày hiện tại và 3 ngày tiếp theo đó
    }
}