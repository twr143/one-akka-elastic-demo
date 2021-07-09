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
  case class E(v: Int)
  case class D(b: String, e: E)
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
    "create a map from B " in {
      val b = B("1", A("2"))
      val m = Materializer.toMap(b)
      val m1 = m ++ Materializer.toMap(m("a").asInstanceOf[A])
      assert(m1.isInstanceOf[Map[_,_]])
    }
    "create a map from D " in {
      val d = D("1", E(2))
      val m = Materializer.toMap(d)
      val m1 = m ++ Materializer.toMap(m("e").asInstanceOf[E]) -"e"
      println(m1)
      assert(m1.isInstanceOf[Map[_,_]])
    }

  }

}
