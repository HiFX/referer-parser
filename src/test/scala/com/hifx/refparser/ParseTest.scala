package com.hifx.refparser

/**
  * Based on https://github.com/snowplow/referer-parser/blob/develop/java-scala/
  */

import org.specs2.Specification
import org.specs2.matcher.DataTables


class ParseTest extends Specification with DataTables {
  def is =

    "This is a specification to test the parse function" ^
      p ^
      "parse should successfully extract referer details from URIs with recognised referers" ! e1 ^
            "parse should return unknown when the provided referer URI is not recognised" ! e2 ^
            "parse will (unavoidably) return some false positives as a result of its lookup algorithm" ! e3 ^
      end

  // Aliases
  val pageHost = "http://www.manoramaonline.com"

  // Successful extractions
  def e1 =
    "SPEC NAME" || "REFERER URI" | "REFERER MEDIUM" | "REFERER SOURCE" | "REFERER TERM" |
      "Google search #1" !! "http://www.google.com/search" ! Medium.SEARCH ! "Google" ! "" |
      "Google search #2" !! "http://www.google.com/search?q=gateway+oracle+cards+denise+linn&hl=en&client=safari" ! Medium.SEARCH ! "Google" ! "gateway oracle cards denise linn" |
      "Powered by Google" !! "http://isearch.avg.com/pages/images.aspx?q=tarot+card+change&sap=dsp&lang=en&mid=209215200c4147d1a9d6d1565005540b-b0d4f81a8999f5981f04537c5ec8468fd5234593&cid=%7B50F9298B-C111-4C7E-9740-363BF0015949%7D&v=12.1.0.21&ds=AVG&d=7%2F23%2F2012+10%3A31%3A08+PM&pr=fr&sba=06oENya4ZG1YS6vOLJwpLiFdjG91ICt2YE59W2p5ENc2c4w8KvJb5xbvjkj3ceMjnyTSpZq-e6pj7GQUylIQtuK4psJU60wZuI-8PbjX-OqtdX3eIcxbMoxg3qnIasP0ww2fuID1B-p2qJln8vBHxWztkpxeixjZPSppHnrb9fEcx62a9DOR0pZ-V-Kjhd-85bIL0QG5qi1OuA4M1eOP4i_NzJQVRXPQDmXb-CpIcruc2h5FE92Tc8QMUtNiTEWBbX-QiCoXlgbHLpJo5Jlq-zcOisOHNWU2RSHYJnK7IUe_SH6iQ.%2CYT0zO2s9MTA7aD1mNjZmZDBjMjVmZDAxMGU4&snd=hdr&tc=test1" ! Medium.SEARCH ! "Google" ! "tarot card change" |
      "Google Images search" !! "http://www.google.fr/imgres?q=Ogham+the+celtic+oracle&hl=fr&safe=off&client=firefox-a&hs=ZDu&sa=X&rls=org.mozilla:fr-FR:unofficial&tbm=isch&prmd=imvnsa&tbnid=HUVaj-o88ZRdYM:&imgrefurl=http://www.psychicbazaar.com/oracles/101-ogham-the-celtic-oracle-set.html&docid=DY5_pPFMliYUQM&imgurl=http://mdm.pbzstatic.com/oracles/ogham-the-celtic-oracle-set/montage.png&w=734&h=250&ei=GPdWUIePCOqK0AWp3oCQBA&zoom=1&iact=hc&vpx=129&vpy=276&dur=827&hovh=131&hovw=385&tx=204&ty=71&sig=104115776612919232039&page=1&tbnh=69&tbnw=202&start=0&ndsp=26&ved=1t:429,r:13,s:0,i:114&biw=1272&bih=826" ! Medium.SEARCH ! "Google Images" ! "Ogham the celtic oracle" |
      "Yahoo! search" !! "http://es.search.yahoo.com/search;_ylt=A7x9QbwbZXxQ9EMAPCKT.Qt.?p=BIEDERMEIER+FORTUNE+TELLING+CARDS&ei=utf-8&type=685749&fr=chr-greentree_gc&xargs=0&pstart=1&b=11" ! Medium.SEARCH ! "Yahoo!" ! "BIEDERMEIER FORTUNE TELLING CARDS" |
      "Yahoo! Images search" !! "http://it.images.search.yahoo.com/images/view;_ylt=A0PDodgQmGBQpn4AWQgdDQx.;_ylu=X3oDMTBlMTQ4cGxyBHNlYwNzcgRzbGsDaW1n?back=http%3A%2F%2Fit.images.search.yahoo.com%2Fsearch%2Fimages%3Fp%3DEarth%2BMagic%2BOracle%2BCards%26fr%3Dmcafee%26fr2%3Dpiv-web%26tab%3Dorganic%26ri%3D5&w=1064&h=1551&imgurl=mdm.pbzstatic.com%2Foracles%2Fearth-magic-oracle-cards%2Fcard-1.png&rurl=http%3A%2F%2Fwww.psychicbazaar.com%2Foracles%2F143-earth-magic-oracle-cards.html&size=2.8+KB&name=Earth+Magic+Oracle+Cards+-+Psychic+Bazaar&p=Earth+Magic+Oracle+Cards&oid=f0a5ad5c4211efe1c07515f56cf5a78e&fr2=piv-web&fr=mcafee&tt=Earth%2BMagic%2BOracle%2BCards%2B-%2BPsychic%2BBazaar&b=0&ni=90&no=5&ts=&tab=organic&sigr=126n355ib&sigb=13hbudmkc&sigi=11ta8f0gd&.crumb=IZBOU1c0UHU" ! Medium.SEARCH ! "Yahoo! Images" ! "Earth Magic Oracle Cards" |
      "PriceRunner search" !! "http://www.pricerunner.co.uk/search?displayNoHitsMessage=1&q=wild+wisdom+of+the+faery+oracle" ! Medium.SEARCH ! "PriceRunner" ! "wild wisdom of the faery oracle" |
      "Bing Images search" !! "http://www.bing.com/images/search?q=psychic+oracle+cards&view=detail&id=D268EDDEA8D3BF20AF887E62AF41E8518FE96F08" ! Medium.SEARCH ! "Bing Images" ! "psychic oracle cards" |
      "IXquick search" !! "https://s3-us3.ixquick.com/do/search" ! Medium.SEARCH ! "IXquick" ! "" |
      "AOL search" !! "http://aolsearch.aol.co.uk/aol/search?s_chn=hp&enabled_terms=&s_it=aoluk-homePage50&q=pendulums" ! Medium.SEARCH ! "AOL" ! "pendulums" |
      "Ask search" !! "http://uk.search-results.com/web?qsrc=1&o=1921&l=dis&q=pendulums&dm=ctry&atb=sysid%3D406%3Aappid%3D113%3Auid%3D8f40f651e7b608b5%3Auc%3D1346336505%3Aqu%3Dpendulums%3Asrc%3Dcrt%3Ao%3D1921&locale=en_GB" ! Medium.SEARCH ! "Ask" ! "pendulums" |
      "Mail.ru search" !! "http://go.mail.ru/search?q=Gothic%20Tarot%20Cards&where=any&num=10&rch=e&sf=20" ! Medium.SEARCH ! "Mail.ru" ! "Gothic Tarot Cards" |
      "Yandex search" !! "http://images.yandex.ru/yandsearch?text=Blue%20Angel%20Oracle%20Blue%20Angel%20Oracle&noreask=1&pos=16&rpt=simage&lr=45&img_url=http%3A%2F%2Fmdm.pbzstatic.com%2Foracles%2Fblue-angel-oracle%2Fbox-small.png" ! Medium.SEARCH ! "Yandex Images" ! "Blue Angel Oracle Blue Angel Oracle" |
      "Twitter redirect" !! "http://t.co/chrgFZDb" ! Medium.SOCIAL ! "Twitter" ! "" |
      "Facebook social" !! "http://www.facebook.com/l.php?u=http%3A%2F%2Fwww.psychicbazaar.com&h=yAQHZtXxS&s=1" ! Medium.SOCIAL ! "Facebook" ! "" |
      "Facebook mobile" !! "http://m.facebook.com/l.php?u=http%3A%2F%2Fwww.psychicbazaar.com%2Fblog%2F2012%2F09%2Fpsychic-bazaar-reviews-tarot-foundations-31-days-to-read-tarot-with-confidence%2F&h=kAQGXKbf9&s=1" ! Medium.SOCIAL ! "Facebook" ! "" |
      "Odnoklassniki" !! "http://www.odnoklassniki.ru/dk?cmd=logExternal&st._aid=Conversations_Openlink&st.name=externalLinkRedirect&st.link=http%3A%2F%2Fwww.psychicbazaar.com%2Foracles%2F187-blue-angel-oracle.html" ! Medium.SOCIAL ! "Odnoklassniki" ! "" |
      "Tumblr social #1" !! "http://www.tumblr.com/dashboard" ! Medium.SOCIAL ! "Tumblr" ! "" |
      "Tumblr w subdomain" !! "http://psychicbazaar.tumblr.com/" ! Medium.SOCIAL ! "Tumblr" ! "" |
      "Yahoo! Mail" !! "http://36ohk6dgmcd1n-c.c.yom.mail.yahoo.net/om/api/1.0/openmail.app.invoke/36ohk6dgmcd1n/11/1.0.35/us/en-US/view.html/0" ! Medium.EMAIL ! "Yahoo! Mail" ! "" |
      "Outlook.com mail" !! "http://co106w.col106.mail.live.com/default.aspx?rru=inbox" ! Medium.EMAIL ! "Outlook.com" ! "" |
      "Orange Webmail" !! "http://webmail1m.orange.fr/webmail/fr_FR/read.html?FOLDER=SF_INBOX&IDMSG=8594&check=&SORTBY=31" ! Medium.EMAIL ! "Orange Webmail" ! "" |
      "Internal HTTP" !! "http://www.manoramaonline.com/technology.html" ! Medium.INTERNAL ! "manoramaonline" ! "" |
      "Internal HTTPS" !! "http://english.manoramaonline.com/" ! Medium.INTERNAL ! "manoramaonline" ! "" |> {
      (_, refererUri, medium, source, term) =>
        Parser().parse(refererUri, pageHost) must_== Referer(medium, source, term)
    }

