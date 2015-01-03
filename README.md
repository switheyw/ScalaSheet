ScalaSheet
==========

SpreadSheet Example from Ed. 2 Programming in Scala (The Stairway book)

I use my hands instead of my head, to learn,  thus am imputing the spreadsheet
example from the stairway book.  As it happens, my wife and I want to track
expenses for a month or three, and I'd like to be able to use this spreadsheet
to that end.

I am also using this project as an opportunity to setup and get familiar with
git from within Eclipse and from within IntelliJ.  Using (or trying to use) the 'eGit' plugin.

Link: Do not create repository in Eclipse workspace:
http://wiki.eclipse.org/EGit/User_Guide#Creating_Repositories
.. but, I am having trouble using and or setting up a git repository
not in Eclipse workspace.

Mistakes or ommisions from text of 2nd Edition?
1) Pg 774, listing 35.4
	new Array[Array[Cell]](x,y) should be Array.ofDim[Cell](x,y)
	Notice that the declaration is not prefixed with 'new'.
