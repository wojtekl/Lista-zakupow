package com.gmail.lesniakwojciech.listazakupowa

import android.Manifest
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.transition.Explode
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.gmail.lesniakwojciech.commons.*
import java.util.*

class ActivityMain : AppCompatActivity(), RepositoryListaZakupow.IRepository {
    private lateinit var repository: RepositoryListaZakupow

    override fun onCreate(savedInstanceState: Bundle?) {
        /*val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/

        val ustawienia = Ustawienia(this)
        if (ustawienia.getSkorkaCiemna(false)) {
            setTheme(R.style.AppThemeNight)
        }
        super.onCreate(savedInstanceState)
        if (Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT) {
            Utils.ustawAnimacje(window, Explode(), Explode())
        }
        setContentView(R.layout.activitymain)

        Reklamy.initialize(this)
        Reklamy.banner(findViewById(R.id.adView),
            !Zetony(this).sprawdzZetony(Zetony.ZETONY_BANNER_WYLACZ, false, null),
            object : Reklamy.Listener {
                override fun onRewarded(amount: Int) {
                    Zetony(applicationContext).dodajZetony(
                        Zetony.ZETONY_BANNER,
                        findViewById(R.id.main)
                    )
                }
            })

        repository = RepositoryListaZakupow(this)

        setSupportActionBar(findViewById(R.id.toolbar))

        val viewPager = findViewById<ViewPager>(R.id.alzViewPager)
        viewPager.adapter = FPagerAdapterMain(supportFragmentManager, resources)
        FPagerAdapterMain.tabLayout(viewPager, findViewById(R.id.alzTabLayout), resources, 1)

        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT) {
            Permissions.requestPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission_group.STORAGE
            )
        }

        if (TextUtils.isEmpty(ustawienia.getKraj(""))) {
            ustawienia.setKraj(Utils.pobierzKraj(this))
        }

        if (TextUtils.isEmpty(ustawienia.getIdentyfikator(""))) {
            ustawienia.setIdentyfikator(UUID.randomUUID().toString())
        }

        NotificationMain.createNotificationChannel(this)

        WebAPI.ustawAPIAdress(this)
    }

    override fun onResume() {
        super.onResume()

        Reklamy.resume(this)
        repository.get()
    }

    override fun onPause() {
        AWProviderListaZakupow.update(this)
        createShortcuts()
        repository.update()
        Reklamy.pause(this)

        super.onPause()
    }

    override fun onDestroy() {
        Reklamy.destroy(this)

        super.onDestroy()
    }

    override fun getRepository(): RepositoryListaZakupow {
        return repository
    }

    private fun createShortcuts() {
        if (Build.VERSION_CODES.N_MR1 > Build.VERSION.SDK_INT) {
            return
        }
        val intent = Wiadomosci.tekst(
            this.packageManager, RepositoryListaZakupow(this)
                .doWyslania(getString(R.string.do_kupienia))
        )
        if (null != intent) {
            val label = getString(R.string.wyslij_liste_SMSem)
            getSystemService(ShortcutManager::class.java)!!.dynamicShortcuts = listOf(
                ShortcutInfo
                    .Builder(this, getString(R.string.app_name))
                    .setShortLabel(label)
                    .setLongLabel(label)
                    .setIcon(Icon.createWithResource(this, R.drawable.ic_launcher))
                    .setIntent(intent)
                    .build()
            )
        }
    }
}
