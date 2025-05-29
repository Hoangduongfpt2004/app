package com.example.appquanly.SalePutIn

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.appquanly.*
import com.example.appquanly.LinkAccount.Link_AccountActivity
import com.example.appquanly.Login.LoginActivity
import com.example.appquanly.MenuCategory.Menu_CategoryActivity
import com.example.appquanly.ProductInformation.ProductlnformationActivity
import com.example.appquanly.Report.ReportActivity
import com.example.appquanly.Setup.Set_upActivity
import com.example.appquanly.Sychronize.SychronizeActivity
import com.example.appquanly.salee.SaleeActivity
import com.google.android.material.navigation.NavigationView

abstract class BaseDrawerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    protected lateinit var drawerLayout: DrawerLayout
    protected lateinit var navigationView: NavigationView
    protected lateinit var toolbar: Toolbar

    abstract fun getLayoutId(): Int
    abstract fun getNavigationMenuItemId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())

        toolbar = findViewById(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open, R.string.close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)
        navigationView.setCheckedItem(getNavigationMenuItemId())
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.ban_hang -> {
                if (this !is SaleeActivity) {
                    startActivity(Intent(this, SaleeActivity::class.java))
                    finish()
                }
            }
            R.id.thuc_don -> {
                if (this !is Menu_CategoryActivity) {
                    startActivity(Intent(this, Menu_CategoryActivity::class.java))
                    finish()
                }
            }
            R.id.bao_cao -> {
                if (this !is ReportActivity) {
                    startActivity(Intent(this, ReportActivity::class.java))
                    finish()
                }
            }
            R.id.dong_bo_du_lieu -> {
                if (this !is SychronizeActivity) {
                    startActivity(Intent(this, SychronizeActivity::class.java))
                    finish()
                }
            }
            R.id.thiet_lap -> {
                if (this !is Set_upActivity) {
                    startActivity(Intent(this, Set_upActivity::class.java))
                    finish()
                }
            }
            R.id.lien_ket_tai_khoan -> {
                if (this !is Link_AccountActivity) {
                    startActivity(Intent(this, Link_AccountActivity::class.java))
                    finish()
                }
            }
            R.id.thong_tin_san_pham -> {
                if (this !is ProductlnformationActivity) {
                    startActivity(Intent(this, ProductlnformationActivity::class.java))
                    finish()
                }
            }
            R.id.dang_xuat -> {
                startActivity(Intent(this, LoginActivity::class.java))
                finishAffinity()
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
