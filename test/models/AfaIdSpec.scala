/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import generators.ModelGenerators
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.EitherValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.mvc.PathBindable

import java.time.Year

class AfaIdSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with EitherValues with ModelGenerators {

  "an AFA ID" - {

    val pathBindable = implicitly[PathBindable[AfaId]]

    "must bind from a url" in {

      forAll(arbitrary[String], arbitrary[AfaId]) {
        (key, afaId) =>

          pathBindable.bind(key, afaId.toString).right.value mustEqual afaId
      }
    }

    "must unbind to a url" in {

      forAll(arbitrary[String], arbitrary[AfaId]) {
        (key, value) =>

          pathBindable.unbind(key, value) mustEqual value.toString
      }
    }

    "with a UK prefix must be formatted correctly" in {

      AfaId(Year.of(2019), 123, AfaId.UK).toString mustEqual "UK20190123"
    }

    "with a GB prefix and three digit Id must be formatted correctly" in {

      AfaId(Year.of(2019), 123, AfaId.GB).toString mustEqual "GB2019123"
    }

    "with a GB prefix and two digit Id must be formatted correctly" in {

      AfaId(Year.of(2019), 12, AfaId.GB(false)).toString mustEqual "GB201912"
    }

    "with a UK prefix must identify as not migrated" in {

      AfaId(Year.of(2019), 123, AfaId.UK).isMigrated mustEqual false
    }

    "with a GB prefix and three digit Id must identify as migrated" in {

      AfaId(Year.of(2019), 123, AfaId.GB).isMigrated mustEqual true
    }

    "with a GB prefix and two digit Id must identify as migrated" in {

      AfaId(Year.of(2019), 12, AfaId.GB(false)).isMigrated mustEqual true
    }
  }
}
