package org.specs2
package foldm

import scalaz.{Apply, Functor, Profunctor, Monoid, Compose, Monad, Category, MonadPlus, ~>}
import scalaz.Id._
import scalaz.syntax.monad._
import scalaz.syntax.foldable._
import scalaz.std.list._
import scalaz.concurrent.Task
import scalaz.stream.Process._

trait FoldM[T, M[_], U] { outer =>
  type S

  def start: M[S]
  def fold: (S, T) => S
  def end(s: S): M[U]

  def zip[V](f: FoldM[T, M, V])(implicit ap: Apply[M]): FoldM[T, M, (U, V)] = new FoldM[T, M, (U, V)] {
    type S = (outer.S, f.S)
    def start = ap.tuple2(outer.start, f.start)
    def fold = (s, t) => (outer.fold(s._1, t), f.fold(s._2, t))
    def end(s: S) = ap.tuple2(outer.end(s._1), f.end(s._2))
  }

  def to[N[_]](implicit nat: M ~> N): FoldM[T, N, U] = new FoldM[T, N, U] {
    type S = outer.S
    def start = nat(outer.start)
    def fold = (s, t) => outer.fold(s, t)
    def end(s: S) = nat(outer.end(s))
  }

}

object FoldM {

  type Fold[T, U] = FoldM[T, Id, U]

  def fromMonoidMap[T, M : Monoid](f: T => M): FoldM[T, Id, M] = new FoldM[T, Id, M] {
    type S = M
    def start = Monoid[M].zero
    def fold = (s: S, t: T) => Monoid[M].append(s, f(t))
    def end(s: S) = s
  }

  def fromFreeMonoid[T]: FoldM[T, Id, List[T]] = 
    fromMonoidMap[T, List[T]](t => List(t))

  def fromMonoid[M : Monoid]: FoldM[M, Id, M] = 
    fromMonoidMap[M, M](identity _)

  def pipe[T, M[_] : Monad, U, V](f1: FoldM[T, M, U], f2: FoldM[U, M, V]): FoldM[T, M, V] = new FoldM[T, M, V] {
    type S = M[(f1.S, f2.S)]
    def start = Monad[M].point(Apply[M].tuple2(f1.start, f2.start))
      
    def fold = (s, t) => 
      s.flatMap { case (f1s, f2s) =>
        val f1state = f1.fold(f1s, t)
        f1.end(f1state).map { u: U => (f1state, f2.fold(f2s, u)) }
      }

    def end(s: S) = s.flatMap { case (_, fs2) => f2.end(fs2) }
  }

  implicit def FoldApply[T, M[_] : Apply]: Apply[({type F[U] = FoldM[T, M, U]})#F] = new Apply[({type F[U] = FoldM[T, M, U]})#F] {
    type F[U] = FoldM[T, M, U]

    def map[A, B](fa: F[A])(f: A => B): F[B] = new FoldM[T, M, B] {
      type S = fa.S
      def start = fa.start
      def fold = fa.fold
      def end(s: S) = Functor[M].map(fa.end(s))(f)
    }

    def ap[A, B](fa: => F[A])(f: => F[A => B]): F[B] =
      map(fa zip f) { case (a, b) => b(a) }
  }

  implicit def FoldProfunctor[M[_] : Functor]: Profunctor[({type F[T, U] = FoldM[T, M, U]})#F] = new Profunctor[({type F[T, U] = FoldM[T, M, U]})#F] {
    type =>:[T, U] = FoldM[T, M, U]

    /** Contramap on `A`. */
    def mapfst[A, B, C](fab: (A =>: B))(f: C => A): (C =>: B) = new FoldM[C, M, B] {
      type S = fab.S
      def start = fab.start
      def fold = (s: S, c: C) => fab.fold(s, f(c))
      def end(s: S) = fab.end(s)
    }

    /** Functor map on `B`. */
    def mapsnd[A, B, C](fab: (A =>: B))(f: B => C): (A =>: C) = new FoldM[A, M, C] {
      type S = fab.S
      def start = fab.start
      def fold = fab.fold
      def end(s: S) = Functor[M].map(fab.end(s))(f)
    }
  }

  def FoldCategory[M[_] : MonadPlus]: Category[({type F[A,B] = FoldM[A, M, B]})#F] = new Category[({type F[A,B] = FoldM[A, M, B]})#F] {
    type F[A,B] = FoldM[A, M, B]

    def id[A] = new FoldM[A, M, A] {
      type S = M[A]
      def start: M[M[A]] = Monad[M].point(MonadPlus[M].empty[A])
      def fold = (s: S, a: A) => Monad[M].point(a)
      def end(a: M[A]) = a
    }

    def compose[A, B, C](f: F[B, C], g: F[A, B]): F[A, C] =
      FoldCompose[M].compose(f, g)
  }

  implicit def FoldCompose[M[_] : Monad]: Compose[({type F[A,B] = FoldM[A, M, B]})#F] = new Compose[({type F[A,B] = FoldM[A, M, B]})#F] {
    type F[A,B] = FoldM[A, M, B]

    def compose[A, B, C](f: F[B, C], g: F[A, B]): F[A, C] = 
      pipe(g, f)
  }

  implicit val IdTaskNaturalTransformation: Id ~> Task = new (Id ~> Task) {
    def apply[A](i: Id[A]): Task[A] = Task.now(i)
  }
  implicit val IteratorListNaturalTransformation: Iterator ~> List = new (Iterator ~> List) {
    def apply[A](i: Iterator[A]): List[A] = i.toList
  }

}

object FoldableOps {
  implicit class FoldableOperations[T, M[_], U](fold: FoldM[T, M, U]) {
    def run[F[_]](foldable: F[T])(implicit M: Monad[M], foldableM: FoldableM[F, M]): M[U] =
      FoldableM[F, M].foldM(foldable)(fold)
  }
}


