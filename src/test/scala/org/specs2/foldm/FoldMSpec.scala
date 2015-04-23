package org.specs2
package foldm

import FoldM._
import FoldableM._
import stream.FoldableProcessM._
import FoldableOps._
import matcher.{Matcher, TaskMatchers}
import scalaz.{Equal, Apply, Compose}
import scalaz.std.list._
import scalaz.syntax.traverse._
import scalaz.syntax.apply._
import scalaz.concurrent.Task
import scalaz.stream.Process
import org.scalacheck.{Arbitrary, Gen}

class FoldMSpec extends Specification with ScalaCheck with TaskMatchers { def is = s2"""

 Folding a Foldable from the left  $foldableIsFoldable
 Folding an Iterator from the left $iteratorIsFoldable

 Zip foldings         $zipFolds
 Pipe foldings        $pipeFolds

 Apply laws ${ prop { (fbc: F[Int, String => Int], fab: F[Int, Int => String], fa: F[Int, Int]) =>
   Apply[({type l[A]=F[Int, A]})#l].applyLaw.composition(fbc, fab, fa)
 }.set(minTestsOk = 10)}

 Compose laws ${ prop { (fa: F[Int, String], fb: F[String, Int], fc: F[Int, String]) =>
   Compose[F].composeLaw.associative(fa, fb, fc)
 }.set(minTestsOk = 10)}


 Performance
 ===========

 Source loop vs fold $$sourceFold


"""

  type F[A, B] = FoldM[A, Task, B]

  def foldableIsFoldable = prop { (list: List[Int], fold: F[Int, String]) =>
    fold.run(list) must returnTask(runFoldOnList(list, fold))
  }

  def iteratorIsFoldable = prop { (list: List[Int], fold: F[Int, String]) =>
    fold.run(list.iterator) must returnTask(runFoldOnList(list, fold))
  }

  def zipFolds = prop { (list: List[Int], fold1: F[Int, String], fold2: F[Int, String]) =>
    implicit val m = Apply[({type F[U] = FoldM[Int, Task, U]})#F]
    val fold = fold1 zip fold2

    fold.run(list.iterator) must returnTask(runFoldOnList(list, fold))
  }

  def pipeFolds = prop { (list: List[Int], fold: FoldM[Int, Task, Int]) =>
    def acc[A] = fromFreeMonoid[A].to[Task]

    val scans: FoldM[Int, Task, List[Int]] = FoldCompose[Task].compose(acc, fold)
    scans.run(list.iterator) must returnTask(runScanOnList(list, fold))
  }

  /**
   * HELPERS
   */
  def runFoldOnList[A](list: List[Int], fold: FoldM[Int, Task, A]): Task[A] =
    fold.start.flatMap { i: fold.S =>
      fold.end(list.foldLeft(i: fold.S)((res, cur) => fold.fold(res, cur)))
    }

  def runScanOnList[A](list: List[Int], fold: FoldM[Int, Task, A]): Task[List[A]] =
    fold.start.flatMap { i: fold.S =>
      list.scanLeft(i: fold.S)((res, cur) => fold.fold(res, cur)).drop(1).traverseU(fold.end)
    }

  implicit def FoldIntStringArbitrary: Arbitrary[F[Int, String]] = Arbitrary {
    for {
      init <- Gen.choose(0, 10)
      fd   <- Gen.oneOf((s: Int, i: Int) => s + i, (s: Int, i: Int) => s * i)
      last <- Gen.oneOf((i: Int) => i.toString, (i: Int) => (i*2).toString)
    } yield new F[Int, String] {
      type S = Int

      def start       = Task.delay(init)
      def fold        = fd
      def end(s: Int) = Task.delay(last(s))

      override def toString = {
        val foldres = this.run((1 to 10).toList)
        "int fold with init "+ init + " and fold result "+foldres
      }
    }
  }

  implicit def FoldIntIntArbitrary: Arbitrary[FoldM[Int, Task, Int]] = Arbitrary {
    for {
      init <- Gen.choose(0, 10)
      fd   <- Gen.oneOf((s: Int, i: Int) => s + i, (s: Int, i: Int) => s * i)
      last <- Gen.oneOf((i: Int) => i, (i: Int) => (i*2))
    } yield new FoldM[Int, Task, Int] {
      type S = Int

      def start       = Task.delay(init)
      def fold        = fd
      def end(s: Int) = Task.delay(last(s))

      override def toString = {
        val foldres = this.run((1 to 10).toList)
        "int fold with init "+ init + " and fold result "+foldres
      }
    }
  }

  implicit def FoldStringIntArbitrary: Arbitrary[F[String, Int]] = Arbitrary {
    for {
      init <- Gen.choose(0, 10).map(_.toString)
      fd   <- Gen.oneOf((s: String, i: String) => s + i, (s: String, i: String) => i + s + i)
      last <- Gen.oneOf((i: String) => i.size, (i: String) => i.size * 2)
    } yield new F[String, Int] {
      type S = String

      def start     = Task.delay(init)
      def fold      = fd
      def end(s: S) = Task.delay(last(s))

      override def toString = {
        val foldres = this.run((1 to 10).toList.map(_.toString))
        "string fold with init "+ init + " and fold result "+foldres
      }
    }
  }

  implicit def FoldIntStringToIntArbitrary: Arbitrary[F[Int, String => Int]] = Arbitrary {
    for {
      init <- Gen.choose(0, 10)
      fd   <- Gen.oneOf((s: Int, i: Int) => s + i, (s: Int, i: Int) => s * i)
      last <- Gen.oneOf((i: Int) => i, (i: Int) => (i*2))
    } yield new F[Int, String => Int] {
      type S = Int

      def start       = Task.delay(init)
      def fold        = fd
      def end(s: Int) = Task.delay((string: String) => string.size+last(s))

      override def toString = {
        val foldres = this.run((1 to 1).toList)
        "int fold with init "+ init + " and fold result "+foldres
      }
    }
  }

  implicit def FoldIntIntToStringArbitrary: Arbitrary[F[Int, Int => String]] = Arbitrary {
    for {
      init <- Gen.choose(0, 10)
      fd   <- Gen.oneOf((s: Int, i: Int) => s + i, (s: Int, i: Int) => s * i)
      last <- Gen.oneOf((i: Int) => i, (i: Int) => (i*2))
    } yield new F[Int, Int => String] {
      type S = Int

      def start       = Task.delay(init)
      def fold        = fd
      def end(s: Int) = Task.delay((i: Int) => (i+last(s)).toString)

      override def toString = {
        val foldres = this.run((1 to 1).toList)
        "int fold with init "+ init + " and fold result "+foldres
      }
    }
  }

  implicit def LeftIntFoldEqual[U]: Equal[FoldM[Int, Task, U]] = new Equal[FoldM[Int, Task, U]] {
    type F = FoldM[Int, Task, U]

    def equal(a1: F, a2: F): Boolean =
      a1.run((1 to 10).toList).run == a2.run((1 to 10).toList).run
  }

  implicit def LeftStringFoldEqual[U]: Equal[FoldM[String, Task, U]] = new Equal[FoldM[String, Task, U]] {
    type F = FoldM[String, Task, U]

    def equal(a1: F, a2: F): Boolean =
      a1.run((1 to 10).toList.map(_.toString)).run == a2.run((1 to 10).toList.map(_.toString)).run
  }

  def returnTask[T](expected: Task[T]): Matcher[Task[T]] = { actual: Task[T] =>
    (actual |@| expected)(_ ==== _).run
  }
}
