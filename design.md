Fizzy Design
============

http://nickthecoder.co.uk/wiki/view/software/Fizzy
https://github.com/nickthecoder/fizzy

If you are interest in working with me on Fizzy, drop me a line @ nickthecoder at the gmail dot the com.

Technology
----------

Fizzy is written in Kotlin, a JVM language. Currently only tested on Linux, but should work on Windows, MacOS etc.
I'm currently targeting 1.8 of the JVM, and 1.1.2-2 of Kotlin.

The GUI uses JavaFX 8.

The build system is Gradle.

Unit testing uses JUnit 4. There is currently no test framework for the GUI.
That isn't as bad as it sounds though, because Fizzy has been designed so that editing documents can be mocked
without a specialised GUI testing framework.

Saving a document isn't implement yet! When it does, I'll probable use : com.eclipsesource.minimal-json:minimal-json

I use IntelliJ for development, but there are no dependencies.
Eclipse, vi(m), emacs, notepad  (;-) etc will work just fine.


Overview
--------

Fizzy is split into two modules (or will be).

The "core" module contains no JavaFX code, and can therefore be run in a headless environment,
and is easy to unit test.

The "app" module contains only JavaFX related code, and is deliberately made as small as possible.
Counterintuitively, most of the drawing, and editing of Fizzy diagrams is within the "core" module.

Controller CMouseEvent and DrawContext abstract away JavaFX classes such as MouseEvent and Canvas
so that the core code is JavaFX free.

The "app" module contains the rest of the gui elements, such as buttons, menus, dialog boxes, keyboard shortcuts etc.

The Model
---------

The high level classes, such as Document, Page and Shape are quite easy to comprehend.
Shape is the base class for Shape1d, Shape2d and ShapeGroup.

### Shape2d

Shaped2d objects are the most common type of Shape, and are used for pretty much everything that isn't a line.

### Shape1d

Shape1d objects behave like lines (though they may not always LOOK like lines).
They have an start point and an end point. They also have a 2D size, and transformations, just like Shape2d.
The difference here, is that the size and rotation of a Shape1d is governed by its end points
(i.e. size and rotation are calculated values).

### ShapeText

Shape1d and Shape2d do NOT have text. Instead, there is a third type of Shape.
This is markedly different from Visio (where every shape has the option of having a single piece of text).

### Groups

All Shapes can be nested within other Shapes. e.g. it is perfectly normal for a Shape1d to be made up in part from other child
shapes (which can be Shape1d, Shape2d or ShapeGroup).

Shapes have their own local coordinate system. So when drawing a Shape, the coordinates specified will be local
to that shape. For example a box will have points of (0,0), (width,0), (width, height) and (height,0).
These values do NOT change when a Shape is rotated, or moved, or flipped etc.

While drawing the objects the DrawContext takes care of transforming the local coordinates into the view's coordinates.
Within the GUI this is via JavaFX's Canvas and GraphicsContext classes.

However, there we also need to translate from one set of coordinates to another. e.g. to connect the end of a
Shape1d to a ConnectionPoint of another shape. We then use transformation matrices. See Shape.fromLocalToPage,
fromPageToLocal, fromLocalToParent and fromParentToLocal. Like most of Fizzy data, these matrices are lazily evaluated.


Shape Sheets
------------

Unlike other diagram applications, there are no primitive drawing objects, such as box or circle.
Instead, each shape is designed using a set of internal spreadsheets.
One cell in the spreadsheet is the Shape's size. Other cells describe how to draw the shape.
For example, a box will have 5 rows of cells something like this :

MoveTo  0,0
LineTo  Size * Vector(1,0)
LineTo  Size * Vector(1,1)
LineTo  Size * Vector(0,1)
LineTo  Size * Vector(1,0)

In practice these values will be more complex, for example they may reference the Shape's LineWidth (another cell).

Unlike Visio, Fizzy stores a 2D point in a single cell, rather than two X and Y cells. Fizzy has a richer set of types.

Low Level Types
---------------

Cells can contain any of the following types :

    String
    Boolean
    Double
    Vector2 (contains x:Double and y:Double)
    Dimension (a double, plus units, for example 10cm == 100mm == 0.1m)
    Dimension2 (contains x:Dimension, y:Dimension). This is the most common data type.
    Angle (contains a Double of the angle in radians). This makes it easy to switch between working in degrees and radians without even noticing!
    Paint, Color

These are all immutable types.

