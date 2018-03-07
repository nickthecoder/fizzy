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

import uk.co.nickthecoder.fizzy.util.toFormula

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
     * Create a new Color that is a more transparent version of this Color
     */
    fun transparent(opacity: Double = 0.5) = deriveColor(0.0, 1.0, 1.0, opacity)

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


    override fun toFormula() =
            if (this.isOpaque())
                "RGB(${this.red.toFormula()},${this.green.toFormula()},${this.blue.toFormula()})"
            else
                "RGBA(${this.red.toFormula()},${this.green.toFormula()},${this.blue.toFormula()},${this.opacity.toFormula()})"

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
         * The color black with an RGB value of #000000
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#000000;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val BLACK = Color(0.0, 0.0, 0.0)
        /**
         * The color white with an RGB value of #FFFFFF
         * <div style="border:1px solid black;width:40px;height:20px;background-color:#FFFFFF;float:right;margin: 0 10px 0 0"></div><br></br><br></br>
         */
        val WHITE = Color(1.0, 1.0, 1.0)

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
