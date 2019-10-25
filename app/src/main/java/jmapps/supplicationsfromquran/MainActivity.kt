package jmapps.supplicationsfromquran

import android.database.sqlite.SQLiteDatabase
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import jmapps.supplicationsfromquran.data.database.DatabaseLists
import jmapps.supplicationsfromquran.data.database.DatabaseOpenHelper
import jmapps.supplicationsfromquran.presentation.mvp.main.MainContract
import jmapps.supplicationsfromquran.presentation.mvp.main.MainPresenterImpl
import jmapps.supplicationsfromquran.presentation.ui.about.BottomSheetAboutUs
import jmapps.supplicationsfromquran.presentation.ui.main.MainAdapter
import jmapps.supplicationsfromquran.presentation.ui.main.MainModel
import jmapps.supplicationsfromquran.presentation.ui.settings.BottomSheetSettings
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), MainContract.MainView, MainAdapter.FindButtons,
    View.OnClickListener, CompoundButton.OnCheckedChangeListener, MainAdapter.PlayItem {

    private lateinit var database: SQLiteDatabase

    private lateinit var mainContentList: MutableList<MainModel>
    private lateinit var mainAdapter: MainAdapter

    private lateinit var mainPresenterImpl: MainPresenterImpl

    private var player: MediaPlayer? = null
    private lateinit var runnable: Runnable
    private var handler: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        mainPresenterImpl = MainPresenterImpl(this, this)

        openDatabase()
        initMainContent()

        btnPrevious.setOnClickListener(this)
        tbPlayPause.setOnCheckedChangeListener(this)
        btnNext.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_settings -> mainPresenterImpl.getSettings()

            R.id.action_about_us -> mainPresenterImpl.getAboutUs()

            R.id.action_rate -> mainPresenterImpl.rateApp()

            R.id.action_share -> mainPresenterImpl.shareAppLink()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        closeDatabase()
    }

    override fun openDatabase() {
        database = DatabaseOpenHelper(this).readableDatabase
    }

    override fun closeDatabase() {
        database.close()
    }

    override fun initMainContent() {
        mainContentList = DatabaseLists(this).getContentList
        val verticalLayout = LinearLayoutManager(this)
        rvMainContent.layoutManager = verticalLayout
        mainAdapter = MainAdapter(mainContentList, this, this)
        rvMainContent.adapter = mainAdapter
    }

    override fun getSettings() {
        BottomSheetSettings().show(supportFragmentManager, "settings")
    }

    override fun getAboutUs() {
        BottomSheetAboutUs().show(supportFragmentManager, "about_us")
    }

    override fun findButtons(btnCopy: Button, btnShare: Button) {
        btnCopy.setOnClickListener(this)
        btnShare.setOnClickListener(this)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (!isChecked) {
            mainAdapter.onItemSelected(-1)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {

            R.id.btnCopy -> {Toast.makeText(this, "Copy", Toast.LENGTH_LONG).show()}

            R.id.btnShare -> {Toast.makeText(this, "Share", Toast.LENGTH_LONG).show()}

            R.id.btnPrevious -> {Toast.makeText(this, "Previous", Toast.LENGTH_LONG).show()}

            R.id.btnNext -> {Toast.makeText(this, "Next", Toast.LENGTH_LONG).show()}
        }
    }

    override fun playItem(position: Int) {
        mainAdapter.onItemSelected(position)
        tbPlayPause.isChecked = position != -1
    }

    private fun clear() {
        player?.stop()
        player?.release()
    }

//    val resId: Int? = resources?.getIdentifier(
//        mainContentList[1].strNameAudio, "raw", "jmapps.supplicationsfromquran")
//    player = MediaPlayer.create(this, resId!!)
//    player?.start()
}