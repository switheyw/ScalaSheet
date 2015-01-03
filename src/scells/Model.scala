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

import swing._

class Model(val height: Int, val width: Int) extends
                  Evaluator with Arithmetic { 

  // One Cell per spreadsheet Table cell.
  case class Cell(row: Int, column: Int) extends Publisher {

    override def toString = formula match {
      case Textual(s) => s
      case _ => value.toString
   }

   private var v: Double = 0
   def value: Double = v
    // Field setter. If value has changed, set field, then publish the update.
    // Is this a single valued function with expression block result replacing
    // the parameter? 
   def value_=(w: Double) {
      if ( !(v == w) || v.isNaN && w.isNaN) {
        v = w
        publish(ValueChanged(this))
      }
    }

    // WHY IS 'f' USED TWICE, private var f & formula_=(f:Formula)
    private var f: Formula = Empty
    def formula: Formula = f // formula & formula_ & f are empty.
    def formula_=(f:Formula) {
      for(c <- references(formula)) deafTo(c)
      this.f = f
      
      // references: returns what the formula references, a formula, a coord or a range 
      // evaluate: 
      for (c <- references(formula)) listenTo(c)
      value = evaluate(f)
      println( s"update formula field.  value = $value f = $f")
    }
    
    reactions += {
      case ValueChanged(_) => value = evaluate(formula)
    } 
  } // end case class Cell

  case class ValueChanged(cell: Cell) extends event.Event
 
  // cells (backs the Table UI array.) contains internal
  // representation of Table, and contains what should be
  // displayed at a particular row and col.
  val cells = Array.ofDim[Cell](height, width) 

  for (i <- 0 until height; j <- 0 until width) 
    cells(i)(j) = new Cell(i, j)
}
