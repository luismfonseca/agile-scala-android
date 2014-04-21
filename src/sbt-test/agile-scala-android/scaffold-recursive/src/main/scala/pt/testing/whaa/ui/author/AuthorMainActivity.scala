package pt.testing.whaa.ui.author

import android.app.Fragment
import android.app.FragmentManager
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import org.scaloid.common._

import pt.testing.whaa.ui.ChangeToFragmentHandler
import pt.testing.whaa.R

class AuthorMainActivity
  extends SActivity
  with AuthorFragment.AuthorDeleteHandler
  with ChangeToFragmentHandler
  with FragmentManager.OnBackStackChangedListener {

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_author)

    if (savedInstanceState == null) {
        getFragmentManager()
          .beginTransaction()
          .add(R.id.author_main_container, new AuthorListFragment())
          .commit()
    }
    getFragmentManager().addOnBackStackChangedListener(this)
  }
  
  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    setHomeButton()

    val currentFragment = getFragmentManager().findFragmentById(R.id.author_main_container)

    if (currentFragment.getClass() == classOf[AuthorListFragment])
    {
        getMenuInflater().inflate(R.menu.main_author, menu)
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

      case R.id.menu_main_new_author => {

        val intent = new Intent(this, classOf[EditAuthorActivity])
        intent.putExtra(EditAuthorFragment.BUNDLE_CREATE_NEW, true)

        startActivity(intent)
        true
      }
      case _ => super.onOptionsItemSelected(menuItem)
    }
  }


  override def onAuthorDeleteHandler(): Unit = {
    getFragmentManager().popBackStack()
  }

  override def onChangeToFragment(fragment: Fragment): Unit = {
    getFragmentManager().beginTransaction()
      .setCustomAnimations(R.animator.slide_in, R.animator.slide_out)
      .replace(R.id.author_main_container, fragment)
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
