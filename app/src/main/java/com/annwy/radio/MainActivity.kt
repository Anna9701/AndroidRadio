package com.annwy.radio

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
import com.annwy.radio.radioStations.RadioStationsContent
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, RadioStationFragment.OnListFragmentInteractionListener {
    private lateinit var currentCity: String

    override fun onListFragmentInteraction(item: RadioStationsContent.RadioStation?) {
        openRadioPlayerFragment(item!!.radioUrl, item.radioName)
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
        currentCity = PreferenceManager
            .getDefaultSharedPreferences(applicationContext)
            .getString("city_preference", "")
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

    private fun openRadioPlayerFragment(radioUrl: String, radioLabel: String) {
        val fragmentTransaction = supportFragmentManager.beginTransaction().addToBackStack(null)
        val bundle = Bundle()
        bundle.putString(RadioPlayer.RADIO_STATION_URL, radioUrl)
        bundle.putString(RadioPlayer.RADIO_STATION_LABEL, radioLabel)
        val radioPlayerFragment = RadioPlayer.newInstance(bundle)
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
