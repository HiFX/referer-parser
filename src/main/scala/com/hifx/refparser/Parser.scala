package com.hifx.refparser

import java.io.{InputStream, InputStreamReader}

import cats.syntax.either._
import com.netaporter.uri.Uri
import io.circe.{yaml, _}

import scala.collection.mutable.{HashMap => MMap}
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
    val referrerUrl = Uri.parse(referrer)
    val pageUrl = Uri.parse(page)
    val refererChk = referrerUrl.scheme.isEmpty || referrerUrl.host.isEmpty || (!referrerUrl.scheme.get.equals("http") && !referrerUrl.scheme.get.equals("https"))
    val pageChk = pageUrl.scheme.isEmpty || (!pageUrl.scheme.get.equals("http") && !pageUrl.scheme.get.equals("https"))
    if (refererChk || pageChk) {
      Referer(Medium.fromString("unknown"), "unknown", "")
    } else {

      var referrer = lookupReferer(referrerUrl.host.get, referrerUrl.path, includePath = true)
      if (referrer.isEmpty) referrer = lookupReferer(referrerUrl.host.get, referrerUrl.path, includePath = false)

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
  private def extractSearchTerm(referrerUrl: Uri, possibleParameters: List[String]): Option[String] = {
    val paramMap = referrerUrl.query.paramMap
    var searchTeam: Option[String] = None
    for (pair <- paramMap) {
      val name: String = pair._1
      val value: Seq[String] = pair._2.map(_.replace("+", " "))
      if (possibleParameters.contains(name)) {
        searchTeam = Some(value.mkString(" "))
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

    if (referer.isEmpty && includePath) {
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
        case (medium, sources) => sources.foreach {
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