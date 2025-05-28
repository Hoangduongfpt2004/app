package com.example.appquanly.salee

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanly.*
import com.example.appquanly.ChooseDish.view.ChooseDishActivity
import com.example.appquanly.LinkAccount.Link_AccountActivity
import com.example.appquanly.MenuCategory.Menu_CategoryActivity
import com.example.appquanly.Setup.Set_upActivity
import com.example.appquanly.data.sqlite.Entity.SAInvoiceItem
import com.example.appquanly.SalePutIn.BaseDrawerActivity

class SaleeActivity : BaseDrawerActivity(), SaleeContract.View {

    private lateinit var presenter: SaleeContract.Presenter
    private lateinit var tvHint: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutEmpty: View

    override fun getLayoutId(): Int = R.layout.activity_sale
    override fun getNavigationMenuItemId(): Int = R.id.ban_hang

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())

        tvHint = findViewById(R.id.tvHint)
        recyclerView = findViewById(R.id.rvProducts)
        layoutEmpty = findViewById(R.id.layoutEmpty)

        presenter = SaleePresenter(this, this)

        recyclerView.layoutManager = LinearLayoutManager(this)

        tvHint.setOnClickListener {
            presenter.onHintClicked()
        }

        presenter.loadInvoiceItems()
        showNoOrders()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_banhang, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add -> {
                presenter.onAddButtonClicked()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun showNoOrders() {
        tvHint.text = "Bấm vào đây hoặc dấu + để chọn món"
    }

    override fun openOrderScreen() {
        startActivity(Intent(this, ChooseDishActivity::class.java))
    }

    override fun showMenu() {
        Toast.makeText(this, "Mở menu", Toast.LENGTH_SHORT).show()
    }

    override fun navigateTo(screen: SaleeContract.MenuScreen) {
        when (screen) {
            SaleeContract.MenuScreen.BAN_HANG -> {}
            SaleeContract.MenuScreen.THUC_DON -> startActivity(Intent(this, Menu_CategoryActivity::class.java))
            SaleeContract.MenuScreen.BAO_CAO -> Toast.makeText(this, "Mở báo cáo", Toast.LENGTH_SHORT).show()
            SaleeContract.MenuScreen.DONG_BO_DU_LIEU -> Toast.makeText(this, "Đồng bộ dữ liệu", Toast.LENGTH_SHORT).show()
            SaleeContract.MenuScreen.THIET_LAP -> startActivity(Intent(this, Set_upActivity::class.java))
            SaleeContract.MenuScreen.LIEN_KET_TAI_KHOAN -> startActivity(Intent(this, Link_AccountActivity::class.java))
            SaleeContract.MenuScreen.THONG_BAO -> Toast.makeText(this, "Mở thông báo", Toast.LENGTH_SHORT).show()
            SaleeContract.MenuScreen.GIOI_THIEU_CHO_BAN -> Toast.makeText(this, "Giới thiệu cho bạn bè", Toast.LENGTH_SHORT).show()
            SaleeContract.MenuScreen.DANH_GIA_UNG_DUNG -> Toast.makeText(this, "Đánh giá ứng dụng", Toast.LENGTH_SHORT).show()
            SaleeContract.MenuScreen.GOP_Y_VOI_NHA_PHAT_TRIEN -> Toast.makeText(this, "Góp ý với nhà phát triển", Toast.LENGTH_SHORT).show()
            SaleeContract.MenuScreen.THONG_TIN_SAN_PHAM -> Toast.makeText(this, "Thông tin sản phẩm", Toast.LENGTH_SHORT).show()
            SaleeContract.MenuScreen.DAT_MAT_KHAU -> Toast.makeText(this, "Đặt mật khẩu", Toast.LENGTH_SHORT).show()
            SaleeContract.MenuScreen.DANG_XUAT -> Toast.makeText(this, "Đăng xuất", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMenuItemClicked(itemId: Int) {
        presenter.onMenuItemClicked(itemId)
    }

    override fun showInvoiceItems(items: List<SAInvoiceItem>) {
        // TODO: Hiển thị danh sách hóa đơn
        Toast.makeText(this, "Có ${items.size} hóa đơn", Toast.LENGTH_SHORT).show()
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
