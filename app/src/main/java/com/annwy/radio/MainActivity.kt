package com.annwy.radio

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.annwy.radio.R
import com.annwy.radio.R.id.action_settings
import com.annwy.radio.models.RadioStation
import com.annwy.radio.radioStations.RadioStationsContent
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    RadioStationFragment.OnListFragmentInteractionListener, RadioPlayer.OnFragmentInteractionListener {
    private lateinit var currentCity: String
    private var currentPlayerServiceIntent: Intent? = null

    override fun onListFragmentInteraction(item: RadioStation?) {
        openRadioPlayerFragment(item!!)
    }

    override fun onPlayPlayerFragmentInteraction(playerIntent: Intent) {
        currentPlayerServiceIntent = playerIntent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setTitleToCurrentCity()

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        openRadioStationsListFragment()
    }

    private fun setTitleToCurrentCity() {
        if (!PreferenceManager.getDefaultSharedPreferences(applicationContext).contains("city_preference")) {
            PreferenceManager.setDefaultValues(applicationContext, R.xml.pref_general, false)
        }
        currentCity = PreferenceManager
            .getDefaultSharedPreferences(applicationContext)
            .getString("city_preference", String())
        title = currentCity
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        menu.findItem(R.id.action_settings).setOnMenuItemClickListener {
            startActivity(Intent(applicationContext, SettingsActivity::class.java))
            true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_stations -> {
                openRadioStationsListFragment()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun openRadioPlayerFragment(radioStation: RadioStation) {
        val fragmentTransaction = supportFragmentManager.beginTransaction().addToBackStack(null)
        val radioPlayerFragment = RadioPlayer.newInstance(radioStation, currentPlayerServiceIntent)
        fragmentTransaction.replace(R.id.main_content, radioPlayerFragment)
        fragmentTransaction.commit()
    }

    private fun openRadioStationsListFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val radioStationsFragment = RadioStationFragment.newInstance(currentCity)
        fragmentTransaction.replace(R.id.main_content, radioStationsFragment)
        fragmentTransaction.commit()
    }
}
