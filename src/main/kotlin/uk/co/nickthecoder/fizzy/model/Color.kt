/*
 * Copyright (c) 2010, 2015, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package uk.co.nickthecoder.fizzy.model

/*
 * This code was taken from JavaFx's Color class. Therefore I have kept the
 * original GPL2 only copyright notice.
 *
 * My changes (other than converting to Kotlin, and changing the package name) :
 *
 * red,green,blue,opacity are simple 'val's
 *
 * To fit in with Fizzy better, the rgba attributes are Double, rather than Float.
 *
 * Removed : extends Paint implements Interpolatable<Color>
 *
 * Taken some Color related functions from JavaFX's Util class, and added
 * them to the companion object.
 *
 * FYI, the reason for COPYING, rather than just using the original, I want my core
 * classes to be independent of JavaFx, so that they can work in a plain, headless JVM
 * without JavaFX being present. Useful for non-gui related tasks.
 */

// NOTE: this definition, while correct, contains a lot of information which
// is irrelevant to most developers. We should get to the basic definition and
// usage patterns sooner.

/**
 * The Color class is used to encapsulate colors in the default sRGB color space.
 * Every color has an implicit alpha value of 1.0 or an explicit one provided
 * in the constructor. The alpha value defines the transparency of a color
 * and can be  represented by a double value in the range 0.0-1.0 or 0-255.
 * An alpha value of 1.0 or 255 means that the color is completely opaque
 * and an alpha value of 0 or 0.0 means that the color is completely transparent.
 * When constructing a `Color` with an explicit alpha or getting
 * the color/alpha components of a Color,
 * the color components are never premultiplied by the alpha component.
 *

 *
 * `Color`s can be created with the constructor or with one of several
 * utility methods.  The following lines of code all create the same
 * blue color:

 * <pre>
 * Color c = Color.BLUE;   //use the blue constant
 * Color c = new Color(0,0,1,1.0); // standard constructor, use 0->1.0 values, explicit alpha of 1.0

 * Color c = Color.color(0,0,1.0); //use 0->1.0 values. implicit alpha of 1.0
 * Color c = Color.color(0,0,1.0,1.0); //use 0->1.0 values, explicit alpha of 1.0

 * Color c = Color.rgb(0,0,255); //use 0->255 integers, implicit alpha of 1.0
 * Color c = Color.rgb(0,0,255,1.0); //use 0->255 integers, explicit alpha of 1.0

 * Color c = Color.hsb(270,1.0,1.0); //hue = 270, saturation & value = 1.0. inplicit alpha of 1.0
 * Color c = Color.hsb(270,1.0,1.0,1.0); //hue = 270, saturation & value = 1.0, explicit alpha of 1.0

 * Color c = Color.web("0x0000FF",1.0);// blue as a hex web value, explicit alpha
 * Color c = Color.web("0x0000FF");// blue as a hex web value, implicit alpha
 * Color c = Color.web("0x00F");// blue as a short hex web value, implicit alpha
 * Color c = Color.web("#0000FF",1.0);// blue as a hex web value, explicit alpha
 * Color c = Color.web("#0000FF");// blue as a hex web value, implicit alpha
 * Color c = Color.web("#00F");// blue as a short hex web value, implicit alpha
 * Color c = Color.web("0000FF",1.0);// blue as a hex web value, explicit alpha
 * Color c = Color.web("0000FF");// blue as a hex web value, implicit alpha
 * Color c = Color.web("00F");// blue as a short hex web value, implicit alpha
 * Color c = Color.web("rgba(0,0,255,1.0)");// blue as an rgb web value, explicit alpha
 * Color c = Color.web("rgb(0,0,255)");// blue as an rgb web value, implicit alpha
 * Color c = Color.web("rgba(0,0,100%,1.0)");// blue as an rgb percent web value, explicit alpha
 * Color c = Color.web("rgb(0,0,100%)");// blue as an rgb percent web value, implicit alpha
 * Color c = Color.web("hsla(270,100%,100%,1.0)");// blue as an hsl web value, explicit alpha
 * Color c = Color.web("hsl(270,100%,100%)");// blue as an hsl web value, implicit alpha
 * </pre>
 *
 *
 * The creation of a 'Color' will throw `IllegalArgumentException` if any
 * of the values are out of range.
 *
 */
class Color : Paint {

    val red: Double
    val green: Double
    val blue: Double
    val opacity: Double

    /**
     * Creates a new instance of color
     * @param red red component ranging from `0` to `1`
     * *
     * @param green green component ranging from `0` to `1`
     * *
     * @param blue blue component ranging from `0` to `1`
     * *
     * @param opacity opacity ranging from `0` to `1`
     */
    constructor(red: Double, green: Double, blue: Double, opacity: Double = 1.0) {
        if (red < 0 || red > 1) {
            throw IllegalArgumentException("Color's red value ($red) must be in the range 0.0-1.0")
        }
        if (green < 0 || green > 1) {
            throw IllegalArgumentException("Color's green value ($green) must be in the range 0.0-1.0")
        }
        if (blue < 0 || blue > 1) {
            throw IllegalArgumentException("Color's blue value ($blue) must be in the range 0.0-1.0")
        }
        if (opacity < 0 || opacity > 1) {
            throw IllegalArgumentException("Color's opacity value ($opacity) must be in the range 0.0-1.0")
        }

        this.red = red
        this.green = green
        this.blue = blue
        this.opacity = opacity
    }

    /**
     * Gets the hue component of this `Color`.
     * @return Hue value in the range `0.0-360.0`.
     */
    val hue: Double
        get() = RGBtoHSB(red, green, blue)[0]

    /**
     * Gets the saturation component of this `Color`.
     * @return Saturation value in the range `0.0-1.0`.
     */
    val saturation: Double
        get() = RGBtoHSB(red, green, blue)[1]

    /**
     * Gets the brightness component of this `Color`.
     * @return Brightness value in the range `0.0-1.0`.
     */
    val brightness: Double
        get() = RGBtoHSB(red, green, blue)[2]

    /**
     * Creates a new `Color` based on this `Color` with hue,
     * saturation, brightness and opacity values altered. Hue is shifted
     * about the given value and normalized into its natural range, the
     * other components' values are multiplied by the given factors and
     * clipped into their ranges.

     * Increasing brightness of black color is allowed by using an arbitrary,
     * very small source brightness instead of zero.
     */
    fun deriveColor(hueShift: Double, saturationFactor: Double,
                    brightnessFactor: Double, opacityFactor: Double): Color {

        val hsb = RGBtoHSB(red, green, blue)

        /* Allow brightness increase of black color */
        var b = hsb[2]
        if (b == 0.0 && brightnessFactor > 1.0) {
            b = 0.05
        }

        /* the tail "+ 360) % 360" solves shifts into negative numbers */
        val h = ((hsb[0] + hueShift) % 360 + 360) % 360
        val s = Math.max(Math.min(hsb[1] * saturationFactor, 1.0), 0.0)
        b = Math.max(Math.min(b * brightnessFactor, 1.0), 0.0)
        val a = Math.max(Math.min(opacity * opacityFactor, 1.0), 0.0)
        return hsb(h, s, b, a)
    }

    /**
     * Creates a new Color that is a brighter version of this Color.
     */
    fun brighter(): Color {
        return deriveColor(0.0, 1.0, 1.0 / DARKER_BRIGHTER_FACTOR, 1.0)
    }

    /**
     * Creates a new Color that is a darker version of this Color.
     */
    fun darker(): Color {
        return deriveColor(0.0, 1.0, DARKER_BRIGHTER_FACTOR, 1.0)
    }

    /**
     * Creates a new Color that is a more saturated version of this Color.
     */
    fun saturate(): Color {
        return deriveColor(0.0, 1.0 / SATURATE_DESATURATE_FACTOR, 1.0, 1.0)
    }

