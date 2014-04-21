package pt.testing.whaa.ui.comment

import android.app.Fragment
import android.app.FragmentManager
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import org.scaloid.common._

import pt.testing.whaa.ui.ChangeToFragmentHandler
import pt.testing.whaa.R

class CommentMainActivity
  extends SActivity
  with CommentFragment.CommentDeleteHandler
  with ChangeToFragmentHandler
  with FragmentManager.OnBackStackChangedListener {

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_comment)

    if (savedInstanceState == null) {
        getFragmentManager()
          .beginTransaction()
          .add(R.id.comment_main_container, new CommentListFragment())
          .commit()
    }
    getFragmentManager().addOnBackStackChangedListener(this)
  }
  
  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    setHomeButton()

    val currentFragment = getFragmentManager().findFragmentById(R.id.comment_main_container)

    if (currentFragment.getClass() == classOf[CommentListFragment])
    {
        getMenuInflater().inflate(R.menu.main_comment, menu)
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

      case R.id.menu_main_new_comment => {

        val intent = new Intent(this, classOf[EditCommentActivity])
        intent.putExtra(EditCommentFragment.BUNDLE_CREATE_NEW, true)

        startActivity(intent)
        true
      }
      case _ => super.onOptionsItemSelected(menuItem)
    }
  }


  override def onCommentDeleteHandler(): Unit = {
    getFragmentManager().popBackStack()
  }

  override def onChangeToFragment(fragment: Fragment): Unit = {
    getFragmentManager().beginTransaction()
      .setCustomAnimations(R.animator.slide_in, R.animator.slide_out)
      .replace(R.id.comment_main_container, fragment)
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
