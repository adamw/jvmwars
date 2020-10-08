package com.softwaremill.jvmwars

import io.circe._
import io.circe.generic.semiauto._
import sttp.client3._
import sttp.client3.httpclient.zio._
import sttp.client3.circe._
import zio._
import zio.duration.durationInt

/*
      ___     ____  ____        __
     | \ \   / /  \/  \ \      / /_ _ _ __ ___
  _  | |\ \ / /| |\/| |\ \ /\ / / _` | '__/ __|
 | |_| | \ V / | |  | | \ V  V / (_| | |  \__ \
  \___/   \_/  |_|  |_|  \_/\_/ \__,_|_|  |___/
 */

/*
 Scala: typed functional programming on the JVM

 *the* way to do FP on an "enterprise" platform
 */

/*
 A number of features from Scala migrated (partially) to Java already:
 * lambdas
 * pattern matching
 * expression-oriented
 * data classes
 */

/*
 These are all great! But there's more!

 All of the features above - but regular - not only for special cases!
 * Scala: more regular
 * domain modeling - immutability first, sealed, pattern matching
 * term inference
 * custom control structures
 */
object JVMWars extends App {
  /*
   Domain modeling: immutability-first, ADTs, pattern matching
   */
  case class Author(firstName: String, lastName: String)

  sealed trait Literature
  case class Book(title: String, author: Author) extends Literature
  case class Magazine(title: String, number: Int, year: Int) extends Literature

  case class Shelf(items: List[Literature])

  type RoomName = String
  case class Apartment(shelves: Map[RoomName, Shelf])

  def allBooksByShakespeare(a: Apartment): Set[String] =
    a.shelves.values.toSet.flatMap((_: Shelf).items).collect { case Book(title, Author(_, "Shakespeare")) =>
      title
    }

  // ZIO:         composable asynchronous & concurrent programming library for Scala
  // sttp client: HTTP client library for Scala
  // circe:       JSON library for Scala
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    case class OrderRequest(books: List[Book])
    case class OrderResponse(price: Long)

    /*
     Principle metaprogramming
     */
    implicit val bookEncoder: Encoder[Book] = deriveEncoder[Book]
    implicit val requestEncoder: Encoder[OrderRequest] = deriveEncoder[OrderRequest] // can be also written by hand, if needed
    implicit val responseDecoder: Decoder[OrderResponse] = deriveDecoder[OrderResponse]

    /*
     Type & term inference
     Embedded DSLs
     Everything is a value
     */
    def orderBooks(books: List[Book]) = send(
      basicRequest
        .post(uri"http://amazing.shop")
        .body(OrderRequest(books))
        .response(asJson[OrderResponse])
    )

    val retryStrategy = Schedule.recurs(10) && Schedule.exponential(10.milliseconds)
    ZIO
      .tupledPar(
        orderBooks(
          List(Book("Introduction to Algorithms", Author("Thomas", "Cormen")), Book("The Colour of Magic", Author("Terry", "Pratchett")))
        )
          .retry(retryStrategy),
        orderBooks(List(Book("Lalka", Author("Bolesław", "Prus")))).retry(retryStrategy).timeout(1.second)
      )
      .provideCustomLayer(HttpClientZioBackend.layer())
      .exitCode
  }
}

/*
 Why types?
 * auto-complete
 * make illegal states unrepresentable
 * computers are intelligent enough to prove a lot of basic properties
 * tests should sample the rest.
 */

/*
 Evolve your programming style: from OO to FP!

 Scala3:
 * streamlined data modelling
 * programmer-friendly metaprogramming
 * specialized term inference
 */

/*
  _____ _                 _                        _
 |_   _| |__   __ _ _ __ | | __  _   _  ___  _   _| |
   | | | '_ \ / _` | '_ \| |/ / | | | |/ _ \| | | | |
   | | | | | | (_| | | | |   <  | |_| | (_) | |_| |_|
   |_| |_| |_|\__,_|_| |_|_|\_\  \__, |\___/ \__,_(_)
                                 |___/

 * https://scala.page
 * https://twitter.com/adamwarski
 * https://github.com/adamw/jvmwars
 */