It is worth noting, that Dimension not only stores the units, but also the power.
For example, 1m * 1m == 1 square meter. This can help track down bugs in the spreadsheet, but may also be confusing.
Alas, 1m / 1m is NOT a Double it is a Dimension with units of meters and a "power" of zero.
There's a special function "ratio" to get around this problem. 1m.ratio( 1m ) = 1.0 (a Double, not a Dimension).

In order to keep Fizzy's core module free from JavaFX dependencies, Paint and Color are Fizzy specific, not the
more familiar JavaFX version.
As there is an open (GPL) version of JavaFX, I copied the Color class, and translated it from Java to Kotlin.

Properties (Prop)
-----------------

All of the low level types above are wrapped within "Prop"s. A Prop is similar to JavaFX's Property.
A Prop has a value, and a set of listeners. Whenever the value changes, the listeners are notified via a "dirty" event.

There are many implementing classes of the Prop interface. The simplest are PropValue and PropConstant.
But there are more complex varieties, such as PropExpression and PropMethod.

It is called "dirty", rather than "changed", because most Prop values are lazily evaluated.
For example, PropExpression contains a string (called a "formula"), which can be parsed (by Evaluator).
Changing the PropExpression's formula will fire "dirty", but the value will NOT be evaluated until needed.

Parsing "1+1" will NOT return 2, instead, it returns a PropCalculation, which knows how to add numbers.
Parsing "A+B" returns the same type of object, but in this case, when either A or B become dirty, the PropCalculation
will also become dirty.
Consider, the following pseudo code :

    A.value = 1
    B.value = 2
    C = PropExpression("A + B")
    print C.value => returns 3
    A.value = 2
    print C.value => returns 4

Note that C doesn't behave like a variable in traditional computer languages, because its value is dynamic.
However, it does NOT recalculate A + B each time. The value is cached until A or B fire a "dirty" event.
So if we again run :

    print C.value

No addition takes place, 4 is returned immediately.


PropType
--------

Each low-level data type such as Double, String, Dimension etc, has a PropType, such as DoublePropType.

These objects hold the meta-data, such as the names of field and methods, as well as a method to convert
the value to a string suitable for use in a formula.

MetaData
--------

Each Shape (and eventually each Document and Page) can return all of the cells in the shape sheet.
This aids debugging (as you can quickly see all of the formulas and their values).
It also makes loading and saving simple, because we just need to persist the meta data.

I've just noticed this is badly named, because it returns actual data, not meta data. Doh!


Expressions
-----------

Fizzy has its own mini language. It supports all the operators that you would expect, such as +, -, * etc.
These are polymorphic, i.e. Double + Double is different to Dimension + Dimension.

There are suffix operators, which I refer to as "converters", because they are used to convert a Double in a
Dimension (e.g. "3 mm", "1cm" etc), or into an Angle (e.g. "45 deg", "PI rad").

Constructors e.g. "Dimension2( 10mm, 1m )"

Methods. e.g. "Dimension2( 4mm, 3mm ).normalise()"

Fields e.g. "Dimension2( 2mm, 3mm).X" will return 2mm

Functions e.g. "abs( -2 )" will return 2

For convenience, many things are accessed like fields, although they are really methods under the hood.
For example, to find the length of a vector :

    "Vector2( 3, 4 ).Length" returns 5.

Internally, Fizzy used lists, but formulas reference items like a spreadsheet. For example :
If we have a spreadsheet called "Geometry1" with rows numbered 1, 2, 3... and a column called "Point", then :

    "Geometry1.Point3"

will return the value of the cell in row 1, column "Point".

In reality, Geometry1 has a List of GeometryParts, and each GeometryPart has a "point" attribute.
So the example above will end up performing : "theGeometry[2].point".
Note that the Fizzy language uses 1 based indices, so the "3" becomes a "2" when accessing the List.

There are no looping structures, the Fizzy language only calculates expressions.

There is an "if", but it is more akin to Java's ? : operator. It is implemented as a function :

    "if( true, 1, 3 )" returns 1

Currently all numbers are stored internally as Double, there are no Int values. This may change.

When evaluating an expression within a shape sheet, "this" will refer to the Shape that the shape sheet is defined
within. Using the "this" keyword is optional. e.g. the following mean the same :

    "this.Geometry1.Point1"
    "Geometry.Point1"


### Naming Convention:

Fields begin with an upper case letter, functions and methods begin with a lower case letter.


### Operator Precedence

