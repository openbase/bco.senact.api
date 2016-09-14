package org.openbase.bco.senact.api.data;

/*-
 * #%L
 * BCO Senact API
 * %%
 * Copyright (C) 2013 - 2016 openbase.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

/**
 * Color Data Object. Platform independent data type for color representation.
 *
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class Color {

	/**
	 * The color white. In the default sRGB space.
	 *
	 * @since 1.4
	 */
	public final static Color WHITE = new Color(255, 255, 255);

	/**
	 * The color light gray. In the default sRGB space.
	 */
	public final static Color LIGHT_GRAY = new Color(192, 192, 192);

	/**
	 * /**
	 * The color gray. In the default sRGB space.
	 */
	public final static Color GRAY = new Color(128, 128, 128);

	/**
	 * The color dark gray. In the default sRGB space.
	 */
	public final static Color DARK_GRAY = new Color(64, 64, 64);

	/**
	 * The color black. In the default sRGB space.
	 */
	public final static Color BLACK = new Color(0, 0, 0);

	/**
	 * The color red. In the default sRGB space.
	 */
	public final static Color RED = new Color(255, 0, 0);

	/**
	 * The color pink. In the default sRGB space.
	 */
	public final static Color PINK = new Color(255, 175, 175);

	/**
	 * The color orange. In the default sRGB space.
	 */
	public final static Color ORANGE = new Color(255, 200, 0);

	/**
	 * The color yellow. In the default sRGB space.
	 */
	public final static Color YELLOW = new Color(255, 255, 0);

	/**
	 * The color green. In the default sRGB space.
	 */
	public final static Color GREEN = new Color(0, 255, 0);

	/**
	 * The color magenta. In the default sRGB space.
	 */
	public final static Color MAGENTA = new Color(255, 0, 255);

	/**
	 * The color cyan. In the default sRGB space.
	 */
	public final static Color CYAN = new Color(0, 255, 255);

	/**
	 * The color blue. In the default sRGB space.
	 */
	public final static Color BLUE = new Color(0, 0, 255);

	private int alpha, red, green, blue;

	public Color(final java.awt.Color color) {
		this(color.getRed(), color.getGreen(), color.getBlue());
	}

	public Color(final int red, final int green, final int blue) {
		this.alpha = 0;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public Color(final int red, final int green, final int blue, final int alpha) {
		this.alpha = alpha;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public int getAlpha() {
		return alpha;
	}

	public void setAlpha(final int alpha) {
		this.alpha = alpha;
	}

	public int getRed() {
		return red;
	}

	public void setRed(final int red) {
		this.red = red;
	}

	public int getGreen() {
		return green;
	}

	public void setGreen(final int green) {
		this.green = green;
	}

	public int getBlue() {
		return blue;
	}

	public void setBlue(final int blue) {
		this.blue = blue;
	}

	public java.awt.Color getAWTColor() {
		return new java.awt.Color(getRed(), getGreen(), getBlue());
	}

	/**
	 * Converts the components of a color, as specified by the HSB model, to an
	 * equivalent set of values for the default RGB model.
	 * <p>
	 * The <code>saturation</code> and <code>brightness</code> components should
	 * be floating-point values between zero and one (numbers in the range
	 * 0.0-1.0). The <code>hue</code> component can be any floating-point
	 * number. The floor of this number is subtracted from it to create a
	 * fraction between 0 and 1. This fractional number is then multiplied by
	 * 360 to produce the hue angle in the HSB color model.
	 *
	 * @param hue the hue component of the color
	 * @param saturation the saturation of the color
	 * @param brightness the brightness of the color
	 * @return the RGB value of the color with the indicated hue, saturation,
	 * and brightness.
	 * @see java.awt.Color#getRGB()
	 * @see java.awt.Color#Color(int)
	 * @see java.awt.image.ColorModel#getRGBdefault()
	 * @since JDK1.0
	 */
	public static int HSBtoRGB(float hue, float saturation, float brightness) {
		int r = 0, g = 0, b = 0;
		if (saturation == 0) {
			r = g = b = (int) (brightness * 255.0f + 0.5f);
		} else {
			float h = (hue - (float) Math.floor(hue)) * 6.0f;
			float f = h - (float) java.lang.Math.floor(h);
			float p = brightness * (1.0f - saturation);
			float q = brightness * (1.0f - saturation * f);
			float t = brightness * (1.0f - (saturation * (1.0f - f)));
			switch ((int) h) {
				case 0:
					r = (int) (brightness * 255.0f + 0.5f);
					g = (int) (t * 255.0f + 0.5f);
					b = (int) (p * 255.0f + 0.5f);
					break;
				case 1:
					r = (int) (q * 255.0f + 0.5f);
					g = (int) (brightness * 255.0f + 0.5f);
					b = (int) (p * 255.0f + 0.5f);
					break;
				case 2:
					r = (int) (p * 255.0f + 0.5f);
					g = (int) (brightness * 255.0f + 0.5f);
					b = (int) (t * 255.0f + 0.5f);
					break;
				case 3:
					r = (int) (p * 255.0f + 0.5f);
					g = (int) (q * 255.0f + 0.5f);
					b = (int) (brightness * 255.0f + 0.5f);
					break;
				case 4:
					r = (int) (t * 255.0f + 0.5f);
					g = (int) (p * 255.0f + 0.5f);
					b = (int) (brightness * 255.0f + 0.5f);
					break;
				case 5:
					r = (int) (brightness * 255.0f + 0.5f);
					g = (int) (p * 255.0f + 0.5f);
					b = (int) (q * 255.0f + 0.5f);
					break;
			}
		}
		return 0xff000000 | (r << 16) | (g << 8) | (b << 0);
	}

	/**
	 * Converts the components of a color, as specified by the default RGB
	 * model, to an equivalent set of values for hue, saturation, and brightness
	 * that are the three components of the HSB model.
	 * <p>
	 * If the <code>hsbvals</code> argument is <code>null</code>, then a new
	 * array is allocated to return the result. Otherwise, the method returns
	 * the array <code>hsbvals</code>, with the values put into that array.
	 *
	 * @param r the red component of the color
	 * @param g the green component of the color
	 * @param b the blue component of the color
	 * @param hsbvals the array used to return the three HSB values, or
	 * <code>null</code>
	 * @return an array of three elements containing the hue, saturation, and
	 * brightness (in that order), of the color with the indicated red, green,
	 * and blue components.
	 * @see java.awt.Color#getRGB()
	 * @see java.awt.Color#Color(int)
	 * @see java.awt.image.ColorModel#getRGBdefault()
	 * @since JDK1.0
	 */
	public static float[] RGBtoHSB(int r, int g, int b, float[] hsbvals) {
		float hue, saturation, brightness;
		if (hsbvals == null) {
			hsbvals = new float[3];
		}
		int cmax = (r > g) ? r : g;
		if (b > cmax) {
			cmax = b;
		}
		int cmin = (r < g) ? r : g;
		if (b < cmin) {
			cmin = b;
		}

		brightness = ((float) cmax) / 255.0f;
		if (cmax != 0) {
			saturation = ((float) (cmax - cmin)) / ((float) cmax);
		} else {
			saturation = 0;
		}
		if (saturation == 0) {
			hue = 0;
		} else {
			float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
			float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
			float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
			if (r == cmax) {
				hue = bluec - greenc;
			} else if (g == cmax) {
				hue = 2.0f + redc - bluec;
			} else {
				hue = 4.0f + greenc - redc;
			}
			hue = hue / 6.0f;
			if (hue < 0) {
				hue = hue + 1.0f;
			}
		}
		hsbvals[0] = hue;
		hsbvals[1] = saturation;
		hsbvals[2] = brightness;
		return hsbvals;
	}

	/**
	 * Creates a <code>Color</code> object based on the specified values for the
	 * HSB color model.
	 * <p>
	 * The <code>s</code> and <code>b</code> components should be floating-point
	 * values between zero and one (numbers in the range 0.0-1.0). The
	 * <code>h</code> component can be any floating-point number. The floor of
	 * this number is subtracted from it to create a fraction between 0 and 1.
	 * This fractional number is then multiplied by 360 to produce the hue angle
	 * in the HSB color model.
	 *
	 * @param h the hue component
	 * @param s the saturation of the color
	 * @param b the brightness of the color
	 * @return a <code>Color</code> object with the specified hue, saturation,
	 * and brightness.
	 * @since JDK1.0
	 */
	public static java.awt.Color getHSBColor(float h, float s, float b) {
		return new java.awt.Color(HSBtoRGB(h, s, b));
	}
}