    /**
     * Creates a new Color that is a less saturated version of this Color.
     */
    fun desaturate(): Color {
        return deriveColor(0.0, SATURATE_DESATURATE_FACTOR, 1.0, 1.0)
    }

    /**
     * Creates a new Color that is grayscale equivalent of this Color.
     * Opacity is preserved.
     */
    fun grayscale(): Color {
        val gray = 0.21 * red + 0.71 * green + 0.07 * blue
        return Color.color(gray, gray, gray, opacity)
    }

    /**
     * Creates a new Color that is inversion of this Color.
     * Opacity is preserved.
     */
    fun invert(): Color {
        return Color.color(1.0 - red, 1.0 - green, 1.0 - blue, opacity)
    }

    /*
     * Named colors moved to nested class to initialize them only when they
     * are needed.
     */
    object NamedColors {
        private val namedColors = createNamedColors()

        operator fun get(name: String): Color? {
            return namedColors[name]
        }

        private fun createNamedColors(): Map<String, Color> {
            val colors = HashMap<String, Color>(256)

            colors.put("aliceblue", ALICEBLUE)
            colors.put("antiquewhite", ANTIQUEWHITE)
            colors.put("aqua", AQUA)
            colors.put("aquamarine", AQUAMARINE)
            colors.put("azure", AZURE)
            colors.put("beige", BEIGE)
            colors.put("bisque", BISQUE)
            colors.put("black", BLACK)
            colors.put("blanchedalmond", BLANCHEDALMOND)
            colors.put("blue", BLUE)
            colors.put("blueviolet", BLUEVIOLET)
            colors.put("brown", BROWN)
            colors.put("burlywood", BURLYWOOD)
            colors.put("cadetblue", CADETBLUE)
            colors.put("chartreuse", CHARTREUSE)
            colors.put("chocolate", CHOCOLATE)
            colors.put("coral", CORAL)
            colors.put("cornflowerblue", CORNFLOWERBLUE)
            colors.put("cornsilk", CORNSILK)
            colors.put("crimson", CRIMSON)
            colors.put("cyan", CYAN)
            colors.put("darkblue", DARKBLUE)
            colors.put("darkcyan", DARKCYAN)
            colors.put("darkgoldenrod", DARKGOLDENROD)
            colors.put("darkgray", DARKGRAY)
            colors.put("darkgreen", DARKGREEN)
            colors.put("darkgrey", DARKGREY)
            colors.put("darkkhaki", DARKKHAKI)
            colors.put("darkmagenta", DARKMAGENTA)
            colors.put("darkolivegreen", DARKOLIVEGREEN)
            colors.put("darkorange", DARKORANGE)
            colors.put("darkorchid", DARKORCHID)
            colors.put("darkred", DARKRED)
            colors.put("darksalmon", DARKSALMON)
            colors.put("darkseagreen", DARKSEAGREEN)
            colors.put("darkslateblue", DARKSLATEBLUE)
            colors.put("darkslategray", DARKSLATEGRAY)
            colors.put("darkslategrey", DARKSLATEGREY)
            colors.put("darkturquoise", DARKTURQUOISE)
            colors.put("darkviolet", DARKVIOLET)
            colors.put("deeppink", DEEPPINK)
            colors.put("deepskyblue", DEEPSKYBLUE)
            colors.put("dimgray", DIMGRAY)
            colors.put("dimgrey", DIMGREY)
            colors.put("dodgerblue", DODGERBLUE)
            colors.put("firebrick", FIREBRICK)
            colors.put("floralwhite", FLORALWHITE)
            colors.put("forestgreen", FORESTGREEN)
            colors.put("fuchsia", FUCHSIA)
            colors.put("gainsboro", GAINSBORO)
            colors.put("ghostwhite", GHOSTWHITE)
            colors.put("gold", GOLD)
            colors.put("goldenrod", GOLDENROD)
            colors.put("gray", GRAY)
            colors.put("green", GREEN)
            colors.put("greenyellow", GREENYELLOW)
            colors.put("grey", GREY)
            colors.put("honeydew", HONEYDEW)
            colors.put("hotpink", HOTPINK)
            colors.put("indianred", INDIANRED)
            colors.put("indigo", INDIGO)
            colors.put("ivory", IVORY)
            colors.put("khaki", KHAKI)
            colors.put("lavender", LAVENDER)
            colors.put("lavenderblush", LAVENDERBLUSH)
            colors.put("lawngreen", LAWNGREEN)
            colors.put("lemonchiffon", LEMONCHIFFON)
            colors.put("lightblue", LIGHTBLUE)
            colors.put("lightcoral", LIGHTCORAL)
            colors.put("lightcyan", LIGHTCYAN)
            colors.put("lightgoldenrodyellow", LIGHTGOLDENRODYELLOW)
            colors.put("lightgray", LIGHTGRAY)
            colors.put("lightgreen", LIGHTGREEN)
            colors.put("lightgrey", LIGHTGREY)
            colors.put("lightpink", LIGHTPINK)
            colors.put("lightsalmon", LIGHTSALMON)
            colors.put("lightseagreen", LIGHTSEAGREEN)
            colors.put("lightskyblue", LIGHTSKYBLUE)
            colors.put("lightslategray", LIGHTSLATEGRAY)
            colors.put("lightslategrey", LIGHTSLATEGREY)
            colors.put("lightsteelblue", LIGHTSTEELBLUE)
            colors.put("lightyellow", LIGHTYELLOW)
            colors.put("lime", LIME)
            colors.put("limegreen", LIMEGREEN)
            colors.put("linen", LINEN)
            colors.put("magenta", MAGENTA)
            colors.put("maroon", MAROON)
            colors.put("mediumaquamarine", MEDIUMAQUAMARINE)
            colors.put("mediumblue", MEDIUMBLUE)
            colors.put("mediumorchid", MEDIUMORCHID)
            colors.put("mediumpurple", MEDIUMPURPLE)
            colors.put("mediumseagreen", MEDIUMSEAGREEN)
            colors.put("mediumslateblue", MEDIUMSLATEBLUE)
            colors.put("mediumspringgreen", MEDIUMSPRINGGREEN)
            colors.put("mediumturquoise", MEDIUMTURQUOISE)
            colors.put("mediumvioletred", MEDIUMVIOLETRED)
            colors.put("midnightblue", MIDNIGHTBLUE)
            colors.put("mintcream", MINTCREAM)
            colors.put("mistyrose", MISTYROSE)
            colors.put("moccasin", MOCCASIN)
            colors.put("navajowhite", NAVAJOWHITE)
            colors.put("navy", NAVY)
            colors.put("oldlace", OLDLACE)
            colors.put("olive", OLIVE)
            colors.put("olivedrab", OLIVEDRAB)
            colors.put("orange", ORANGE)
            colors.put("orangered", ORANGERED)
            colors.put("orchid", ORCHID)
            colors.put("palegoldenrod", PALEGOLDENROD)
            colors.put("palegreen", PALEGREEN)
            colors.put("paleturquoise", PALETURQUOISE)
            colors.put("palevioletred", PALEVIOLETRED)
            colors.put("papayawhip", PAPAYAWHIP)
            colors.put("peachpuff", PEACHPUFF)
            colors.put("peru", PERU)
            colors.put("pink", PINK)
            colors.put("plum", PLUM)
            colors.put("powderblue", POWDERBLUE)
            colors.put("purple", PURPLE)
            colors.put("red", RED)
            colors.put("rosybrown", ROSYBROWN)
            colors.put("royalblue", ROYALBLUE)
            colors.put("saddlebrown", SADDLEBROWN)
            colors.put("salmon", SALMON)
            colors.put("sandybrown", SANDYBROWN)
            colors.put("seagreen", SEAGREEN)
            colors.put("seashell", SEASHELL)
            colors.put("sienna", SIENNA)
            colors.put("silver", SILVER)
            colors.put("skyblue", SKYBLUE)
            colors.put("slateblue", SLATEBLUE)
            colors.put("slategray", SLATEGRAY)
            colors.put("slategrey", SLATEGREY)
            colors.put("snow", SNOW)
            colors.put("springgreen", SPRINGGREEN)
            colors.put("steelblue", STEELBLUE)
            colors.put("tan", TAN)
            colors.put("teal", TEAL)
            colors.put("thistle", THISTLE)
            colors.put("tomato", TOMATO)
            colors.put("transparent", TRANSPARENT)
            colors.put("turquoise", TURQUOISE)
            colors.put("violet", VIOLET)
            colors.put("wheat", WHEAT)
            colors.put("white", WHITE)
            colors.put("whitesmoke", WHITESMOKE)
            colors.put("yellow", YELLOW)
            colors.put("yellowgreen", YELLOWGREEN)

            return colors
        }
    }

