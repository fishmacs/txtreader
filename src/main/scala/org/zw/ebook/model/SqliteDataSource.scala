package org.zw.ebook.model

import java.io.{Closeable, FileNotFoundException, PrintWriter}
import java.sql.{DriverManager, SQLException}
import java.util.Properties
import javax.sql.DataSource

import org.sqldroid.SQLDroidDriver

class SqliteDataSource(path: String) extends DataSource with Closeable {
  @throws(classOf[SQLException])
  override def getConnection = {
    val url = "jdbc:sqldroid:" + path
    println(url)
    new SQLDroidDriver().connect(url, new Properties())
  }

  @throws(classOf[SQLException])
  override def getConnection(username: String, password: String) = getConnection

  @throws(classOf[SQLException])
  override def getLoginTimeout: Int = 0

  @throws(classOf[SQLException])
  override def setLoginTimeout(seconds: Int): Unit = {}

  @throws(classOf[SQLException])
  override def getLogWriter = {
    try {
      new PrintWriter("droid.log")
    } catch {
      case e: FileNotFoundException =>
        e.printStackTrace()
        null
    }
  }

  @throws(classOf[SQLException])
  override def setLogWriter(out: PrintWriter): Unit = {
    try {
      DriverManager.setLogWriter(new PrintWriter("droid.log"))
    } catch {
      case e: FileNotFoundException =>
        e.printStackTrace()
    }
  }

  override def unwrap[T](iface: Class[T]): T =
    throw new UnsupportedOperationException("unwrap")

  override def isWrapperFor(iface: Class[_]): Boolean =
    throw new UnsupportedOperationException("isWrapperFor")

  override def close(): Unit = {}
}
