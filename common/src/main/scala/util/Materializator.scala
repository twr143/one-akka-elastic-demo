package util

/**
 * Created by twr143 on 02.07.2021 at 13:12.
 */
object Materializator {
  def cmon[T: Mappable](map: Map[String, Any]) =
    implicitly[Mappable[T]].fromMap(map)
  def toMap[T: Mappable](t:T) =
    implicitly[Mappable[T]].toMap(t)

}
