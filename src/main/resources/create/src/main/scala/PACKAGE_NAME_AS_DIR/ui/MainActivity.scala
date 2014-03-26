package PACKAGE_NAME.ui

import android.graphics.Color
import android.view.Menu
import android.view.MenuInflater

import org.scaloid.common._

import PACKAGE_NAME.R

class MainActivity extends SActivity {

  onCreate {
    setContentView(R.layout.activity_main);
  }
  
  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    
    getMenuInflater().inflate(R.menu.main, menu)
    true
  }

}
