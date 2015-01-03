package scells

import scala.util.parsing.combinator._

object FormulaParsers extends RegexParsers {

	def ident: Parser[String] = """[a-zA-Z_]\w*""".r

	// Change 2nd decimal regex to (\.\\d+)?
	// Prevents decimal pt  w/o any trailing digits
	def decimal: Parser[String] = """-?\d+(\.\d+)?""".r

	// ^^ is the parser combinator transform operator
	// Returns the transformed result of the regexp parse.
	//
	def cell: Parser[Coord] = """[A-Za-z]\d+""".r ^^ { s =>
		val column = s.charAt(0).toUpper - 'A'
		val row = s.substring(1).toInt
		Coord(row,column)
	}

	// TODO Range should not be allowed to be a 'top level' formula. pg 779
	// The ~ operator produces as it's result an instance
	// of a case class with the same name: ~
	// 
	def range: Parser[Range] = cell~":"~cell ^^ {
		case c1~":"~c2 => Range(c1,c2)
	}

	def number: Parser[Number] =
		decimal ^^ (d => Number(d.toDouble))

	def application: Parser[Application] =
		ident~"("~repsep(expr, ",")~")" ^^ {
			case f~"("~ps~")" => Application(f, ps)
		}	

	def expr: Parser[Formula] =
		range | cell | number | application

	// Textual is defined in Formula.scala as xx.toString
	def textual: Parser[Textual] =
		"""[^=].*""".r ^^ Textual

	// legal input to cells are numbers, text or expressions (formula proceeded by an equal sign)
	def formula: Parser[Formula] = number | textual | "="~>expr

	def parse ( input:String) : Formula =
		parseAll(formula, input) match {
			case Success(e,_) => e
			case f: NoSuccess => Textual("["+f.msg+"]")
		}

} // end FormulaParsers
