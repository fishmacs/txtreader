package org.zw.ebook.model

import java.io.File
import java.util.Date

import android.content.Context
import io.getquill.{JdbcContext, SnakeCase, SqliteDialect}
import org.zw.ebook.util.IOHelper

/**
  * Created by zw on 16/7/21.
  */

case class App(name: String, app_id: String, version: String, created_time: Date, modified_time: Date)

case class Book(id: Int, name: String, length: Int)

case class Volume(id: Int, name: String, book_id: Int, file: String, length: Int)

case class Chapter(id: Int, name: String, start: Int, size: Int, offset: Int, length: Int, volume_id: Int, book_id: Int)

case class Bookmark(id: Int, text: String, offset: Int, created_time: Date, chapter_id: Int, chapter_name: String, volume_id: Int, book_id: Int)

class BookDB(private val dbFile: String)(implicit val ctx: Context = null) {
  private val dbPath =
    if (ctx != null) {
      val f = ctx.getDatabasePath(dbFile)
      if (!f.exists()) {
        val dir = f.getParentFile()
        if (!dir.exists())
          dir.mkdirs()
        IOHelper.copy(ctx.getAssets().open(dbFile), f)
      }
      f
    } else
      new File(dbFile)

  private val ds = new SqliteDataSource(dbPath.getPath())

  private val db = new JdbcContext[SqliteDialect, SnakeCase](ds)

  import db.{lift, query, quote}

  def getApp() = {
    val q = quote {
      query[App]
    }
    db.run(q)(0)
  }

  def getBook() = {
    val q = quote {
      query[Book]
    }
    db.run(q)(0)
  }

  def getVolumes(bookId: Int) = {
    val q = quote {
      query[Volume].filter(v => v.book_id == lift(bookId))
    }
    db.run(q)
  }

  def getChapters(volumeId: Int) = {
    val q = quote {
      query[Chapter].filter(c => c.volume_id == lift(volumeId))
    }
    db.run(q)
  }

  // def dbExists() = dbPath.exists()

  // def dbCopy(path: String): Unit = {

  // }
}