    // Unknown referer URI
    def e2 =
      "SPEC NAME" || "REFERER URI" | "REFERER SOURCE" |
        "Unknown referer #1" !! "http://www.behance.net/gallery/psychicbazaarcom/2243272" ! "unknown" |
        "Unknown referer #2" !! "http://www.wishwall.me/home" ! "unknown" |
        "Unknown referer #3" !! "http://www.spyfu.com/domain.aspx?d=3897225171967988459" ! "unknown" |
        "Unknown referer #4" !! "http://seaqueen.wordpress.com/" ! "unknown" |
        "Non-search Yahoo! site" !! "http://finance.yahoo.com" ! "Yahoo!" |> {
        (_, refererUri, refererSource) =>
          Parser().parse(refererUri, pageHost) must_== Referer(Medium.UNKNOWN, refererSource, "")
      }
  //
  //  // Unavoidable false positives
    def e3 =
      "SPEC NAME" || "REFERER URI" | "REFERER MEDIUM" | "REFERER SOURCE" | "REFERER TERM" |
        "Unknown Google service" !! "http://xxx.google.com" ! Medium.SEARCH ! "Google" ! "" |
        "Unknown Yahoo! service" !! "http://yyy.yahoo.com" ! Medium.SEARCH ! "Yahoo!" ! "" |
        "Non-search Google Drive link" !! "http://www.google.com/url?q=http://www.whatismyreferer.com/&sa=D&usg=ALhdy2_qs3arPmg7E_e2aBkj6K0gHLa5rQ" ! Medium.SEARCH ! "Google" ! "http://www.whatismyreferer.com/" |> {
        // ^ Sadly indistinguishable from a search link
        (_, refererUri, medium, source, term) =>
          Parser().parse(refererUri, pageHost) must_== Referer(medium, source, term)
      }
}