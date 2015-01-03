package scells
import swing._
import event._

class Spreadsheet(val height: Int, val width: Int)
    extends ScrollPane {

  // Originally thought this just for reference, but now realize
  // that until now there was no instance of Model class.
  val cellModel = new Model(height, width)
  import cellModel._

  val table = new Table(height, width) {
    rowHeight = 25
    autoResizeMode = Table.AutoResizeMode.Off
    showGrid = true
    gridColor = new java.awt.Color(150, 150, 150)

    // hasFocus means editable field.
    // The userData helper method obtains and formats 
    // entered data and passes it to the TextField call
    // to display in the proper row/col of spreadsheet
    override def rendererComponent(isSelected: Boolean,
                                   hasFocus: Boolean, 
                                   row: Int, 
                                   column: Int): Component =
      if (hasFocus)
        new TextField(userData(row, column))
      else // Label obtains data from backing store.
        new Label( cells(row)(column).toString) {
          xAlignment = Alignment.Right
        }

    // Data from ScalaSheet extracted here.
    def userData(row: Int, column: Int): String = {
      // this(row,col) references the user data just entered,
      // via an 'apply' method on Table.
      // Data type of 'v' is 'Any'..
      val v = this(row, column)
      if (v == null) "" else v.toString
    }

    /* 
     * User data has been written to table cell.  A table updated event results in cells
     * being revisited to allow any formulas to be  parsed. The result of parse is stored in
     * backing store Cell.formula. Here a formula can  be POD or an actual formula which must be applied
     * to cell data. Each element in the cell model contains  Cell(i,j).value and Cell(i,j).formula
     * ? Is tableUpdated invoked for every change in user data? user data to table cell then TablelUpdated event?
     */
    reactions += {
      case TableUpdated(table, rows, column) =>
        println("Table update@col(" + column + ")")
        // A range results in multiple rows being read.
        for (row <- rows)
          cells(row)(column).formula =
            FormulaParsers.parse(userData(row, column))
    }

  }

  val rowHeader =
    new ListView((0 until height) map (_.toString)) {
      fixedCellWidth = 30
      fixedCellHeight = table.rowHeight
    }

  viewportView = table
  rowHeaderView = rowHeader
}
