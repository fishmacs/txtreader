import java.io.{File, RandomAccessFile}

import android.graphics.Paint
import android.graphics.pdf.PdfDocument.Page

import scala.collection.mutable.ArrayBuffer

import org.zw.ebook.config.BookConfig
import org.zw.ebook.util.IOHelper.use2

class PageManager(width: Int, height: Int, paint: Paint, pager: Pager) {
  private val leftPadding = 2
  private val topPadding = 2

  width -= 2 * leftPadding
  height -= 2 * topPadding

  private val fontMetrics = paint.getFontMetrics()
  private val fontHeight: Int = (fontMetrics.descent - fontMetrics.ascent).toInt
  private val fontWidth = paint.measureText("我").toInt
  private val readLength = (width / fontWidth) * (height / fontHeight) * 2

  private var nLine = 0
  private var nChar = 0

  def readForward(): Page = {
    if(lineBuffer.length < readLength) {
      pager.readForward(readLength, fontHeight)
    }
    val lines = getLines()
    pager.forwaredDone(nLine, nChar)
    new ContentPage(lines.mkString("\n"))
  }

  def readBackward: Page = {
    if(lineBuffer.length < readLength) {
      pager.readBackward(readLength, fontHeight)
    }
    val lines = getLines(true)
    if (getAdd1LinesHeight(lines) < height) {
      nLine = 0
      nChar = 0
      pager.backToFileHeadThenPrepareForward
      readForward
    } else {
      pager.backwardDone(nLine, nChar)
      new ContentPage(lines.mkString("\n"))
    }
  }

  private def getAdd1LinesHeight(lines: Seq[String]): Float = 
    lines.length * (fontHeight + BookConfig.lineSpace) + fontHeight

  private def getLines(backward: Boolean = false): ArrayBuffer[String] = {
    val it = pager.lineIterator
    val lines = new ArrayBuffer[String]()
    var h: Float = getAdd1LinesHeight(lines)
    while(h < height && it.hasNext) {
      splitLine(it.next(), lines, h - fontHeight, backward)
      h = getAdd1LinesHeight(lines)
    }
    lines
  }

  // private def splitLine(line: String, lines: ArrayBuffer[String], height1: Float): Unit = {
  //   val n = paint.breakText(line, true, width, null)
  //   val s = if(n < line.length) line.take(n) else line
  //   val lineHeight = fontHeight + BookConfig.lineSpace
  //   var currHeight = height1 + lineHeight
  //   lines += s
  //   if(n < line.length) {
  //     nChar += n
  //     if(currHeight + fontHeight < height)
  //       splitLine(line.drop(n), lines, currHeight)
  //   } else {
  //     nChar = 0
  //     nLine += 1
  //   }
  // }

  private def splitLine(line: String, lines: ArrayBuffer[String], height1: Float, backward: Boolean = false): Unit = {
    val sublines = allSubLines(line)
    nChar = 0
    nLine += 1
    val currHeight = height1 + (fontHeight + BookConfig.lineSpace) * (sublines.length - 1) + fontHeight
    if(currHeight > height) {
      val lineHeight = fontHeight + BookConfig.lineSpace
      val n = Math.ceil((currHeight - height) / lineHeight).toInt
      if(backward) {
        val (removed, remained) = sublines.splitAt(n)
        lines ++= remained
        nChar = removed.foldLeft(0) {(n, s) => n + s.length}
      } else {
        val sublines1 = sublines.dropRight(n)
        lines ++= sublines1
        nChar = sublines1.foldLeft(0) {(n, s) => n + s.length}
      }
      nLine -= 1
    }
  }

  private def allSubLines(line: String): ArrayBuffer[String] = {
    val lines = new ArrayBuffer[String]
    var line1 = line
    while(line1.length > 0) {
      val n = paint.breakText(line1, true, width, null)
      lines += line1.take(n)
      line1 = line1.drop(n)
    }
    lines
  }

  def prevPage(): Option[Page] = {
    if(pager.isFileHead)
      None
    else {
      nLine = 0
      nChar = 0
      pager.beforeBackward
      Some(readBackward)
    }
  }

  def nextPage(): Option[Page] = {
    if(pager.isFileEnd)
      None
    else {
      nLine = 0
      nChar = 0
      pager.beforeForwared
      Some(readForward)
    }
  }

  def refreshPage(): Page = {
    nLine = 0
    nChar = 0
    pager.resetPagePos
    readForward
  }
}

class Pager(txtFile: File, fileSize: Int, pos: Int = 0) {
  private val lineBuffer = new LineBuffer()
  private var pageHead = 0
  private var pageHeadPos = pos
  private var pageEndPos = pos
  private var filePos = pos
  private var fileEnd = false
  private var forwardLast = pos == 0

  def readForward(readLength: Int, fontHeight: Int): Unit = {
    use2(new RandomAccessFile(txtFile, "r")) { input =>
      var header = true
      if(filePos > 0) {
        input.seek(filePos - 1)
        if (input.read() != 10)
          header = false
      } else {
        input.seek(0)
      }
      var line = input.readLine
      while(line != null && lineBuffer.length < readLength) {
        lineBuffer.appendLine(line, header)
        line = input.readLine
        header = true
      }
    } { input =>
      filePos = input.getFilePointer.toInt
      fileEnd = filePos >= fileSize
      input.close()
    }
  }

  def readBackward(readLength: Int, fontHeight: Int): Unit = {
    pageHead = 0
    use2(new RandomAccessFile(txtFile, "r")) { input =>
      pageHead = Math.max(0, pageHeadPos - readLength)
      var breaked = false
      while(!breaked && pageHead > 0) {
        input.seek(pageHead)
        if (input.read == 10) {
          pageHead += 1
          breaked = true
        } else
          pageHead -= 1
      }
      if (pageHead == 0)
        input.seek(0)
      val size = pageHeadPos - pageHead
      var line = input.readLine
      while(line != null && lineBuffer.length < readLength) {
        lineBuffer.pushLine(line, size, true)
        line = input.readLine
      }
    } { input =>
      filePos = input.getFilePointer.toInt
      fileEnd = filePos >= fileSize
      input.close()
    }
  }

  def forwaredDone(nLine: Int, nChar: Int): Unit = {
    pageHeadPos = pageEndPos
    pageEndPos = filePos - lineBuffer.remainLength(nLine, nChar, true)
  }

  def backwardDone(nLine: Int, nChar: Int): Unit = {
    filePos = pageHeadPos
    pageEndPos = pageHeadPos
    pageHeadPos = pageHead + lineBuffer.remainLength(nLine, nChar, false)
  }

  def backToFileHeadThenPrepareForward(): Unit = {
    lineBuffer.clear
    filePos = 0
    pageEndPos = 0
    forwardLast = true
  }

  def beforeBackward(): Unit = {
    fileEnd = false
    lineBuffer.clear
    forwardLast = false
  }

  def beforeForwared(): Unit = {
    if(forwardLast)
      lineBuffer.reset(nLine, nChar)
    else
      lineBuffer.clear
    forwardLast = true
  }

  def lineIterator(): Iterator[String] = lineBuffer.iterator

  def pageEndPos(): Int = pageEndPos

  def pageHeadPos(): Int = pageHeadPos

  def isFileHead(): Boolean = pageHeadPos == 0

  def isFileEnd(): Boolean = fileEnd && pageEndPos == filePos

  def bookMarkTitle(): String = {
    lineBuffer.firstLine().take(Bookmark.titleSize)
  }

  def resetPagePos(): Unit = {
    pageEndPos = pageHeadPos
  }
}
