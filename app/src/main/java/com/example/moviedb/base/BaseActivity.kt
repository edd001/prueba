package com.example.moviedb.base

import android.R.attr
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.moviedb.R
import com.example.moviedb.databinding.ActivityBaseBinding
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import eightbitlab.com.blurview.RenderScriptBlur


@AndroidEntryPoint
class BaseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBaseBinding
    private lateinit var appBarConfig: AppBarConfiguration
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigationComponent()
        appBarConfig = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.favoritesFragment),
            binding.drawer)
        setupActionBar(navController, appBarConfig)
        setCustomHamburgerIcon()

        val radius = 20f

        val decorView = window.decorView
        val rootView = decorView.findViewById<View>(android.R.id.content) as ViewGroup
        val windowBackground = decorView.background

        binding.content.blurView.setupWith(rootView)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(radius)
            .setBlurAutoUpdate(true)
            .setHasFixedTransformationMatrix(true) // Or f

    }

    private fun isTopLevelDestination(id: Int): Boolean {
        var destination = false
        if (R.id.homeFragment == id) destination = true
        if (R.id.favoritesFragment == id) destination = true
        return destination
    }

    private fun setCustomHamburgerIcon() {
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (isTopLevelDestination(destination.id)){
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24)
            }else{
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_left_24)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = item.onNavDestinationSelected(navController)
            || super.onOptionsItemSelected(item)

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfig)
//        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
            binding.drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun changeBackground(bitmap: Bitmap?){
        val bd = BitmapDrawable(resources, bitmap)
        binding.drawer.setBackgroundDrawable(bd)
    }
    private fun setupNavigationComponent() {
        val drawerToggle = ActionBarDrawerToggle(this, binding.drawer, R.string.open, R.string.close)
        binding.drawer.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setSupportActionBar(binding.content.toolbar)
        setupWithNavController(binding.navView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    private fun setupActionBar(navController: NavController,
                               appBarConfig : AppBarConfiguration
    ) {
        setupActionBarWithNavController(navController, appBarConfig)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

}