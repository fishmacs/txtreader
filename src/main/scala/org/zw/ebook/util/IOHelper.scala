package org.zw.ebook.util

import java.io._

/**
  * Created by zw on 16/8/2.
  */

object IOHelper {
  def use2[T](t: T)(block: T => Unit)(finalBlock: T => Unit): Unit = {
    try {
      block(t)
    } finally {
      finalBlock(t)
    }
  }
  
  def use[T <: { def close(): Unit }](closable: T)(block: T => Unit): Unit = {
    try {
      block(closable)
    } finally {
      closable.close()
    }
  }

  @throws(classOf[IOException])
  def copy(input: InputStream, output: OutputStream): Unit = {
    val buffer = new Array[Byte](8192)
    try {
      var l = input.read(buffer)
      while(l > 0) {
        output.write(buffer, 0, l)
        l = input.read(buffer)
      }
    } finally {
      output.close()
      input.close()
    }
  }

  @throws(classOf[IOException])
  def copy(input: InputStream, output: File): Unit = {
    copy(input, new BufferedOutputStream(new FileOutputStream(output), 8192))
  }

  @throws(classOf[IOException])
  def copy(input: File, output: File): Unit = {
    copy(new BufferedInputStream(new FileInputStream(input), 8192),
      new BufferedOutputStream(new FileOutputStream(output), 8192))
  }

  @throws(classOf[IOException])
  def copy(input: File, output: OutputStream): Unit = {
    copy(new BufferedInputStream(new FileInputStream(input), 8192), output)
  }
}
