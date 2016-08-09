package org.zw.ebook.ui

import android.os.Bundle
import org.scaloid.common._
import org.zw.ebook.TypedFindView
import org.zw.ebook.config.{BookConfig => BC}
import org.zw.ebook.model.{EBook, BookDB}

class MainActivity extends SActivity with TypedFindView {
  lazy val mText = new STextView(getApplicationInfo().dataDir)
  lazy val mBook = new EBook(new BookDB("index.db"))

  /** Called when the activity is first created. */
  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    val app = mBook.db.getApp()
    contentView = new SVerticalLayout {
      mText.here.fill
       .text(app.name)
       .textColor(BC.textColor)
       .backgroundColor(BC.bgColor)
       .textSize(BC.fontSize.sp)
       .padding(2.dip)
    }.padding(10.dip)
    mText.setLineSpacing(BC.lineSpaceExtra, BC.lineSpaceMulti)
    mText.setShadowLayer(BC.fontShadowRadius, BC.fontShadowX, BC.fontShadowY, BC.textColor)
  }
}
