package PACKAGE_UI

import android.os.Bundle

import org.scaloid.common._

import PACKAGE_R

object EditCLASS_NAME_AS_ISActivity {
  val RESULT_EDIT_OCCURRED: Int = 1
  val RESULT_NOTHING_CHANGED: Int = 2
}

class EditCLASS_NAME_AS_ISActivity extends SActivity {

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.activity_edit_CLASS_NAME_UNDERSCORED)
    if (savedInstanceState == null) {

      val fragment = new EditCLASS_NAME_AS_ISFragment()
      fragment.setArguments(getIntent().getExtras())

      getFragmentManager().beginTransaction()
        .add(R.id.edit_CLASS_NAME_UNDERSCORED_container, fragment)
        .commit()
    }
  }

}
