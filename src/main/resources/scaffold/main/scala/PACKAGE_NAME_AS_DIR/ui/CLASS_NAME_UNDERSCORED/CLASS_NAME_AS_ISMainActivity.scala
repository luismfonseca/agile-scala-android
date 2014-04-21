package PACKAGE_UI.CLASS_NAME_UNDERSCORED

import android.app.Fragment
import android.app.FragmentManager
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import org.scaloid.common._

import PACKAGE_UI.ChangeToFragmentHandler
import PACKAGE_R

class CLASS_NAME_AS_ISMainActivity
  extends SActivity
  with CLASS_NAME_AS_ISFragment.CLASS_NAME_AS_ISDeleteHandler
  with ChangeToFragmentHandler
  with FragmentManager.OnBackStackChangedListener {

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_CLASS_NAME_UNDERSCORED)

    if (savedInstanceState == null) {
        getFragmentManager()
          .beginTransaction()
          .add(R.id.CLASS_NAME_UNDERSCORED_main_container, new CLASS_NAME_AS_ISListFragment())
          .commit()
    }
    getFragmentManager().addOnBackStackChangedListener(this)
  }
  
  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    setHomeButton()

    val currentFragment = getFragmentManager().findFragmentById(R.id.CLASS_NAME_UNDERSCORED_main_container)

    if (currentFragment.getClass() == classOf[CLASS_NAME_AS_ISListFragment])
    {
        getMenuInflater().inflate(R.menu.main_CLASS_NAME_UNDERSCORED, menu)
        return true
    }
    return super.onCreateOptionsMenu(menu)
  }

  override def onOptionsItemSelected(menuItem: MenuItem): Boolean = {
    menuItem.getItemId() match {
      case android.R.id.home => {
        getFragmentManager().popBackStack()

        true
      }

      case R.id.menu_main_new_CLASS_NAME_UNDERSCORED => {

        val intent = new Intent(this, classOf[EditCLASS_NAME_AS_ISActivity])
        intent.putExtra(EditCLASS_NAME_AS_ISFragment.BUNDLE_CREATE_NEW, true)

        startActivity(intent)
        true
      }
      case _ => super.onOptionsItemSelected(menuItem)
    }
  }


  override def onCLASS_NAME_AS_ISDeleteHandler(): Unit = {
    getFragmentManager().popBackStack()
  }

  override def onChangeToFragment(fragment: Fragment): Unit = {
    getFragmentManager().beginTransaction()
      .setCustomAnimations(R.animator.slide_in, R.animator.slide_out)
      .replace(R.id.CLASS_NAME_UNDERSCORED_main_container, fragment)
      .addToBackStack(null)
      .commit()
  }

  override def onBackStackChanged(): Unit = {
    setHomeButton()
  }
  
  def setHomeButton(): Unit = {
    val enableBack = getFragmentManager().getBackStackEntryCount() != 0

    getActionBar().setHomeButtonEnabled(enableBack)
    getActionBar().setDisplayHomeAsUpEnabled(enableBack)
  }

}
