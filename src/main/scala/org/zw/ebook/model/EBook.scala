package org.zw.ebook.model

/**
  * Created by zw on 16/7/29.
  */
class EBook(val db: BookDB) {

  def load() {
    val book = db.getBook()
  }
}
