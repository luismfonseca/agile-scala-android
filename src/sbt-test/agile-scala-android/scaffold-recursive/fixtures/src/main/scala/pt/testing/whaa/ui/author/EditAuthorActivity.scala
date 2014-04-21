package pt.testing.whaa.ui.author

import android.os.Bundle

import org.scaloid.common._

import pt.testing.whaa.R

object EditAuthorActivity {
  val RESULT_EDIT_OCCURRED: Int = 1
  val RESULT_NOTHING_CHANGED: Int = 2
}

class EditAuthorActivity extends SActivity {

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.activity_edit_author)
    if (savedInstanceState == null) {

      val fragment = new EditAuthorFragment()
      fragment.setArguments(getIntent().getExtras())

      getFragmentManager().beginTransaction()
        .add(R.id.edit_author_container, fragment)
        .commit()
    }
  }

}
