package pt.test.ok.ui

import android.graphics.Color
import android.view.Menu
import android.view.MenuInflater

import org.scaloid.common._

import pt.test.ok.R

class MainActivity extends SActivity {

  onCreate {
    setContentView(R.layout.activity_main)
  }
  
  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    
    getMenuInflater().inflate(R.menu.main, menu)
    true
  }

}
