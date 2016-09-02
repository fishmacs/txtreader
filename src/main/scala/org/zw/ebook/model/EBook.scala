package org.zw.ebook.model

import java.io.File
import java.util.Date

/**
  * Created by zw on 16/7/29.
  */

class EVolume(volume: Volume) {
  def id(): Int = volume.id
  def name(): String = volume.name
  def length(): Int = volume.length
  def contentFile(): File = new File(volume.file)
}

class EChapter(chapter: Chapter) {
  def id(): Int = chapter.id
  def name(): String = chapter.name
  def start(): Int = chapter.start
  def length(): Int = chapter.size
  def fileOffset(): Int = chapter.offset
  def compressedLength(): Int = chapter.length
}

class EBookmark(bookmark: Bookmark) {
  def id(): Int = bookmark.id
  def name(): String = bookmark.text
  def offset(): Int = bookmark.offset
  def chapId(): Int = bookmark.chapter_id
  def chapName(): String = bookmark.chapter_name
  def volumeId(): Int = bookmark.volume_id
  def createdTime: Date = bookmark.created_time
}

class EBook(db: BookDB) {
  private val book = db.getBook()
  val id = book.id
  val name = book.name
  val length = book.length

  val volumes = loadVolumes()
  val chapters = loadChapters()

  val bookmarks = loadBookmarks()

  def loadVolumes(): List[EVolume] = {
    val volumes = db.getVolumes(id)
    volumes.map(v => new EVolume(v))
  }

  def loadChapters(): Array[EChapter] = {
    val chapters = db.getChapters(volumes.head.id)
    chapters.map(c => new EChapter(c)).toArray
  }

  def loadBookmarks(): List[EBookmark] = {
    val bookmarks = db.getBookmarks(volumes.head.id)
    bookmarks.map(b => new EBookmark(b))
  }
}
