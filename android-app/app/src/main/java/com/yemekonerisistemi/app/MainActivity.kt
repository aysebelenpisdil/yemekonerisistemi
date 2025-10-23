package com.yemekonerisistemi.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Navigation kurulumu
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Bottom Navigation kurulumu (opsiyonel)
        findViewById<BottomNavigationView>(R.id.bottom_navigation)?.let {
            it.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_inventory -> {
                        // Buzdolabım ekranına git
                        navController.navigate(R.id.inventoryFragment)
                        true
                    }
                    R.id.navigation_recipes -> {
                        // Tarifler ekranına git
                        navController.navigate(R.id.recipeListFragment)
                        true
                    }
                    R.id.navigation_profile -> {
                        // Profil ekranına git
                        navController.navigate(R.id.profileFragment)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}