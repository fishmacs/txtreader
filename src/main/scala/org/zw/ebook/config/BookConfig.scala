package org.zw.ebook.config

import scala.collection.mutable.BitSet

/**
  * Created by zw on 16/7/18.
  */
object BookConfig {
  // bitset bits
  private val flagDayNightMode = 0
  private val flagFontSize = 1
  private val flagFontShadow = 2
  private val flagDayTextColor = 3
  private val flagDayBgColor = 4
  private val flagNightTextColor = 5
  private val flagNightBgColor = 6
  private val flagLineSpaceExtra = 7
  private val flagLineSpaceMulti = 8
  private val flagProgFontSize = 9

  // bitset of modified flags
  private var modifiedSet = BitSet()

  def clearModifiedFlags: Unit = modifiedSet.clear()

  // fields

  private var _nightMode = false

  def nightMode = _nightMode
  def nightMode_=(b: Boolean): Unit = {
    _nightMode = b
    modifiedSet += flagDayNightMode
  }

  private var _fontSize = 18

  def fontSize = _fontSize
  def fontSize_=(size: Int): Unit = {
    _fontSize = size
    modifiedSet += flagFontSize
  }

  var _lineSpaceExtra = 3f

  def lineSpaceExtra = _lineSpaceExtra
  def lineSpaceExtra_=(n: Int): Unit = {
    _lineSpaceExtra = n
    modifiedSet += flagLineSpaceExtra
  }

  var _lineSpaceMulti = 1.0f

  def lineSpaceMulti = _lineSpaceMulti
  def lineSpaceMulti_=(n: Int): Unit = {
    _lineSpaceMulti = n
    modifiedSet += flagLineSpaceMulti
  }

  def lineSpace = _lineSpaceMulti * _lineSpaceExtra

  private var _dayTextColor = 0xff000000

  def dayTextColor = _dayTextColor
  def dayTextColor_=(color: Int): Unit = {
    _dayTextColor = color
    modifiedSet += flagDayTextColor
  }

  private var _dayBgColor = 0xffffffc0

  def dayBgColor = _dayBgColor
  def dayBgColor_=(color: Int): Unit = {
    _dayBgColor = color
    modifiedSet += flagDayBgColor
  }

  private var _nightTextColor = 0xffffc0

  def nightTextColor = _nightTextColor
  def nightTextColor_=(color: Int): Unit = {
    _nightTextColor = color
    modifiedSet += flagNightTextColor
  }

  private var _nightBgColor = 0

  def nightBgColor = _nightBgColor
  def nightBgColor_=(color: Int): Unit = {
    _nightBgColor = color
    modifiedSet += flagNightBgColor
  }

  def textColor =
    if (nightMode)
      _nightTextColor
    else
      _dayTextColor

  def textColor_=(color: Int): Unit = {
    if(nightMode)
      nightTextColor = color
    else
      dayTextColor = color
  }

  def bgColor =
    if(nightMode)
      _nightBgColor
    else
      _dayBgColor

  def bgColor_=(color: Int): Unit = {
    if(nightMode)
      nightBgColor = color
    else
      dayBgColor = color
  }

  private var _fontShadowX = 0f
  private var _fontShadowY = 0f
  private var _fontShadowRadius = 0f

  def fontShadowX = _fontShadowX
  def fontShadowY = _fontShadowY
  def fontShadowRadius = _fontShadowRadius

  def fontShadow_=(x: Float, y: Float, radius: Float): Unit = {
    _fontShadowX = x
    _fontShadowY = y
    _fontShadowRadius = radius
    modifiedSet += flagFontShadow
  }

  private var _progFontSize = 14

  def progressFontSize = _progFontSize
  def progressFontSize_=(size: Int) {
    _progFontSize = size
    modifiedSet += flagProgFontSize
  }
}
