
/*
 * Copyright (C) 2007-2010 Artima, Inc. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Example code from:
 *
 * Programming in Scala, Second Edition
 * by Martin Odersky, Lex Spoon, Bill Venners
 *
 * http://booksites.artima.com/programming_in_scala_2ed
 */
package scells

/**
 * @author sww
 */
// "this", in file, refers to Model.

trait Evaluator { this: Model =>

  // function which takes list of Doubles and returns a double.
  type Op = List[Double] => Double

  //  val operations = new collection.mutable.HashMap[String, Op]
  val operations =
    new collection.mutable.HashMap[String, List[Double] => Double]

  def evaluate(e: Formula): Double = try {
    e match {
      case Coord(row, column) => {
        var x =  cells(row)(column).value
        var y =  cells(row)(column).formula
        
        println(s"cells($row)($column) value is $x and formula is $y")

        cells(row)(column).value
      }
      case Number(v)          => v
      case Textual(_)         => 0
      case Application(function, args) =>
        println(s"Application: function = $function, args = $args")
        val argvals = args flatMap evalList
        println(s"Application: argvals = $argvals")
        // 'operations' is a Map, invoked as if it were a curried function.
        // Keys are names of functions, ie, "add". Values are case classes.
        // "add"  -> { case List(x, y) => x + y },
        //
        operations(function)(argvals)
    }
  } catch {
    case ex: Exception => Double.NaN
  }

  private def evalList(e: Formula): List[Double] = e match {
    case Range(_, _) => references(e) map (_.value)
    case _           => List(evaluate(e))
  }

  def references(e: Formula): List[Cell] = e match {
    case Coord(row, column) => List(cells(row)(column))
    case Range(Coord(r1, c1), Coord(r2, c2)) =>
      println("references: evaluating range")
      for (row <- (r1 to r2).toList; column <- c1 to c2)
        yield cells(row)(column)
    case Application(function, arguments) =>
      arguments flatMap references
    case _ => List()
  }

}