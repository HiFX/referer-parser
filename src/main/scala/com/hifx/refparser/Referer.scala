package com.hifx.refparser

/**
  * Enum for the types of referer
  */
object Medium extends Enumeration {
  type Medium = Value
  val UNKNOWN, INTERNAL, SEARCH, SOCIAL, PAID, EMAIL = Value

  def fromString(medium: String): Medium = {
    this.withName(medium.toUpperCase)
  }
}

import com.hifx.refparser.Medium._

/**
  * Referrer object to be returned to the client
  */
case class Referer(medium: Medium, source: String, term: String)

/**
  * Referrer lookup db
  */
case class RefererLookup(medium: Medium, source: String, parameters: List[String])

object RefererLookup {
  def apply(medium: String, source: String, parameters: List[String]): RefererLookup = new RefererLookup(Medium.fromString(medium), source, parameters)
}
