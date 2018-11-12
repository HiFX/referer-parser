package com.hifx.refparser

import java.io.{InputStream, InputStreamReader}
import java.net.URLDecoder

import cats.syntax.either._
import com.anthonynsimon.url.URL
import io.circe.{yaml, _}

import scala.collection.mutable.{HashMap => MMap}
import scala.collection.JavaConverters._
import scala.util.Try

/**
  * Parser class hold the db for referrer lookups
  * and parse method to fetch a Referrer object from the referrer url
  */
case class Parser(refList: MMap[String, RefererLookup]) {

  /**
    * takes the referrer and page urls and returns the Referrer object
    */
  def parse(referrer: String, page: String): Referer = {
    if (referrer.equalsIgnoreCase(""))
    {
      return Referer(Medium.fromString("unknown"), "unknown", "")
    }
    val referrerUrl = URL.parse(referrer)
    val pageUrl = URL.parse(page)
    val refererChk = referrerUrl.getScheme.isEmpty ||
      referrerUrl.getHost.isEmpty ||
      (!referrerUrl.getScheme.equals("http") && !referrerUrl.getScheme.equals("https"))
    val pageChk = pageUrl.getScheme.isEmpty || (!pageUrl.getScheme.equals("http") && !pageUrl.getScheme.equals("https"))
    if (refererChk || pageChk) {
      Referer(Medium.fromString("unknown"), "unknown", "")
    } else {

      var referrer = lookupReferer(referrerUrl.getHost, referrerUrl.getPath, includePath = true)
      if (referrer.isEmpty) referrer = lookupReferer(referrerUrl.getHost, referrerUrl.getPath, includePath = false)

      if (referrer.isEmpty) {
        Referer(Medium.fromString("unknown"), "unknown", "")
      } else {
        val term = if (referrer.get.medium.equals(Medium.SEARCH)) {
          extractSearchTerm(referrerUrl, referrer.get.parameters)
        } else {
          None
        }
        Referer(referrer.get.medium, referrer.get.source, term.getOrElse(""))

      }
    }
  }

  /**
    * Extracts the search terms from the query string
    */
  private def extractSearchTerm(referrerUrl: URL, possibleParameters: List[String]): Option[String] = {
    val paramMap = referrerUrl.getQueryPairs
    var searchTeam: Option[String] = None

    for ((name, value) <- paramMap.asScala) {
      if (possibleParameters.contains(name)) {
        searchTeam = Some(URLDecoder.decode(value))
      }
    }
    searchTeam
  }

  /**
    * Recursive method to lookup referrer
    */
  def lookupReferer(refererHost: String, refererPath: String, includePath: Boolean): Option[RefererLookup] = {
    var referer = if (includePath) {
      refList.get(refererHost + refererPath)
    } else {
      refList.get(refererHost)
    }

    if (referer.isEmpty && includePath && !(refererPath == null)) {
      val pathElements = refererPath.split("/")
      if (pathElements.length > 1) {
        referer = refList.get(refererHost + "/" + pathElements(1))
      }
    }

    if (referer.isEmpty) {

      val idx = refererHost.indexOf(".")
      if (idx == -1) {
        None
      } else {
        lookupReferer(refererHost.substring(idx + 1), refererPath, includePath)
      }
    } else {
      referer
    }
  }
}

/**
  * Parser object instantiates the case class with database of referrers
  * loaded  via the supplied inputStream or referers.yml
  */
object Parser {
  val REFERERS_YAML_PATH = "referers.yml"

  def apply(): Parser = {
    loadReferers(this.getClass.getClassLoader.getResourceAsStream(REFERERS_YAML_PATH)).get
  }

  def apply(source: InputStream): Parser = {
    loadReferers(source).get
  }

  //populates the referrers db
  def loadReferers(stream: InputStream): Try[Parser] = {
    Try {
      type Providers = Map[String, Map[String, Map[String, List[String]]]]
      val yml = yaml.parser.parse(new InputStreamReader(stream))

      val provdList = yml
        .leftMap(err => err: Error)
        .flatMap(_.as[Providers])
        .valueOr(throw _)

      val referer = new MMap[String, RefererLookup]

      provdList.foreach {
        case (medium, sources) =>
          sources.foreach {
            case (source, values) =>
              val params = values.getOrElse("parameters", Nil)
              val domains = values.getOrElse("domains", Nil)
              domains.foreach(domain => referer += (domain -> RefererLookup(medium, source, params)))
          }
      }
      Parser(referer)
    }
  }

}