I've use the same precedence that Kotlin uses. I think this differs from Java in relation to booleans.
(I may be mistaken, in which case, I've been using far too many brackets when writing Java code!).


Listeners
---------
Fizzy has many listeners, and is the hardest part of the project to understand.
There are currently three kind of listeners in the core module :

PropListener, ChangeListener and CollectionListener

CollectionListener receive "add" and "remove" event whenever the STRUCTURE of a list changes.
They do NOT receiver notifications when an item within the list changes state.

ChangeListener is quite generic, and is used by the high-level model objects such as Shape, Page and Document.
Typically, ChangeListeners and CollectionListeners combine. So for example, a Page listens to its list of Shapes
using a CollectionListener.
Whenever a Shape is added, it also registers as a ChangeListener for that Shape.
In this way, Page will be notified whenever Shapes are added/removed AND changed.

PropListener is the most common type, because they are attached to every low-level property, such as the "point"
within a Geometry's LineTo and MoveTo.
PropListeners get quite complicated, because there are chains of them. An expression such as "Geometry1.Point1.Length"
will have many PropListeners.

In this example "Length" is a PropField, and this listens to the underlying MoveTo.point property.

Point1 is a complex object (which I won't go into here). However, it listens to "Length", and is also a
CollectionListener for the Geometry.parts list. It will fire "dirty" events when the list changes and when the
"Length" becomes dirty.

Geometry1 is another (different) complex object, which is a CollectionListener (for Shape's list of Geometries).

Above all of these is a PropExpression (which holds the formula "Geometry1.Point1.Length".
It is parsed by Evaluator, but only parses when the formula changes. During normal operation, the PropExpression will
be able to re-calculate the value without re-parsing.

It does this by storing a single Prop, which has the value of the expression. This Prop will listen for "dirty" events
from each of its immediate dependencies, and those will listen to lower ones.

Each individual Prop know how to refresh its value.
For example "Length" listens to the MoveTo's point (which is of type Prop<Dimension2>) and
whenever the point changes, the "Length" can recalculate when needed by calling Dimension2.length() again.

So, to clarify, Evaluator returns a single Prop, which has a complex chain of Props and their PropListeners.
We can ask for the Prop's value, and if it isn't "dirty", then it will return the value immediately. However, if
it IS dirty, then it will recalculate its small part, and in doing so, it will ask OTHER Props for their value,
and they will either return their cached value (if they aren't dirty), or recalculate their value.

In this way everything in Fizzy is lazily evaluated, and then cached. The cached value is marked as dirty whenever
any of the items it is listening to fires a "dirty" event.

Note, it is common for a PropExpression to reference other Props which are ALSO PropExpressions.
It is therefore possible to get recursive dependencies. In which case, an exception will be thrown.
PropExpression will catch all exception (not only ones caused by recursion), and pass that to an error handler.
If the error handler doesn't throw, then the PropExpression will return a default value.
This allows a diagram to work as best it can, with only the "broken" parts behaving badly.
Within the unit tests, the error handler throws, and therefore the test fails fast (no default value is returned).


### Weak References

All Fizzy listeners are stored in collections of WeakReference.
This is to prevent memory leaks, and I imagine it would happen a lot, and would be very hard to find.
The drawback, is that listeners may be unintentionally garbage collected.
For anonymous class in this pseudo code would fail :

    myProp.addListener( new Listener() { ... listener code ... }

Instead, all listeners must be assigned to a val :

    val myListener = new Listener() { ... listener code ... }
    myProp.addListener( myListener )

and assuming "myListener" is a property of a class object, then the listener won't be gc'd until the owning class
is gc'd.

Special care must be taken when building up the tree like structure from Evaluator.parse().
If a listener is created, but doesn't form part of the tree back to the result of parse(), then
it will be gc'd, and the expression won't re-evaluate as expected.

I speak from experience! 4 hours "lost" today hunting a bug from a gc'd listener!


File Format
-----------

I'm yet to decide between json and xml. I tend to favour json.

Fizzy will export to SVG.

I'm not planning on making Fizzy load any existing file formats.
While loading Visio documents would obviously be nice, playing catch-up isn't a winning strategy.
It would be extremely complicated, and would likely give poor results.

In the distant future, I may support a subset of SVG.
Also, I Fizzy will allow images to be embedded, if I ever get that far.
This isn't a high priority for me, because Fizzy isn't aimed at being a general purpose graphics program.
It is a diagram editor.

