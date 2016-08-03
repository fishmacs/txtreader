package org.zw.ebook

import org.robolectric.annotation.Config
import org.scalatest.{FlatSpec, Matchers, RobolectricSuite}
import org.zw.ebook.model.BookDB

@Config(sdk=Array(21))
class DBSpec extends FlatSpec with Matchers with RobolectricSuite {
  //implicit val ctx = null

  "Book DB" should "open sucessfully" in {
    val db = new BookDB("/Users/zw/myproj/android/EBook/src/test/index.db")
    val app = db.getApp()
    app.name should be ("大清真相")
  }
}
