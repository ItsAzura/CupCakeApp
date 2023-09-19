package com.example.cupcakeapp

import Screen.OrderSummary
import Screen.OrderViewModel
import Screen.SelectOption
import Screen.StartOrder
import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cupcakeapp.data.DataSource
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * enum values that represent the screens in the app
 * Định nghĩa các màn hình của app
 */
enum class CupcakeScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    Flavor(title = R.string.choose_flavor),
    Pickup(title = R.string.choose_pickup_date),
    Summary(title = R.string.order_summary)
}
/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 * Hàm compos để hiện thị thanh top bar và hiện thị nút trở lại khi việc điều hướng hoạt động xảy ra
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CupcakeAppBar(
    currentScreen: CupcakeScreen,
    //Màn hình htai được chọn từ enum class
    canNavigateBack: Boolean,
    //đã thực hiện việc điều hướng hay chưa
    navigateUp: () -> Unit,
    //làm lamda dùng để điều hướng ngược lại màn hình
    modifier: Modifier = Modifier
) {
    TopAppBar( //Tạo Thanh điều hướng phía trên cùng của màn hình
        title =
        {
            Text(stringResource(currentScreen.title))
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {//Nếu việc thực hiện điều hướng xảy ra thì thay đồi icon
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

//Hàm compost tạo CupCakeApp
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CupcakeApp(
    viewModel: OrderViewModel = viewModel(),
    //Tham số đầu vào có kiểu OderViewModel
    navController: NavHostController = rememberNavController()
    //Tham số đầu vào với mục đích điều hướng giữa các screen trong app
) {
    //Cho phép bạn biết được bạn đang ở đâu trong ứng dụng
    val backStackEntry by navController.currentBackStackEntryAsState()
    //Lấy thông tin về màn hình hiện tại và gắn nó vào currentScreen
    val currentScreen = CupcakeScreen.valueOf(
        backStackEntry?.destination?.route ?: CupcakeScreen.Start.name
    )
    Scaffold(
        topBar = {
            CupcakeAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                //Kiểm tra xem trước không có màn hình nào
                navigateUp = { navController.navigateUp() }
                //Thực hiện việc điều hướng trở lại trang trước đó
            )
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()
        NavHost(
            navController = navController,
            //Quản lý stack điều hướng các trạng thái của các điểm đến
            startDestination = CupcakeScreen.Start.name,
            //Màn hình bắt đầu
            modifier = Modifier.padding(innerPadding)
        )
        {
            //Conposable dùng để xác định một màn hình
            //route dùng để xác định đường dẫn cho màn hình
            composable(route = CupcakeScreen.Start.name) {
                StartOrder(
                    quantityOptions = DataSource.quantityOptions,
                    //hiện thị các option
                    onNextButtonClicked = {
                        viewModel.setQuantity(it)
                        //Lưu option vào để tính toán
                        navController.navigate(CupcakeScreen.Flavor.name)
                        //Điều hướng lên màn hình tiếp theo
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
            composable(route = CupcakeScreen.Flavor.name) {
                val context = LocalContext.current
                SelectOption(
                    subtotal = uiState.price,
                    //Hiện thị giá
                    onNextButtonClicked = { navController.navigate(CupcakeScreen.Pickup.name) },
                    //Nếu click vào thì điều hướng lên màn hình tiếp theo
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                        //Nếu click vào thì sẽ huỷ đơn hàng và điều hướng đến màn hình khởi đầu
                    },
                    options = DataSource.flavors.map { id -> context.resources.getString(id) },
                    //Hiện thị các option để user chọn
                    onSelectionChanged = { viewModel.setFlavor(it) },
                    //Lưu vào viewmodel
                    modifier = Modifier.fillMaxHeight()
                )
            }
            composable(route = CupcakeScreen.Pickup.name) {
                SelectOption(
                    subtotal = uiState.price,
                    //Hiện giá
                    onNextButtonClicked = { navController.navigate(CupcakeScreen.Summary.name) },
                    //Nếu click vào thì đến màn hình tiếp theo
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    //Nếu click vào thì đưa về màn hình khởi đầu và xoá tất cả mọi đơn hàng
                    options = uiState.pickupOptions,
                    //hiện thì các option để user chọn
                    onSelectionChanged = { viewModel.setDate(it) },
                    //Lưu option mà user chọn
                    modifier = Modifier.fillMaxHeight()
                )
            }
            composable(route = CupcakeScreen.Summary.name) {
                val context = LocalContext.current
                //truy cập vào context của màn hình hiện tại
                OrderSummary(
                    orderUiState = uiState,
                    //Hiện thị các mục mà user đã chọn
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    //Click vào thì đưa về màn hình khởi đầi và xoá đơn hàng
                    onSendButtonClicked = { subject: String, summary: String ->
                        shareOrder(context, subject = subject, summary = summary)
                    },
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
}

/**
 * Resets the [OrderUiState] and pops up to [CupcakeScreen.Start]
 */
private fun cancelOrderAndNavigateToStart(
    viewModel: OrderViewModel,
    navController: NavHostController
) {
    viewModel.resetOrder()
    navController.popBackStack(CupcakeScreen.Start.name, inclusive = false)
}

/**
 * Creates an intent to share order details
 */
private fun shareOrder(context: Context, subject: String, summary: String) {
    // Create an ACTION_SEND implicit intent with order details in the intent extras
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, summary)
    }
    context.startActivity(
        Intent.createChooser(
            intent,
            context.getString(R.string.new_cupcake_order)
        )
    )
}
