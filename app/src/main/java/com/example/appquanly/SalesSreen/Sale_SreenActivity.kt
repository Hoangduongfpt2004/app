package com.example.appquanly.SalesSreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.appquanly.Report.ReportActivity
import com.example.appquanly.Sychronize.SychronizeActivity
import com.example.appquanly.R
import com.example.appquanly.ChooseDish.view.ChooseDishActivity
import com.example.appquanly.Setup.Set_upActivity
import com.example.appquanly.MenuCategory.Menu_CategoryActivity
import com.example.appquanly.Login.LoginActivity
import com.google.android.material.navigation.NavigationView

class Sale_SreenActivity : AppCompatActivity(), Sale_SreenContract.View {

    private lateinit var toolbar: Toolbar
    private lateinit var tvHint: TextView
    private lateinit var presenter: Sale_SreenContract.Presenter
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sale)

        // Ánh xạ view
        toolbar = findViewById(R.id.toolbar)
        tvHint = findViewById(R.id.tvHint)
        drawerLayout = findViewById(R.id.layoutMain)
        navigationView = findViewById(R.id.navigationView)

        // Gán presenter
        presenter = Sale_SreenPresenter(this)

        // Thiết lập toolbar
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open,
            R.string.close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(navigationView)
        }

        // Bắt sự kiện
        setupEvents()
        showNoOrders()
    }

    private fun setupEvents() {
        tvHint.setOnClickListener {
            presenter.onHintClicked()
        }

        navigationView.setNavigationItemSelectedListener { item ->
            presenter.onMenuItemClicked(item.itemId)
            drawerLayout.closeDrawers()
            true
        }
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
        val intent = Intent(this, ChooseDishActivity::class.java)
        startActivity(intent)
    }

    override fun showMenu() {
        Toast.makeText(this, "Mở menu", Toast.LENGTH_SHORT).show()
    }

    override fun navigateTo(screen: Sale_SreenContract.MenuScreen) {
        when (screen) {
            Sale_SreenContract.MenuScreen.BAN_HANG -> Ban_Hang()
            Sale_SreenContract.MenuScreen.THUC_DON -> Thuc_don()
            Sale_SreenContract.MenuScreen.BAO_CAO -> Bao_Cao()
            Sale_SreenContract.MenuScreen.DONG_BO_DU_LIEU -> Dong_Bo_Du_Lieu()
            Sale_SreenContract.MenuScreen.THIET_LAP -> Thiet_Lap()
            Sale_SreenContract.MenuScreen.LIEN_KET_TAI_KHOAN -> Lien_Lien_Ket_Tai_Khoan()
            Sale_SreenContract.MenuScreen.THONG_BAO -> Thong_Bao()
            Sale_SreenContract.MenuScreen.GIOI_THIEU_CHO_BAN -> Gioi_Thieu()
            Sale_SreenContract.MenuScreen.DANH_GIA_UNG_DUNG -> Danh_Gia_Ung_Dung()
            Sale_SreenContract.MenuScreen.GOP_Y_VOI_NHA_PHAT_TRIEN -> Gop_Y()
            Sale_SreenContract.MenuScreen.THONG_TIN_SAN_PHAM -> Thong_Tin_San_Pham()
            Sale_SreenContract.MenuScreen.DAT_MAT_KHAU -> Dat_Mat_Khau()
            Sale_SreenContract.MenuScreen.DANG_XUAT -> Dang_Xuat()
        }
    }

    fun Ban_Hang() {
        val intent = Intent(this, Sale_SreenActivity::class.java)
        startActivity(intent)
    }

    fun Thuc_don() {
        val intent = Intent(this, Menu_CategoryActivity()::class.java)
        startActivity(intent)
    }

    fun Bao_Cao() {
        val intent = Intent(this, ReportActivity::class.java)
        startActivity(intent)
    }

    fun Dong_Bo_Du_Lieu() {
        val intent = Intent(this, SychronizeActivity::class.java)
        startActivity(intent)
    }

    fun Thiet_Lap() {
        val intent = Intent(this, Set_upActivity::class.java)
        startActivity(intent)
    }

    fun Lien_Lien_Ket_Tai_Khoan() {
        val intent = Intent(this, Lien_Lien_Ket_Tai_Khoan()::class.java)
        startActivity(intent)
    }

    fun Thong_Bao() {
        val intent = Intent(this, Thong_Bao()::class.java)
        startActivity(intent)
    }

    fun Gioi_Thieu() {
        val intent = Intent(this, Gioi_Thieu()::class.java)
        startActivity(intent)
    }

    fun Danh_Gia_Ung_Dung() {
        val intent = Intent(this, Danh_Gia_Ung_Dung()::class.java)
        startActivity(intent)
    }

    fun Gop_Y() {
        val intent = Intent(this, Gop_Y()::class.java)
        startActivity(intent)
    }

    fun Thong_Tin_San_Pham() {
        val intent = Intent(this, Thong_Tin_San_Pham()::class.java)
        startActivity(intent)
    }

    fun Dat_Mat_Khau() {
        val intent = Intent(this, Dat_Mat_Khau()::class.java)
        startActivity(intent)
    }

    fun Dang_Xuat() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    // Không cần xử lý gì thêm ở đây
    override fun onMenuItemClicked(itemId: Int) {
        when (itemId) {
            R.id.ban_hang -> Ban_Hang()
            R.id.thuc_don -> Thuc_don()
            R.id.bao_cao -> Bao_Cao()
            R.id.dong_bo_du_lieu -> Dong_Bo_Du_Lieu()
            R.id.thiet_lap -> Thiet_Lap()
            R.id.lien_ket_tai_khoan -> Lien_Lien_Ket_Tai_Khoan()
            R.id.thong_bao -> Thong_Bao()
            R.id.gioi_thieu -> Gioi_Thieu()
            R.id.danh_gia_ung_dung -> Danh_Gia_Ung_Dung()
            R.id.gop_y -> Gop_Y()
            R.id.thong_tin_san_pham -> Thong_Tin_San_Pham()
            R.id.dat_mat_khau -> Dat_Mat_Khau()
            R.id.dang_xuat -> Dang_Xuat()
        }
    }
}
