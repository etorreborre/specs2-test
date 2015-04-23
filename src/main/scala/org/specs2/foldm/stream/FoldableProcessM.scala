package org.specs2
package foldm
package stream

import scalaz.{Monad}
import scalaz.concurrent.Task
import scalaz.stream.Process
import scalaz.stream.Process._

object FoldableProcessM {
  type TaskProcess[T] = Process[Task, T]

  implicit def ProcessFoldableM: FoldableM[TaskProcess, Task] = new FoldableM[TaskProcess, Task] {
    def foldM[A, B](fa: TaskProcess[A])(fd: FoldM[A, Task, B])(implicit M: Monad[Task]): Task[B] = {
      def go(state: fd.S): Process[Task, fd.S] =
        fa.flatMap { a =>
          val newState = fd.fold(state, a)
          emit(newState) fby go(newState)
        }
      fd.start.flatMap(st => go(st).runLast.flatMap(last => fd.end(last.getOrElse(st))))
    }
  }
}