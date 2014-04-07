package pt.teste.ok.ui.post

import android.os.Bundle

import org.scaloid.common._

import pt.teste.ok.R

object EditPostActivity {
  val RESULT_EDIT_OCCURRED: Int = 1
  val RESULT_NOTHING_CHANGED: Int = 2
}

class EditPostActivity extends SActivity {

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.activity_edit_post)
    if (savedInstanceState == null) {

      val fragment = new EditPostFragment()
      fragment.setArguments(getIntent().getExtras())

      getFragmentManager().beginTransaction()
        .add(R.id.edit_post_container, fragment)
        .commit()
    }
  }

}
