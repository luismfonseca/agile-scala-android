package pt.testing.whaa.ui.comment

import android.os.Bundle

import org.scaloid.common._

import pt.testing.whaa.R

object EditCommentActivity {
  val RESULT_EDIT_OCCURRED: Int = 1
  val RESULT_NOTHING_CHANGED: Int = 2
}

class EditCommentActivity extends SActivity {

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.activity_edit_comment)
    if (savedInstanceState == null) {

      val fragment = new EditCommentFragment()
      fragment.setArguments(getIntent().getExtras())

      getFragmentManager().beginTransaction()
        .add(R.id.edit_comment_container, fragment)
        .commit()
    }
  }

}
