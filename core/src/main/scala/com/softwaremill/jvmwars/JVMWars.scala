package com.softwaremill.jvmwars

import io.circe._
import io.circe.generic.semiauto._
import sttp.client3._
import sttp.client3.httpclient.zio._
import sttp.client3.circe._

/*
 __        ___             ____            _      ___
 \ \      / / |__  _   _  / ___|  ___ __ _| | __ |__ \
  \ \ /\ / /| '_ \| | | | \___ \ / __/ _` | |/ _` |/ /
   \ V  V / | | | | |_| |  ___) | (_| (_| | | (_| |_|
    \_/\_/  |_| |_|\__, | |____/ \___\__,_|_|\__,_(_)
                   |___/
 */

/*
 Scala: typed functional programming on the JVM

 *The* way to do "business FP"
 */

/*
 "Basic" features of a functional language:
 * lambdas
 * expression-oriented
 * data classes
 * pattern matching
 */

/*
 These are all great! But there's more!
 * term inference
 * higher-order types
 * regularity
 * metaprogramming
 */
object JVMWars {
  /*
   Domain modeling: ADTs, pattern matching
   */
  case class Address(street: String, houseNumber: Int, flatNumber: Option[Int])

  sealed trait Entity
  case class Person(firstName: String, lastName: String, addresses: List[Address]) extends Entity
  case class Company(name: String, hq: Address) extends Entity

  // Productivity gain #1: immutability-first

  def allStreets(es: Set[Entity]): Set[String] = es
    .flatMap {
      case Person(firstName, lastName, addresses) => addresses.toSet
      case Company(name, hq)                      => Set(hq)
    }
    .map(_.street)

  // Productivity gain #2: concise, but readable, type-safe code

  //

  case class ValidationResponse(valid: Boolean)

  /*
   Principled metaprogramming
   Productivity gain #3: compile-time proofs instead of run-time tests
   */
  implicit val addressEncoder: Encoder[Address] = deriveEncoder[Address] // can be also written by hand, if needed
  implicit val responseDecoder: Decoder[ValidationResponse] = deriveDecoder[ValidationResponse]

  /*
   Type & term inference
   Embedded DSLs
   Everything is a value
   */
  def validateAddress(address: Address) = send(
    basicRequest
      .post(uri"http://example.org")
      .body(Address("xyz", 10, Some(12)))
      .response(asJson[ValidationResponse])
  )

  // Productivity gain #4: libraries, not frameworks

  // sttp client: HTTP client library for Scala
  // circe:       JSON library for Scala
}

/*
 Why types?
 * auto-complete
 * make illegal states unrepresentable
 * computers are intelligent enough to prove a lot of basic properties
 * tests should sample the rest.

 Productivity gain #5: auto-complete, explorable APIs and data structures
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
 * https://scalatimes.com
 * https://twitter.com/adamwarski
 */
