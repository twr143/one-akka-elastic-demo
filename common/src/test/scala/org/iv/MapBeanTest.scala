package org.iv

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.iv.util._

/**
 * Created by twr143 on 08.07.2021 at 23:15.
 */
class MapBeanTest extends AnyWordSpec with Matchers {
  case class A(a: String)
  case class B(b: String, a: A)
  case class C(b: String, a: String)
  "Map bean test" should {

    "recreate C from the map" in {
      val m = Map("a" -> "1", "b" -> "2")
      assert(Materializer.cmon[C](m).isInstanceOf[C])
    }
    "recreate B from the map (nested) " in {
      val m = Map("a" -> "1", "b" -> "2")
      val a = Materializer.cmon[A](m)
      val m2 = m + ("a" -> a)
      val b = Materializer.cmon[B](m2)
      assert(b.isInstanceOf[B])
    }

  }

}
