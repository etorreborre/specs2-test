package org.specs2
package foldm

import scalaz.{Monad, Foldable, ~>}
import scalaz.syntax.monad._
import scalaz.syntax.foldable._
import scalaz.std.list._

trait FoldableM[F[_], M[_]]  { self =>
  def foldM[A, B](fa: F[A])(fd: FoldM[A, M, B])(implicit M : Monad[M]): M[B]

  def to[G[_]](implicit nat: G ~> F): FoldableM[G, M] = new FoldableM[G, M] {
    def foldM[A, B](fa: G[A])(fd: FoldM[A, M, B])(implicit M : Monad[M]): M[B] =
     self.foldM(nat(fa))(fd)
  }
}

object FoldableM {

  def apply[F[_], M[_]](implicit fm: FoldableM[F, M]): FoldableM[F, M] =
    implicitly[FoldableM[F, M]]

  implicit def IteratorIsFoldableM[M[_]]: FoldableM[Iterator, M] = 
    FoldableIsFoldableM[List, M].to[Iterator](FoldM.IteratorListNaturalTransformation)

  implicit def FoldableIsFoldableM[F[_] : Foldable, M[_]]: FoldableM[F, M] = new FoldableM[F, M] {
    def foldM[A, B](fa: F[A])(fd: FoldM[A, M, B])(implicit M: Monad[M]): M[B] =
      fd.start.flatMap(st => fd.end(fa.foldLeft(st)(fd.fold)))
  }
}