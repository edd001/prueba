package com.example.moviedb.base

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.*
import android.view.View.OnTouchListener
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.palette.graphics.Palette
import com.example.moviedb.R
import com.example.moviedb.databinding.ActivityBaseBinding
import com.example.moviedb.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.*


@AndroidEntryPoint
class BaseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBaseBinding
    private lateinit var appBarConfig: AppBarConfiguration
    private lateinit var navController: NavController
    val viewModel: BaseViewModel by viewModels()
    var defaultValue = 0xFFFFFF

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
        viewModel.items = resources.getStringArray(R.array.order_array)
        // clear FLAG_TRANSLUCENT_STATUS flag:
        this.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        this.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        viewModel.defaultCollapsedParams = binding.content.toolbar.layoutParams
        initBlur()
        initObservers()
    }

    private fun initObservers(){
        viewModel.backgroundForBlur.observe(this){
            viewModel.isReadyForNextChange = false
            GlobalScope.launch(Dispatchers.Main){ // launches coroutine in main thread
                updateBackground(it)
            }
        }
        viewModel.isCollapsedMode.observe(this){
            if (it){
                binding.content.svRoot.setOnTouchListener(null)
            } else {
                binding.content.appbarLayout.setExpanded(true, /*animate=*/true)
                binding.content.svRoot.setOnTouchListener(OnTouchListener { v, event -> true })
            }
        }
    }

    private suspend fun updateBackground(bitmap: Bitmap) {
        val value = GlobalScope.async { // creates worker thread
            withContext(Dispatchers.Main) {
                binding.drawer.background = BitmapDrawable(resources, bitmap)
                val builder = Palette.Builder(bitmap)
                val palette = builder.generate()
                this@BaseActivity.window.statusBarColor = palette.getDominantColor(defaultValue)
            }
        }
        println(value.await()) //waits for workerthread to finish
        viewModel.isReadyForNextChange = true //runs on ui thread as calling function is on Dispatchers.main
    }

    private fun initBlur(){
        val decorView = window.decorView
        val rootView = decorView.findViewById<View>(android.R.id.content) as ViewGroup
        val windowBackground = decorView.background

        binding.content.blurView.setupWith(rootView)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(Constants.RADIUS)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val spinnerAdapter = object : ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, viewModel.items) {
            override fun isEnabled(position: Int): Boolean {
                // Disable the first item from Spinner
                // First item will be used for hint
                return position != 0
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view: TextView = super.getDropDownView(position, convertView, parent) as TextView
                //set the color of first item in the drop down list to gray
                if(position == 0) {
                    view.setTextColor(Color.GRAY)
                } else {
                    view.setTextColor(Color.BLACK)
                }
                return view
            }
        }

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.content.spinner.adapter = spinnerAdapter

        binding.content.spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                view?.let {
                    if (parent?.getItemAtPosition(position).toString() == viewModel.items[0]){
                        (view as TextView).setTextColor(Color.GRAY)
                    } else {
                        (view as TextView).setTextColor(Color.WHITE)
                    }
                    viewModel.spinnerIndex.value = parent?.getItemAtPosition(position).toString()
                }
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

}