    fun isOpaque(): Boolean {
        return opacity >= 1f
    }

    fun interpolate(endValue: Color, t: Double): Color {
        if (t <= 0.0) return this
        if (t >= 1.0) return endValue
        val ft = t.toFloat()
        return Color(
                (red + (endValue.red - red) * ft),
                (green + (endValue.green - green) * ft),
                (blue + (endValue.blue - blue) * ft),
                (opacity + (endValue.opacity - opacity) * ft)
        )
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param other the reference object with which to compare.
     * *
     * @return `true` if this object is equal to the `obj` argument; `false` otherwise.
     */
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other is Color) {
            return red == other.red
                    && green == other.green
                    && blue == other.blue
                    && opacity == other.opacity
        } else
            return false
    }

    /**
     * Returns a hash code for this `Color` object.
     * @return a hash code for this `Color` object.
     */
    override fun hashCode(): Int {
        // construct the 32bit integer representation of this color
        val r = Math.round(red * 255.0).toInt()
        val g = Math.round(green * 255.0).toInt()
        val b = Math.round(blue * 255.0).toInt()
        val a = Math.round(opacity * 255.0).toInt()
        return to32BitInteger(r, g, b, a)
    }

    /**
     * Returns a string representation of this `Color`.
     * This method is intended to be used only for informational purposes.
     * The content and format of the returned string might vary between implementations.
     * The returned string might be empty but cannot be `null`.

     * @return the string representation
     */
    override fun toString(): String {
        val r = Math.round(red * 255.0).toInt()
        val g = Math.round(green * 255.0).toInt()
        val b = Math.round(blue * 255.0).toInt()
        val o = Math.round(opacity * 255.0).toInt()
        return String.format("0x%02x%02x%02x%02x", r, g, b, o)
    }

    companion object {

        /**
         * Brightness change factor for darker() and brighter() methods.
         */
        private val DARKER_BRIGHTER_FACTOR = 0.7

        /**
         * Saturation change factor for saturate() and desaturate() methods.
         */
        private val SATURATE_DESATURATE_FACTOR = 0.7

        /**
         * Creates an sRGB color with the specified red, green and blue values
         * in the range `0.0-1.0`, and a given opacity.

         * @param red the red component, in the range `0.0-1.0`
         * *
         * @param green the green component, in the range `0.0-1.0`
         * *
         * @param blue the blue component, in the range `0.0-1.0`
         * *
         * @param opacity the opacity component, in the range `0.0-1.0`
         * *
         * @return the `Color`
         * *
         * @throws IllegalArgumentException if any value is out of range
         */
        fun color(red: Double, green: Double, blue: Double, opacity: Double): Color {
            return Color(red, green, blue, opacity)
        }

        /**
         * Creates an opaque sRGB color with the specified red, green and blue values
         * in the range `0.0-1.0`.

         * @param red the red component, in the range `0.0-1.0`
         * *
         * @param green the green component, in the range `0.0-1.0`
         * *
         * @param blue the blue component, in the range `0.0-1.0`
         * *
         * @return the `Color`
         * *
         * @throws IllegalArgumentException if any value is out of range
         */
        fun color(red: Double, green: Double, blue: Double): Color {
            return Color(red, green, blue, 1.0)
        }

        /**
         * Creates an sRGB color with the specified RGB values in the range `0-255`,
         * and a given opacity.

         * @param red the red component, in the range `0-255`
         * *
         * @param green the green component, in the range `0-255`
         * *
         * @param blue the blue component, in the range `0-255`
         * *
         * @param opacity the opacity component, in the range `0.0-1.0`
         * *
         * @return the `Color`
         * *
         * @throws IllegalArgumentException if any value is out of range
         */
        fun rgb(red: Int, green: Int, blue: Int, opacity: Double): Color {
            checkRGB(red, green, blue)
            return Color(
                    red / 255.0,
                    green / 255.0,
                    blue / 255.0,
                    opacity)
        }

        /**
         * Creates an opaque sRGB color with the specified RGB values in the range `0-255`.

         * @param red the red component, in the range `0-255`
         * *
         * @param green the green component, in the range `0-255`
         * *
         * @param blue the blue component, in the range `0-255`
         * *
         * @return the `Color`
         * *
         * @throws IllegalArgumentException if any value is out of range
         */
        fun rgb(red: Int, green: Int, blue: Int): Color {
            checkRGB(red, green, blue)
            return Color(
                    red / 255.0,
                    green / 255.0,
                    blue / 255.0,
                    1.0)
        }


        /**
         * This is a shortcut for `rgb(gray, gray, gray)`.
         */
        fun grayRgb(gray: Int): Color {
            return rgb(gray, gray, gray)
        }

        /**
         * This is a shortcut for `rgb(gray, gray, gray, opacity)`.
         */
        fun grayRgb(gray: Int, opacity: Double): Color {
            return rgb(gray, gray, gray, opacity)
        }

        /**
         * Creates a grey color.
         * @param gray color on gray scale in the range
         * *             `0.0` (black) - `1.0` (white).
         * *
         * @param opacity the opacity component, in the range `0.0-1.0`
         * *
         * @return the `Color`
         * *
         * @throws IllegalArgumentException if any value is out of range
         */
        fun gray(gray: Double, opacity: Double = 1.0): Color {
            return Color(gray, gray, gray, opacity)
        }

        private fun checkRGB(red: Int, green: Int, blue: Int) {
            if (red < 0 || red > 255) {
                throw IllegalArgumentException("Color.rgb's red parameter ($red) expects color values 0-255")
            }
            if (green < 0 || green > 255) {
                throw IllegalArgumentException("Color.rgb's green parameter ($green) expects color values 0-255")
            }
            if (blue < 0 || blue > 255) {
                throw IllegalArgumentException("Color.rgb's blue parameter ($blue) expects color values 0-255")
            }
        }

        /**
         * Creates a `Color` based on the specified values in the HSB color model,
         * and a given opacity.

         * @param hue the hue, in degrees
         * *
         * @param saturation the saturation, `0.0 to 1.0`
         * *
         * @param brightness the brightness, `0.0 to 1.0`
         * *
         * @param opacity the opacity, `0.0 to 1.0`
         * *
         * @return the `Color`
         * *
         * @throws IllegalArgumentException if `saturation`, `brightness` or
         * *         `opacity` are out of range
         */
        fun hsb(hue: Double, saturation: Double, brightness: Double, opacity: Double = 1.0): Color {
            checkSB(saturation, brightness)
            val rgb = HSBtoRGB(hue, saturation, brightness)
            val result = Color(rgb[0], rgb[1], rgb[2], opacity)
            return result
        }

        private fun checkSB(saturation: Double, brightness: Double) {
            if (saturation < 0.0 || saturation > 1.0) {
                throw IllegalArgumentException("Color.hsb's saturation parameter ($saturation) expects values 0.0-1.0")
            }
            if (brightness < 0.0 || brightness > 1.0) {
                throw IllegalArgumentException("Color.hsb's brightness parameter ($brightness) expects values 0.0-1.0")
            }
        }

        /**
         * Creates an RGB color specified with an HTML or CSS attribute string.
         *
         *
         * This method supports the following formats:
         *
         *  * Any standard HTML color name
         *  * An HTML long or short format hex string with an optional hex alpha
         * channel.
         * Hexadecimal values may be preceded by either `"0x"` or `"#"`
         * and can either be 2 digits in the range `00` to `0xFF` or a
         * single digit in the range `0` to `F`.
         *  * An `rgb(r,g,b)` or `rgba(r,g,b,a)` format string.
         * Each of the `r`, `g`, or `b` values can be an integer
         * from 0 to 255 or a floating point percentage value from 0.0 to 100.0
         * followed by the percent (`%`) character.
         * The alpha component, if present, is a
         * floating point value from 0.0 to 1.0.  Spaces are allowed before or
         * after the numbers and between the percentage number and its percent
         * sign (`%`).
         *  * An `hsl(h,s,l)` or `hsla(h,s,l,a)` format string.
         * The `h` value is a floating point number from 0.0 to 360.0
         * representing the hue angle on a color wheel in degrees with
         * `0.0` or `360.0` representing red, `120.0`
         * representing green, and `240.0` representing blue.  The
         * `s` value is the saturation of the desired color represented
         * as a floating point percentage from gray (`0.0`) to
         * the fully saturated color (`100.0`) and the `l` value
         * is the desired lightness or brightness of the desired color represented
         * as a floating point percentage from black (`0.0`) to the full
         * brightness of the color (`100.0`).
         * The alpha component, if present, is a floating
         * point value from 0.0 to 1.0.  Spaces are allowed before or
         * after the numbers and between the percentage number and its percent
         * sign (`%`).
         *

         *
         * For formats without an alpha component and for named colors, opacity
         * is set according to the `opacity` argument. For colors specified
         * with an alpha component, the resulting opacity is a combination of the
         * parsed alpha component and the `opacity` argument, so a
         * transparent color becomes more transparent by specifying opacity.

         *
         * Examples:
         * <div class="classUseContainer">
         * <table class="overviewSummary" border="0" cellpadding="3" cellspacing="0">
         * <tr>
         * <th class="colFirst">Web Format String</th>
         * <th class="colLast">Equivalent constructor or factory call</th>
        </tr> *
         * <tr class="rowColor">
         * <td class="colFirst">`Color.web("orange", 0.5);`</td>
         * <td class="colLast">`new Color(1.0, 0xA5/255.0, 0.0, 0.5)`</td>
        </tr> *
         * <tr class="altColor">
         * <td class="colFirst">`Color.web("0xff66cc33", 0.5);`</td>
         * <td class="colLast">`new Color(1.0, 0.4, 0.8, 0.1)`</td>
        </tr> *
         * <tr class="rowColor">
         * <td class="colFirst">`Color.web("0xff66cc", 0.5);`</td>
         * <td class="colLast">`new Color(1.0, 0.4, 0.8, 0.5)`</td>
        </tr> *
         * <tr class="altColor">
         * <td class="colFirst">`Color.web("#ff66cc", 0.5);`</td>
         * <td class="colLast">`new Color(1.0, 0.4, 0.8, 0.5)`</td>
        </tr> *
         * <tr class="rowColor">
         * <td class="colFirst">`Color.web("#f68", 0.5);`</td>
         * <td class="colLast">`new Color(1.0, 0.4, 0.8, 0.5)`</td>
        </tr> *
         * <tr class="altColor">
         * <td class="colFirst">`Color.web("rgb(255,102,204)", 0.5);`</td>
         * <td class="colLast">`new Color(1.0, 0.4, 0.8, 0.5)`</td>
        </tr> *
         * <tr class="rowColor">
         * <td class="colFirst">`Color.web("rgb(100%,50%,50%)", 0.5);`</td>
         * <td class="colLast">`new Color(1.0, 0.5, 0.5, 0.5)`</td>
        </tr> *
         * <tr class="altColor">
         * <td class="colFirst">`Color.web("rgb(255,50%,50%,0.25)", 0.5);`</td>
         * <td class="colLast">`new Color(1.0, 0.5, 0.5, 0.125)`</td>
        </tr> *
         * <tr class="rowColor">
         * <td class="colFirst">`Color.web("hsl(240,100%,100%)", 0.5);`</td>
         * <td class="colLast">`Color.hsb(240.0, 1.0, 1.0, 0.5)`</td>
        </tr> *
         * <tr class="altColor">
         * <td style="border-bottom:1px solid" class="colFirst">
         * `Color.web("hsla(120,0%,0%,0.25)", 0.5);`
        </td> *
         * <td style="border-bottom:1px solid" class="colLast">
         * `Color.hsb(120.0, 0.0, 0.0, 0.125)`
        </td> *
        </tr> *
        </table> *
        </div> *

         * @param colorString the name or numeric representation of the color
         * *                    in one of the supported formats
         * *
         * @param opacity the opacity component in range from 0.0 (transparent)
         * *                to 1.0 (opaque)
         * *
         * @throws NullPointerException if `colorString` is `null`
         * *
         * @throws IllegalArgumentException if `colorString` specifies
         * *      an unsupported color name or contains an illegal numeric value
         */
        fun web(colorString: String?, opacity: Double = 1.0): Color {
            if (colorString == null) {
                throw NullPointerException(
                        "The color components or name must be specified")
            }
            if (colorString.isEmpty()) {
                throw IllegalArgumentException("Invalid color specification")
            }

            var color = colorString.toLowerCase()

            if (color.startsWith("#")) {
                color = color.substring(1)
            } else if (color.startsWith("0x")) {
                color = color.substring(2)
            } else if (color.startsWith("rgb")) {
                if (color.startsWith("(", 3)) {
                    return parseRGBColor(color, 4, false, opacity)
                } else if (color.startsWith("a(", 3)) {
                    return parseRGBColor(color, 5, true, opacity)
                }
            } else if (color.startsWith("hsl")) {
                if (color.startsWith("(", 3)) {
                    return parseHSLColor(color, 4, false, opacity)
                } else if (color.startsWith("a(", 3)) {
                    return parseHSLColor(color, 5, true, opacity)
                }
            } else {
                val col = NamedColors[color]
                if (col != null) {
                    if (opacity == 1.0) {
                        return col
                    } else {
                        return Color.color(col.red, col.green, col.blue, opacity)
                    }
                }
            }

            val len = color.length

            try {
                val r: Int
                val g: Int
                val b: Int
                val a: Int

                if (len == 3) {
                    r = Integer.parseInt(color.substring(0, 1), 16)
                    g = Integer.parseInt(color.substring(1, 2), 16)
                    b = Integer.parseInt(color.substring(2, 3), 16)
                    return Color.color(r / 15.0, g / 15.0, b / 15.0, opacity)
                } else if (len == 4) {
                    r = Integer.parseInt(color.substring(0, 1), 16)
                    g = Integer.parseInt(color.substring(1, 2), 16)
                    b = Integer.parseInt(color.substring(2, 3), 16)
                    a = Integer.parseInt(color.substring(3, 4), 16)
                    return Color.color(r / 15.0, g / 15.0, b / 15.0,
                            opacity * a / 15.0)
                } else if (len == 6) {
                    r = Integer.parseInt(color.substring(0, 2), 16)
                    g = Integer.parseInt(color.substring(2, 4), 16)
                    b = Integer.parseInt(color.substring(4, 6), 16)
                    return Color.rgb(r, g, b, opacity)
                } else if (len == 8) {
                    r = Integer.parseInt(color.substring(0, 2), 16)
                    g = Integer.parseInt(color.substring(2, 4), 16)
                    b = Integer.parseInt(color.substring(4, 6), 16)
                    a = Integer.parseInt(color.substring(6, 8), 16)
                    return Color.rgb(r, g, b, opacity * a / 255.0)
                }
            } catch (nfe: NumberFormatException) {
            }

            throw IllegalArgumentException("Invalid color specification")
        }

        private fun parseRGBColor(color: String, roff: Int,
                                  hasAlpha: Boolean, a: Double): Color {
            var a = a
            try {
                val rend = color.indexOf(',', roff)
                val gend = if (rend < 0) -1 else color.indexOf(',', rend + 1)
                val bend = if (gend < 0) -1 else color.indexOf((if (hasAlpha) ',' else ')'), gend + 1)
                val aend = if (hasAlpha) if (bend < 0) -1 else color.indexOf(')', bend + 1) else bend
                if (aend >= 0) {
                    val r = parseComponent(color, roff, rend, PARSE_COMPONENT)
                    val g = parseComponent(color, rend + 1, gend, PARSE_COMPONENT)
                    val b = parseComponent(color, gend + 1, bend, PARSE_COMPONENT)
                    if (hasAlpha) {
                        a *= parseComponent(color, bend + 1, aend, PARSE_ALPHA)
                    }
                    return Color(r, g, b, a)
                }
            } catch (nfe: NumberFormatException) {
            }

            throw IllegalArgumentException("Invalid color specification")
        }

        private fun parseHSLColor(color: String, hoff: Int,
                                  hasAlpha: Boolean, a: Double): Color {
            var a = a
            try {
                val hend = color.indexOf(',', hoff)
                val send = if (hend < 0) -1 else color.indexOf(',', hend + 1)
                val lend = if (send < 0) -1 else color.indexOf((if (hasAlpha) ',' else ')'), send + 1)
                val aend = if (hasAlpha) if (lend < 0) -1 else color.indexOf(')', lend + 1) else lend
                if (aend >= 0) {
                    val h = parseComponent(color, hoff, hend, PARSE_ANGLE)
                    val s = parseComponent(color, hend + 1, send, PARSE_PERCENT)
                    val l = parseComponent(color, send + 1, lend, PARSE_PERCENT)
                    if (hasAlpha) {
                        a *= parseComponent(color, lend + 1, aend, PARSE_ALPHA)
                    }
                    return Color.hsb(h, s, l, a)
                }
            } catch (nfe: NumberFormatException) {
            }

            throw IllegalArgumentException("Invalid color specification")
        }

        private val PARSE_COMPONENT = 0 // percent, or clamped to [0,255] => [0,1]
        private val PARSE_PERCENT = 1 // clamped to [0,100]% => [0,1]
        private val PARSE_ANGLE = 2 // clamped to [0,360]
        private val PARSE_ALPHA = 3 // clamped to [0.0,1.0]
        private fun parseComponent(color: String, off: Int, end: Int, type: Int): Double {
            var color = color
            var type = type
            color = color.substring(off, end).trim { it <= ' ' }
            if (color.endsWith("%")) {
                if (type > PARSE_PERCENT) {
                    throw IllegalArgumentException("Invalid color specification")
                }
                type = PARSE_PERCENT
                color = color.substring(0, color.length - 1).trim { it <= ' ' }
            } else if (type == PARSE_PERCENT) {
                throw IllegalArgumentException("Invalid color specification")
            }
            val c: Double = if (type == PARSE_COMPONENT)
                color.toInt().toDouble()
            else
                color.toDouble()

            when (type) {
                PARSE_ALPHA -> return if (c < 0.0) 0.0 else if (c > 1.0) 1.0 else c
                PARSE_PERCENT -> return if (c <= 0.0) 0.0 else if (c >= 100.0) 1.0 else c / 100.0
                PARSE_COMPONENT -> return if (c <= 0.0) 0.0 else if (c >= 255.0) 1.0 else c / 255.0
                PARSE_ANGLE -> return if (c < 0.0)
                    c % 360.0 + 360.0
                else
                    if (c > 360.0)
                        c % 360.0
                    else
                        c
            }

            throw IllegalArgumentException("Invalid color specification")
        }

        private fun to32BitInteger(red: Int, green: Int, blue: Int, alpha: Int): Int {
            var i = red
            i = i shl 8
            i = i or green
            i = i shl 8
            i = i or blue
            i = i shl 8
            i = i or alpha
            return i
        }

        /**
         * A fully transparent color with an ARGB value of #00000000.
         */
        val TRANSPARENT = Color(0.0, 0.0, 0.0, 0.0)

        /**
         * The color alice blue with an RGB value of #F0F8FF
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#F0F8FF;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val ALICEBLUE = Color(0.9411765, 0.972549, 1.0)

        /**
         * The color antique white with an RGB value of #FAEBD7
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FAEBD7;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val ANTIQUEWHITE = Color(0.98039216, 0.92156863, 0.84313726)

        /**
         * The color aqua with an RGB value of #00FFFF
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#00FFFF;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val AQUA = Color(0.0, 1.0, 1.0)

        /**
         * The color aquamarine with an RGB value of #7FFFD4
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#7FFFD4;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val AQUAMARINE = Color(0.49803922, 1.0, 0.83137256)

        /**
         * The color azure with an RGB value of #F0FFFF
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#F0FFFF;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val AZURE = Color(0.9411765, 1.0, 1.0)

        /**
         * The color beige with an RGB value of #F5F5DC
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#F5F5DC;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val BEIGE = Color(0.9607843, 0.9607843, 0.8627451)

        /**
         * The color bisque with an RGB value of #FFE4C4
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFE4C4;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val BISQUE = Color(1.0, 0.89411765, 0.76862746)

        /**
         * The color black with an RGB value of #000000
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#000000;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val BLACK = Color(0.0, 0.0, 0.0)

        /**
         * The color blanched almond with an RGB value of #FFEBCD
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFEBCD;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val BLANCHEDALMOND = Color(1.0, 0.92156863, 0.8039216)

        /**
         * The color blue with an RGB value of #0000FF
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#0000FF;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val BLUE = Color(0.0, 0.0, 1.0)

        /**
         * The color blue violet with an RGB value of #8A2BE2
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#8A2BE2;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val BLUEVIOLET = Color(0.5411765, 0.16862746, 0.8862745)

        /**
         * The color brown with an RGB value of #A52A2A
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#A52A2A;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val BROWN = Color(0.64705884, 0.16470589, 0.16470589)

        /**
         * The color burly wood with an RGB value of #DEB887
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#DEB887;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val BURLYWOOD = Color(0.87058824, 0.72156864, 0.5294118)

        /**
         * The color cadet blue with an RGB value of #5F9EA0
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#5F9EA0;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val CADETBLUE = Color(0.37254903, 0.61960787, 0.627451)

        /**
         * The color chartreuse with an RGB value of #7FFF00
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#7FFF00;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val CHARTREUSE = Color(0.49803922, 1.0, 0.0)

        /**
         * The color chocolate with an RGB value of #D2691E
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#D2691E;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val CHOCOLATE = Color(0.8235294, 0.4117647, 0.11764706)

        /**
         * The color coral with an RGB value of #FF7F50
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FF7F50;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val CORAL = Color(1.0, 0.49803922, 0.3137255)

        /**
         * The color cornflower blue with an RGB value of #6495ED
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#6495ED;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val CORNFLOWERBLUE = Color(0.39215687, 0.58431375, 0.92941177)

        /**
         * The color cornsilk with an RGB value of #FFF8DC
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFF8DC;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val CORNSILK = Color(1.0, 0.972549, 0.8627451)

        /**
         * The color crimson with an RGB value of #DC143C
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#DC143C;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val CRIMSON = Color(0.8627451, 0.078431375, 0.23529412)

        /**
         * The color cyan with an RGB value of #00FFFF
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#00FFFF;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val CYAN = Color(0.0, 1.0, 1.0)

        /**
         * The color dark blue with an RGB value of #00008B
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#00008B;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val DARKBLUE = Color(0.0, 0.0, 0.54509807)

        /**
         * The color dark cyan with an RGB value of #008B8B
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#008B8B;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val DARKCYAN = Color(0.0, 0.54509807, 0.54509807)

        /**
         * The color dark goldenrod with an RGB value of #B8860B
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#B8860B;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val DARKGOLDENROD = Color(0.72156864, 0.5254902, 0.043137256)

        /**
         * The color dark gray with an RGB value of #A9A9A9
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#A9A9A9;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val DARKGRAY = Color(0.6627451, 0.6627451, 0.6627451)

        /**
         * The color dark green with an RGB value of #006400
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#006400;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val DARKGREEN = Color(0.0, 0.39215687, 0.0)

        /**
         * The color dark grey with an RGB value of #A9A9A9
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#A9A9A9;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val DARKGREY = DARKGRAY

        /**
         * The color dark khaki with an RGB value of #BDB76B
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#BDB76B;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val DARKKHAKI = Color(0.7411765, 0.7176471, 0.41960785)

        /**
         * The color dark magenta with an RGB value of #8B008B
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#8B008B;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val DARKMAGENTA = Color(0.54509807, 0.0, 0.54509807)

        /**
         * The color dark olive green with an RGB value of #556B2F
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#556B2F;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val DARKOLIVEGREEN = Color(0.33333334, 0.41960785, 0.18431373)

        /**
         * The color dark orange with an RGB value of #FF8C00
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FF8C00;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val DARKORANGE = Color(1.0, 0.54901963, 0.0)

        /**
         * The color dark orchid with an RGB value of #9932CC
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#9932CC;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val DARKORCHID = Color(0.6, 0.19607843, 0.8)

        /**
         * The color dark red with an RGB value of #8B0000
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#8B0000;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val DARKRED = Color(0.54509807, 0.0, 0.0)

        /**
         * The color dark salmon with an RGB value of #E9967A
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#E9967A;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val DARKSALMON = Color(0.9137255, 0.5882353, 0.47843137)

        /**
         * The color dark sea green with an RGB value of #8FBC8F
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#8FBC8F;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val DARKSEAGREEN = Color(0.56078434, 0.7372549, 0.56078434)

        /**
         * The color dark slate blue with an RGB value of #483D8B
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#483D8B;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val DARKSLATEBLUE = Color(0.28235295, 0.23921569, 0.54509807)

        /**
         * The color dark slate gray with an RGB value of #2F4F4F
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#2F4F4F;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val DARKSLATEGRAY = Color(0.18431373, 0.30980393, 0.30980393)

        /**
         * The color dark slate grey with an RGB value of #2F4F4F
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#2F4F4F;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val DARKSLATEGREY = DARKSLATEGRAY

        /**
         * The color dark turquoise with an RGB value of #00CED1
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#00CED1;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val DARKTURQUOISE = Color(0.0, 0.80784315, 0.81960785)

        /**
         * The color dark violet with an RGB value of #9400D3
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#9400D3;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val DARKVIOLET = Color(0.5803922, 0.0, 0.827451)

        /**
         * The color deep pink with an RGB value of #FF1493
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FF1493;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val DEEPPINK = Color(1.0, 0.078431375, 0.5764706)

        /**
         * The color deep sky blue with an RGB value of #00BFFF
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#00BFFF;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val DEEPSKYBLUE = Color(0.0, 0.7490196, 1.0)

        /**
         * The color dim gray with an RGB value of #696969
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#696969;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val DIMGRAY = Color(0.4117647, 0.4117647, 0.4117647)

        /**
         * The color dim grey with an RGB value of #696969
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#696969;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val DIMGREY = DIMGRAY

        /**
         * The color dodger blue with an RGB value of #1E90FF
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#1E90FF;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val DODGERBLUE = Color(0.11764706, 0.5647059, 1.0)

        /**
         * The color firebrick with an RGB value of #B22222
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#B22222;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val FIREBRICK = Color(0.69803923, 0.13333334, 0.13333334)

        /**
         * The color floral white with an RGB value of #FFFAF0
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFFAF0;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val FLORALWHITE = Color(1.0, 0.98039216, 0.9411765)

        /**
         * The color forest green with an RGB value of #228B22
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#228B22;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val FORESTGREEN = Color(0.13333334, 0.54509807, 0.13333334)

        /**
         * The color fuchsia with an RGB value of #FF00FF
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FF00FF;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val FUCHSIA = Color(1.0, 0.0, 1.0)

        /**
         * The color gainsboro with an RGB value of #DCDCDC
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#DCDCDC;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val GAINSBORO = Color(0.8627451, 0.8627451, 0.8627451)

        /**
         * The color ghost white with an RGB value of #F8F8FF
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#F8F8FF;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val GHOSTWHITE = Color(0.972549, 0.972549, 1.0)

        /**
         * The color gold with an RGB value of #FFD700
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFD700;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val GOLD = Color(1.0, 0.84313726, 0.0)

        /**
         * The color goldenrod with an RGB value of #DAA520
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#DAA520;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val GOLDENROD = Color(0.85490197, 0.64705884, 0.1254902)

        /**
         * The color gray with an RGB value of #808080
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#808080;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val GRAY = Color(0.5019608, 0.5019608, 0.5019608)

        /**
         * The color green with an RGB value of #008000
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#008000;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val GREEN = Color(0.0, 0.5019608, 0.0)

        /**
         * The color green yellow with an RGB value of #ADFF2F
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#ADFF2F;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val GREENYELLOW = Color(0.6784314, 1.0, 0.18431373)

        /**
         * The color grey with an RGB value of #808080
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#808080;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val GREY = GRAY

        /**
         * The color honeydew with an RGB value of #F0FFF0
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#F0FFF0;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val HONEYDEW = Color(0.9411765, 1.0, 0.9411765)

        /**
         * The color hot pink with an RGB value of #FF69B4
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FF69B4;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val HOTPINK = Color(1.0, 0.4117647, 0.7058824)

        /**
         * The color indian red with an RGB value of #CD5C5C
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#CD5C5C;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val INDIANRED = Color(0.8039216, 0.36078432, 0.36078432)

        /**
         * The color indigo with an RGB value of #4B0082
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#4B0082;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val INDIGO = Color(0.29411766, 0.0, 0.50980395)

        /**
         * The color ivory with an RGB value of #FFFFF0
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFFFF0;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val IVORY = Color(1.0, 1.0, 0.9411765)

        /**
         * The color khaki with an RGB value of #F0E68C
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#F0E68C;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val KHAKI = Color(0.9411765, 0.9019608, 0.54901963)

        /**
         * The color lavender with an RGB value of #E6E6FA
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#E6E6FA;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val LAVENDER = Color(0.9019608, 0.9019608, 0.98039216)

        /**
         * The color lavender blush with an RGB value of #FFF0F5
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFF0F5;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val LAVENDERBLUSH = Color(1.0, 0.9411765, 0.9607843)

        /**
         * The color lawn green with an RGB value of #7CFC00
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#7CFC00;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val LAWNGREEN = Color(0.4862745, 0.9882353, 0.0)

        /**
         * The color lemon chiffon with an RGB value of #FFFACD
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFFACD;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val LEMONCHIFFON = Color(1.0, 0.98039216, 0.8039216)

        /**
         * The color light blue with an RGB value of #ADD8E6
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#ADD8E6;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val LIGHTBLUE = Color(0.6784314, 0.84705883, 0.9019608)

        /**
         * The color light coral with an RGB value of #F08080
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#F08080;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val LIGHTCORAL = Color(0.9411765, 0.5019608, 0.5019608)

        /**
         * The color light cyan with an RGB value of #E0FFFF
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#E0FFFF;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val LIGHTCYAN = Color(0.8784314, 1.0, 1.0)

        /**
         * The color light goldenrod yellow with an RGB value of #FAFAD2
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FAFAD2;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val LIGHTGOLDENRODYELLOW = Color(0.98039216, 0.98039216, 0.8235294)

        /**
         * The color light gray with an RGB value of #D3D3D3
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#D3D3D3;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val LIGHTGRAY = Color(0.827451, 0.827451, 0.827451)

        /**
         * The color light green with an RGB value of #90EE90
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#90EE90;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val LIGHTGREEN = Color(0.5647059, 0.93333334, 0.5647059)

        /**
         * The color light grey with an RGB value of #D3D3D3
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#D3D3D3;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val LIGHTGREY = LIGHTGRAY

        /**
         * The color light pink with an RGB value of #FFB6C1
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFB6C1;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val LIGHTPINK = Color(1.0, 0.7137255, 0.75686276)

        /**
         * The color light salmon with an RGB value of #FFA07A
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFA07A;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val LIGHTSALMON = Color(1.0, 0.627451, 0.47843137)

        /**
         * The color light sea green with an RGB value of #20B2AA
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#20B2AA;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val LIGHTSEAGREEN = Color(0.1254902, 0.69803923, 0.6666667)

        /**
         * The color light sky blue with an RGB value of #87CEFA
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#87CEFA;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val LIGHTSKYBLUE = Color(0.5294118, 0.80784315, 0.98039216)

        /**
         * The color light slate gray with an RGB value of #778899
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#778899;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val LIGHTSLATEGRAY = Color(0.46666667, 0.53333336, 0.6)

        /**
         * The color light slate grey with an RGB value of #778899
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#778899;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val LIGHTSLATEGREY = LIGHTSLATEGRAY

        /**
         * The color light steel blue with an RGB value of #B0C4DE
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#B0C4DE;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val LIGHTSTEELBLUE = Color(0.6901961, 0.76862746, 0.87058824)

        /**
         * The color light yellow with an RGB value of #FFFFE0
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFFFE0;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val LIGHTYELLOW = Color(1.0, 1.0, 0.8784314)

        /**
         * The color lime with an RGB value of #00FF00
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#00FF00;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val LIME = Color(0.0, 1.0, 0.0)

        /**
         * The color lime green with an RGB value of #32CD32
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#32CD32;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val LIMEGREEN = Color(0.19607843, 0.8039216, 0.19607843)

        /**
         * The color linen with an RGB value of #FAF0E6
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FAF0E6;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val LINEN = Color(0.98039216, 0.9411765, 0.9019608)

        /**
         * The color magenta with an RGB value of #FF00FF
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FF00FF;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val MAGENTA = Color(1.0, 0.0, 1.0)

        /**
         * The color maroon with an RGB value of #800000
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#800000;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val MAROON = Color(0.5019608, 0.0, 0.0)

        /**
         * The color medium aquamarine with an RGB value of #66CDAA
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#66CDAA;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val MEDIUMAQUAMARINE = Color(0.4, 0.8039216, 0.6666667)

        /**
         * The color medium blue with an RGB value of #0000CD
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#0000CD;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val MEDIUMBLUE = Color(0.0, 0.0, 0.8039216)

        /**
         * The color medium orchid with an RGB value of #BA55D3
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#BA55D3;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val MEDIUMORCHID = Color(0.7294118, 0.33333334, 0.827451)

        /**
         * The color medium purple with an RGB value of #9370DB
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#9370DB;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val MEDIUMPURPLE = Color(0.5764706, 0.4392157, 0.85882354)

        /**
         * The color medium sea green with an RGB value of #3CB371
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#3CB371;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val MEDIUMSEAGREEN = Color(0.23529412, 0.7019608, 0.44313726)

        /**
         * The color medium slate blue with an RGB value of #7B68EE
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#7B68EE;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val MEDIUMSLATEBLUE = Color(0.48235294, 0.40784314, 0.93333334)

        /**
         * The color medium spring green with an RGB value of #00FA9A
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#00FA9A;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val MEDIUMSPRINGGREEN = Color(0.0, 0.98039216, 0.6039216)

        /**
         * The color medium turquoise with an RGB value of #48D1CC
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#48D1CC;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val MEDIUMTURQUOISE = Color(0.28235295, 0.81960785, 0.8)

        /**
         * The color medium violet red with an RGB value of #C71585
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#C71585;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val MEDIUMVIOLETRED = Color(0.78039217, 0.08235294, 0.52156866)

        /**
         * The color midnight blue with an RGB value of #191970
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#191970;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val MIDNIGHTBLUE = Color(0.09803922, 0.09803922, 0.4392157)

        /**
         * The color mint cream with an RGB value of #F5FFFA
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#F5FFFA;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val MINTCREAM = Color(0.9607843, 1.0, 0.98039216)

        /**
         * The color misty rose with an RGB value of #FFE4E1
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFE4E1;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val MISTYROSE = Color(1.0, 0.89411765, 0.88235295)

        /**
         * The color moccasin with an RGB value of #FFE4B5
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFE4B5;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val MOCCASIN = Color(1.0, 0.89411765, 0.70980394)

        /**
         * The color navajo white with an RGB value of #FFDEAD
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFDEAD;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val NAVAJOWHITE = Color(1.0, 0.87058824, 0.6784314)

        /**
         * The color navy with an RGB value of #000080
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#000080;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val NAVY = Color(0.0, 0.0, 0.5019608)

        /**
         * The color old lace with an RGB value of #FDF5E6
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FDF5E6;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val OLDLACE = Color(0.99215686, 0.9607843, 0.9019608)

        /**
         * The color olive with an RGB value of #808000
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#808000;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val OLIVE = Color(0.5019608, 0.5019608, 0.0)

        /**
         * The color olive drab with an RGB value of #6B8E23
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#6B8E23;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val OLIVEDRAB = Color(0.41960785, 0.5568628, 0.13725491)

        /**
         * The color orange with an RGB value of #FFA500
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFA500;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val ORANGE = Color(1.0, 0.64705884, 0.0)

        /**
         * The color orange red with an RGB value of #FF4500
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FF4500;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val ORANGERED = Color(1.0, 0.27058825, 0.0)

        /**
         * The color orchid with an RGB value of #DA70D6
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#DA70D6;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val ORCHID = Color(0.85490197, 0.4392157, 0.8392157)

        /**
         * The color pale goldenrod with an RGB value of #EEE8AA
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#EEE8AA;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val PALEGOLDENROD = Color(0.93333334, 0.9098039, 0.6666667)

        /**
         * The color pale green with an RGB value of #98FB98
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#98FB98;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val PALEGREEN = Color(0.59607846, 0.9843137, 0.59607846)

        /**
         * The color pale turquoise with an RGB value of #AFEEEE
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#AFEEEE;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val PALETURQUOISE = Color(0.6862745, 0.93333334, 0.93333334)

        /**
         * The color pale violet red with an RGB value of #DB7093
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#DB7093;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val PALEVIOLETRED = Color(0.85882354, 0.4392157, 0.5764706)

        /**
         * The color papaya whip with an RGB value of #FFEFD5
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFEFD5;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val PAPAYAWHIP = Color(1.0, 0.9372549, 0.8352941)

        /**
         * The color peach puff with an RGB value of #FFDAB9
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFDAB9;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val PEACHPUFF = Color(1.0, 0.85490197, 0.7254902)

        /**
         * The color peru with an RGB value of #CD853F
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#CD853F;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val PERU = Color(0.8039216, 0.52156866, 0.24705882)

        /**
         * The color pink with an RGB value of #FFC0CB
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFC0CB;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val PINK = Color(1.0, 0.7529412, 0.79607844)

        /**
         * The color plum with an RGB value of #DDA0DD
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#DDA0DD;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val PLUM = Color(0.8666667, 0.627451, 0.8666667)

        /**
         * The color powder blue with an RGB value of #B0E0E6
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#B0E0E6;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val POWDERBLUE = Color(0.6901961, 0.8784314, 0.9019608)

        /**
         * The color purple with an RGB value of #800080
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#800080;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val PURPLE = Color(0.5019608, 0.0, 0.5019608)

        /**
         * The color red with an RGB value of #FF0000
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FF0000;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val RED = Color(1.0, 0.0, 0.0)

        /**
         * The color rosy brown with an RGB value of #BC8F8F
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#BC8F8F;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val ROSYBROWN = Color(0.7372549, 0.56078434, 0.56078434)

        /**
         * The color royal blue with an RGB value of #4169E1
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#4169E1;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val ROYALBLUE = Color(0.25490198, 0.4117647, 0.88235295)

        /**
         * The color saddle brown with an RGB value of #8B4513
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#8B4513;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val SADDLEBROWN = Color(0.54509807, 0.27058825, 0.07450981)

        /**
         * The color salmon with an RGB value of #FA8072
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FA8072;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val SALMON = Color(0.98039216, 0.5019608, 0.44705883)

        /**
         * The color sandy brown with an RGB value of #F4A460
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#F4A460;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val SANDYBROWN = Color(0.95686275, 0.6431373, 0.3764706)

        /**
         * The color sea green with an RGB value of #2E8B57
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#2E8B57;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val SEAGREEN = Color(0.18039216, 0.54509807, 0.34117648)

        /**
         * The color sea shell with an RGB value of #FFF5EE
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFF5EE;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val SEASHELL = Color(1.0, 0.9607843, 0.93333334)

        /**
         * The color sienna with an RGB value of #A0522D
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#A0522D;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val SIENNA = Color(0.627451, 0.32156864, 0.1764706)

        /**
         * The color silver with an RGB value of #C0C0C0
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#C0C0C0;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val SILVER = Color(0.7529412, 0.7529412, 0.7529412)

        /**
         * The color sky blue with an RGB value of #87CEEB
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#87CEEB;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val SKYBLUE = Color(0.5294118, 0.80784315, 0.92156863)

        /**
         * The color slate blue with an RGB value of #6A5ACD
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#6A5ACD;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val SLATEBLUE = Color(0.41568628, 0.3529412, 0.8039216)

        /**
         * The color slate gray with an RGB value of #708090
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#708090;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val SLATEGRAY = Color(0.4392157, 0.5019608, 0.5647059)

        /**
         * The color slate grey with an RGB value of #708090
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#708090;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val SLATEGREY = SLATEGRAY

        /**
         * The color snow with an RGB value of #FFFAFA
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFFAFA;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val SNOW = Color(1.0, 0.98039216, 0.98039216)

        /**
         * The color spring green with an RGB value of #00FF7F
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#00FF7F;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val SPRINGGREEN = Color(0.0, 1.0, 0.49803922)

        /**
         * The color steel blue with an RGB value of #4682B4
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#4682B4;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val STEELBLUE = Color(0.27450982, 0.50980395, 0.7058824)

        /**
         * The color tan with an RGB value of #D2B48C
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#D2B48C;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val TAN = Color(0.8235294, 0.7058824, 0.54901963)

        /**
         * The color teal with an RGB value of #008080
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#008080;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val TEAL = Color(0.0, 0.5019608, 0.5019608)

        /**
         * The color thistle with an RGB value of #D8BFD8
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#D8BFD8;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val THISTLE = Color(0.84705883, 0.7490196, 0.84705883)

        /**
         * The color tomato with an RGB value of #FF6347
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FF6347;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val TOMATO = Color(1.0, 0.3882353, 0.2784314)

        /**
         * The color turquoise with an RGB value of #40E0D0
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#40E0D0;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val TURQUOISE = Color(0.2509804, 0.8784314, 0.8156863)

        /**
         * The color violet with an RGB value of #EE82EE
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#EE82EE;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val VIOLET = Color(0.93333334, 0.50980395, 0.93333334)

        /**
         * The color wheat with an RGB value of #F5DEB3
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#F5DEB3;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val WHEAT = Color(0.9607843, 0.87058824, 0.7019608)

        /**
         * The color white with an RGB value of #FFFFFF
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFFFFF;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val WHITE = Color(1.0, 1.0, 1.0)

        /**
         * The color white smoke with an RGB value of #F5F5F5
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#F5F5F5;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val WHITESMOKE = Color(0.9607843, 0.9607843, 0.9607843)

        /**
         * The color yellow with an RGB value of #FFFF00
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFFF00;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val YELLOW = Color(1.0, 1.0, 0.0)

        /**
         * The color yellow green with an RGB value of #9ACD32
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#9ACD32;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val YELLOWGREEN = Color(0.6039216, 0.8039216, 0.19607843)

        fun HSBtoRGB(hue: Double, saturation: Double, brightness: Double): DoubleArray {
            var hue = hue
            // normalize the hue
            val normalizedHue = (hue % 360 + 360) % 360
            hue = normalizedHue / 360

            var r = 0.0
            var g = 0.0
            var b = 0.0
            if (saturation == 0.0) {
                b = brightness
                g = b
                r = g
            } else {
                val h = (hue - Math.floor(hue)) * 6.0
                val f = h - java.lang.Math.floor(h)
                val p = brightness * (1.0 - saturation)
                val q = brightness * (1.0 - saturation * f)
                val t = brightness * (1.0 - saturation * (1.0 - f))
                when (h.toInt()) {
                    0 -> {
                        r = brightness
                        g = t
                        b = p
                    }
                    1 -> {
                        r = q
                        g = brightness
                        b = p
                    }
                    2 -> {
                        r = p
                        g = brightness
                        b = t
                    }
                    3 -> {
                        r = p
                        g = q
                        b = brightness
                    }
                    4 -> {
                        r = t
                        g = p
                        b = brightness
                    }
                    5 -> {
                        r = brightness
                        g = p
                        b = q
                    }
                }
            }
            val f = DoubleArray(3)
            f[0] = r
            f[1] = g
            f[2] = b
            return f
        }

        fun RGBtoHSB(r: Double, g: Double, b: Double): DoubleArray {
            var hue: Double
            val saturation: Double
            val brightness: Double
            val hsbvals = DoubleArray(3)
            var cmax = if (r > g) r else g
            if (b > cmax) cmax = b
            var cmin = if (r < g) r else g
            if (b < cmin) cmin = b

            brightness = cmax
            if (cmax != 0.0)
                saturation = (cmax - cmin) / cmax
            else
                saturation = 0.0

            if (saturation == 0.0) {
                hue = 0.0
            } else {
                val redc = (cmax - r) / (cmax - cmin)
                val greenc = (cmax - g) / (cmax - cmin)
                val bluec = (cmax - b) / (cmax - cmin)
                if (r == cmax)
                    hue = bluec - greenc
                else if (g == cmax)
                    hue = 2.0 + redc - bluec
                else
                    hue = 4.0 + greenc - redc
                hue /= 6.0
                if (hue < 0)
                    hue += 1.0
            }
            hsbvals[0] = hue * 360
            hsbvals[1] = saturation
            hsbvals[2] = brightness
            return hsbvals
        }

        /**
         * Helper function to convert a color in sRGB space to linear RGB space.
         */
        fun convertSRGBtoLinearRGB(color: Color): Color {
            val colors = doubleArrayOf(color.red, color.green, color.blue)
            for (i in colors.indices) {
                if (colors[i] <= 0.04045) {
                    colors[i] = colors[i] / 12.92
                } else {
                    colors[i] = Math.pow((colors[i] + 0.055) / 1.055, 2.4)
                }
            }
            return Color.color(colors[0], colors[1], colors[2], color.opacity)
        }

        /**
         * Helper function to convert a color in linear RGB space to SRGB space.
         */
        fun convertLinearRGBtoSRGB(color: Color): Color {
            val colors = doubleArrayOf(color.red, color.green, color.blue)
            for (i in colors.indices) {
                if (colors[i] <= 0.0031308) {
                    colors[i] = colors[i] * 12.92
                } else {
                    colors[i] = 1.055 * Math.pow(colors[i], 1.0 / 2.4) - 0.055
                }
            }
            return Color.color(colors[0], colors[1], colors[2], color.opacity)
        }
    }
}
