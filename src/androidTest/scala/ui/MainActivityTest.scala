package org.zw.ebook.ui

import android.test.ActivityInstrumentationTestCase2
//import org.scalatest.Matchers

class MainActivityTest(pkg: String, cls: Class[MainActivity]) extends ActivityInstrumentationTestCase2[MainActivity](pkg, cls) {
  override def setUp() {
    super.setUp()
    val app = getActivity().mBook.db.getApp()
    println(app.name)
  }
}
