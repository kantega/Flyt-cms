/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.commons.media;

/*
 * ImageInfo.java
 *
 * Version 1.5
 *
 * A Java class to determine image width, height and color depth for
 * a number of image file formats.
 *
 * Written by Marco Schmidt
 * <http://www.geocities.com/marcoschmidt.geo/contact.html>.
 *
 * Contributed to the Public Domain.
 *
 * Last modification 2004-02-29
 *
 * Fixed SWF error: Anders Skar
 *
 */

import java.io.DataInput;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;
import java.util.zip.InflaterInputStream;

/**
 * Get file format, image resolution, number of bits per pixel and optionally
 * number of images, comments and physical resolution from
 * JPEG, GIF, BMP, PCX, PNG, IFF, RAS, PBM, PGM, PPM, PSD and SWF files
 * (or input streams).
 * <p>
 * Use the class like this:
 * <pre>
 * ImageInfo ii = new ImageInfo();
 * ii.setInput(in); // in can be InputStream or RandomAccessFile
 * ii.setDetermineImageNumber(true); // default is false
 * ii.setCollectComments(true); // default is false
 * if (!ii.check()) {
 *   System.err.println("Not a supported image file format.");
 *   return;
 * }
 * System.out.println(ii.getFormatName() + ", " + ii.getMimeType() +
 *   ", " + ii.getWidth() + " x " + ii.getHeight() + " pixels, " +
 *   ii.getBitsPerPixel() + " bits per pixel, " + ii.getNumberOfImages() +
 *   " image(s), " + ii.getNumberOfComments() + " comment(s).");
 *  // there are other properties, check out the API documentation
 * </pre>
 * You can also use this class as a command line program.
 * Call it with a number of image file names and URLs as parameters:
 * <pre>
 *   java ImageInfo *.jpg *.png *.gif http://somesite.tld/image.jpg
 * </pre>
 * or call it without parameters and pipe data to it:
 * <pre>
 *   java ImageInfo &lt; image.jpg
 * </pre>
 * <p>
 * Known limitations:
 * <ul>
 * <li>When the determination of the number of images is turned off, GIF bits
 *  per pixel are only read from the global header.
 *  For some GIFs, local palettes change this to a typically larger
 *  value. To be certain to get the correct color depth, call
 *  setDetermineImageNumber(true) before calling check().
 *  The complete scan over the GIF file will take additional time.</li>
 * <li>Transparency information is not included in the bits per pixel count.
 *  Actually, it was my decision not to include those bits, so it's a feature! ;-)</li>
 * </ul>
 * <p>
 * Requirements:
 * <ul>
 * <li>Java 1.1 or higher</li>
 * </ul>
 * <p>
 * The latest version can be found at <a href="http://www.geocities.com/marcoschmidt.geo/image-info.html">http://www.geocities.com/marcoschmidt.geo/image-info.html</a>.
 * <p>
 * Written by <a href="http://www.geocities.com/marcoschmidt.geo/contact.html">Marco Schmidt</a>.
 * <p>
 * This class is contributed to the Public Domain.
 * Use it at your own risk.
 * <p>
 * Last modification 2004-02-29.
 * <p>
 * History:
 * <ul>
 * <li><strong>2001-08-24</strong> Initial version.</li>
 * <li><strong>2001-10-13</strong> Added support for the file formats BMP and PCX.</li>
 * <li><strong>2001-10-16</strong> Fixed bug in read(int[], int, int) that returned
 * <li><strong>2002-01-22</strong> Added support for file formats Amiga IFF and Sun Raster (RAS).</li>
 * <li><strong>2002-01-24</strong> Added support for file formats Portable Bitmap / Graymap / Pixmap (PBM, PGM, PPM) and Adobe Photoshop (PSD).
 *   Added new method getMimeType() to return the MIME type associated with a particular file format.</li>
 * <li><strong>2002-03-15</strong> Added support to recognize number of images in file. Only works with GIF.
 *   Use {@link #setDetermineImageNumber} with <code>true</code> as argument to identify animated GIFs
 *   ({@link #getNumberOfImages()} will return a value larger than <code>1</code>).</li>
 * <li><strong>2002-04-10</strong> Fixed a bug in the feature 'determine number of images in animated GIF' introduced with version 1.1.
 *   Thanks to Marcelo P. Lima for sending in the bug report.
 *   Released as 1.1.1.</li>
 * <li><strong>2002-04-18</strong> Added {@link #setCollectComments(boolean)}.
 *  That new method lets the user specify whether textual comments are to be
 *  stored in an internal list when encountered in an input image file / stream.
 *  Added two methods to return the physical width and height of the image in dpi:
 *   {@link #getPhysicalWidthDpi()} and {@link #getPhysicalHeightDpi()}.
 *  If the physical resolution could not be retrieved, these methods return <code>-1</code>.
 *  </li>
 * <li><strong>2002-04-23</strong> Added support for the new properties physical resolution and
 *   comments for some formats. Released as 1.2.</li>
 * <li><strong>2002-06-17</strong> Added support for SWF, sent in by Michael Aird.
 *  Changed checkJpeg() so that other APP markers than APP0 will not lead to a failure anymore.
 *  Released as 1.3.</li>
 * <li><strong>2003-07-28</strong> Bug fix - skip method now takes return values into consideration.
 *  Less bytes than necessary may have been skipped, leading to flaws in the retrieved information in some cases.
 *  Thanks to Bernard Bernstein for pointing that out.
 *  Released as 1.4.</li>
 * <li><strong>2004-02-29</strong> Added support for recognizing progressive JPEG and
 *  interlaced PNG and GIF. A new method {@link #isProgressive()} returns whether ImageInfo
 *  has found that the storage type is progressive (or interlaced).
 *  Thanks to Joe Germuska for suggesting the feature.
 *  Bug fix: BMP physical resolution is now correctly determined.
 *  Released as 1.5.</li>
 * </ul>
 */
public class ImageInfo {
	/**
	 * Return value of {@link #getFormat()} for JPEG streams.
	 * ImageInfo can extract physical resolution and comments
	 * from JPEGs (only from APP0 headers).
	 * Only one image can be stored in a file.
	 * It is determined whether the JPEG stream is progressive
	 * (see {@link #isProgressive()}).
	 */
	public static final int FORMAT_JPEG = 0;

	/**
	 * Return value of {@link #getFormat()} for GIF streams.
	 * ImageInfo can extract comments from GIFs and count the number
	 * of images (GIFs with more than one image are animations).
	 * If you know of a place where GIFs store the physical resolution
	 * of an image, please
	 * <a href="http://www.geocities.com/marcoschmidt.geo/contact.html">send me a mail</a>!
	 * It is determined whether the GIF stream is interlaced (see {@link #isProgressive()}).
	 */
	public static final int FORMAT_GIF = 1;

	/**
	 * Return value of {@link #getFormat()} for PNG streams.
	 * PNG only supports one image per file.
	 * Both physical resolution and comments can be stored with PNG,
	 * but ImageInfo is currently not able to extract those.
	 * It is determined whether the PNG stream is interlaced (see {@link #isProgressive()}).
	 */
	public static final int FORMAT_PNG = 2;

	/**
	 * Return value of {@link #getFormat()} for BMP streams.
	 * BMP only supports one image per file.
	 * BMP does not allow for comments.
	 * The physical resolution can be stored.
	 */
	public static final int FORMAT_BMP = 3;

	/**
	 * Return value of {@link #getFormat()} for PCX streams.
	 * PCX does not allow for comments or more than one image per file.
	 * However, the physical resolution can be stored.
	 */
	public static final int FORMAT_PCX = 4;

	/**
	 * Return value of {@link #getFormat()} for IFF streams.
	 */
	public static final int FORMAT_IFF = 5;

	/**
	 * Return value of {@link #getFormat()} for RAS streams.
	 * Sun Raster allows for one image per file only and is not able to
	 * store physical resolution or comments.
	 */
	public static final int FORMAT_RAS = 6;

	/** Return value of {@link #getFormat()} for PBM streams. */
	public static final int FORMAT_PBM = 7;

	/** Return value of {@link #getFormat()} for PGM streams. */
	public static final int FORMAT_PGM = 8;

	/** Return value of {@link #getFormat()} for PPM streams. */
	public static final int FORMAT_PPM = 9;

	/** Return value of {@link #getFormat()} for PSD streams. */
	public static final int FORMAT_PSD = 10;

	/** Return value of {@link #getFormat()} for SWF (Shockwave) streams. */
	public static final int FORMAT_SWF = 11;

	public static final int COLOR_TYPE_UNKNOWN = -1;
	public static final int COLOR_TYPE_TRUECOLOR_RGB = 0;
	public static final int COLOR_TYPE_PALETTED = 1;
	public static final int COLOR_TYPE_GRAYSCALE= 2;
	public static final int COLOR_TYPE_BLACK_AND_WHITE = 3;

	/**
	 * The names of all supported file formats.
	 * The FORMAT_xyz int constants can be used as index values for
	 * this array.
	 */
	private static final String[] FORMAT_NAMES =
		{"JPEG", "GIF", "PNG", "BMP", "PCX",
		 "IFF", "RAS", "PBM", "PGM", "PPM",
		 "PSD", "SWF"};

	/**
	 * The names of the MIME types for all supported file formats.
	 * The FORMAT_xyz int constants can be used as index values for
	 * this array.
	 */
	private static final String[] MIME_TYPE_STRINGS =
		{"image/jpeg", "image/gif", "image/png", "image/bmp", "image/pcx",
		 "image/iff", "image/ras", "image/x-portable-bitmap", "image/x-portable-graymap", "image/x-portable-pixmap",
		 "image/psd", "application/x-shockwave-flash"};

	private int width;
	private int height;
	private int bitsPerPixel;
	private int colorType = COLOR_TYPE_UNKNOWN;
	private boolean progressive;
	private int format;
	private InputStream in;
	private DataInput din;
	private boolean collectComments = true;
	private Vector comments;
	private boolean determineNumberOfImages;
	private int numberOfImages;
	private int physicalHeightDpi;
	private int physicalWidthDpi;
	private int bitBuf;
	private int bitPos;

	private void addComment(String s) {
		if (comments == null) {
			comments = new Vector();
		}
		comments.addElement(s);
	}

	/**
	 * Call this method after you have provided an input stream or file
	 * using {@link #setInput(InputStream)} or {@link #setInput(DataInput)}.
	 * If true is returned, the file format was known and information
	 * on the file's content can be retrieved using the various getXyz methods.
	 * @return if information could be retrieved from input
	 */
	public boolean check() {
		format = -1;
		width = -1;
		height = -1;
		bitsPerPixel = -1;
		numberOfImages = 1;
		physicalHeightDpi = -1;
		physicalWidthDpi = -1;
		comments = null;
		try {
			int b1 = read() & 0xff;
			int b2 = read() & 0xff;
			if (b1 == 0x47 && b2 == 0x49) {
				return checkGif();
			}
			else
			if (b1 == 0x89 && b2 == 0x50) {
				return checkPng();
			}
			else
			if (b1 == 0xff && b2 == 0xd8) {
				return checkJpeg();
			}
			else
			if (b1 == 0x42 && b2 == 0x4d) {
				return checkBmp();
			}
			else
			if (b1 == 0x38 && b2 == 0x42) {
				return checkPsd();
			}
			else
			if ((b1 == 0x43 && b2 == 0x57) || (b1 == 0x46 && b2 == 0x57)) {
                boolean compressed = false;
                if (b1 == 0x43) compressed = true;

				return checkSwf(compressed);
			}
			else {
				return false;
			}
		} catch (IOException ioe) {
            ioe.printStackTrace();
            System.out.println(ioe);
			return false;
		}
	}

	private boolean checkBmp() throws IOException {
		byte[] a = new byte[44];
		if (read(a) != a.length) {
			return false;
		}
		width = getIntLittleEndian(a, 16);
		height = getIntLittleEndian(a, 20);
		if (width < 1 || height < 1) {
			return false;
		}
		bitsPerPixel = getShortLittleEndian(a, 26);
		if (bitsPerPixel != 1 && bitsPerPixel != 4 &&
		    bitsPerPixel != 8 && bitsPerPixel != 16 &&
		    bitsPerPixel != 24 && bitsPerPixel != 32) {
		    return false;
		}
		format = FORMAT_BMP;
		return true;
	}

	private boolean checkGif() throws IOException {
		final byte[] GIF_MAGIC_87A = {0x46, 0x38, 0x37, 0x61};
		final byte[] GIF_MAGIC_89A = {0x46, 0x38, 0x39, 0x61};
		byte[] a = new byte[11]; // 4 from the GIF signature + 7 from the global header
		if (read(a) != 11) {
			return false;
		}
		if ((!equals(a, 0, GIF_MAGIC_89A, 0, 4)) &&
			(!equals(a, 0, GIF_MAGIC_87A, 0, 4))) {
			return false;
		}
		format = FORMAT_GIF;
		width = getShortLittleEndian(a, 4);
		height = getShortLittleEndian(a, 6);
		int flags = a[8] & 0xff;
		bitsPerPixel = ((flags >> 4) & 0x07) + 1;
		progressive = (flags & 0x02) != 0;
		if (!determineNumberOfImages) {
			return true;
		}
		// skip global color palette
		if ((flags & 0x80) != 0) {
			int tableSize = (1 << ((flags & 7) + 1)) * 3;
			skip(tableSize);
		}
		numberOfImages = 0;
		int blockType;
		do
		{
			blockType = read();
			switch(blockType)
			{
				case(0x2c): // image separator
				{
					if (read(a, 0, 9) != 9) {
						return false;
					}
					flags = a[8] & 0xff;
					int localBitsPerPixel = (flags & 0x07) + 1;
					if (localBitsPerPixel > bitsPerPixel) {
						bitsPerPixel = localBitsPerPixel;
					}
					if ((flags & 0x80) != 0) {
						skip((1 << localBitsPerPixel) * 3);
					}
					skip(1); // initial code length
					int n;
					do
					{
						n = read();
						if (n > 0) {
							skip(n);
						}
						else
						if (n == -1) {
							return false;
						}
					}
					while (n > 0);
					numberOfImages++;
					break;
				}
				case(0x21): // extension
				{
					int extensionType = read();
					if (collectComments && extensionType == 0xfe) {
						StringBuffer sb = new StringBuffer();
						int n;
						do
						{
							n = read();
							if (n == -1) {
								return false;
							}
							if (n > 0) {
								for (int i = 0; i < n; i++) {
									int ch = read();
									if (ch == -1) {
										return false;
									}
									sb.append((char)ch);
								}
							}
						}
						while (n > 0);
					} else {
						int n;
						do
						{
							n = read();
							if (n > 0) {
								skip(n);
							}
							else
							if (n == -1) {
								return false;
							}
						}
						while (n > 0);
					}
					break;
				}
				case(0x3b): // end of file
				{
					break;
				}
				default:
				{
					return false;
				}
			}
		}
		while (blockType != 0x3b);
		return true;
	}


	private boolean checkJpeg() throws IOException {
		byte[] data = new byte[12];
		while (true) {
			if (read(data, 0, 4) != 4) {
				return false;
			}
			int marker = getShortBigEndian(data, 0);
			int size = getShortBigEndian(data, 2);
			if ((marker & 0xff00) != 0xff00) {
				return false; // not a valid marker
			}
			if (marker == 0xffe0) { // APPx
				if (size < 14) {
					return false; // APPx header must be >= 14 bytes
				}
				if (read(data, 0, 12) != 12) {
					return false;
				}
				final byte[] APP0_ID = {0x4a, 0x46, 0x49, 0x46, 0x00};
				skip(size - 14);
			}
			else
			if (collectComments && size > 2 && marker == 0xfffe) { // comment
				size -= 2;
				byte[] chars = new byte[size];
				if (read(chars, 0, size) != size) {
					return false;
				}
				String comment = new String(chars, "iso-8859-1");
				comment = comment.trim();
				addComment(comment);
			}
			else
			if (marker >= 0xffc0 && marker <= 0xffcf && marker != 0xffc4 && marker != 0xffc8) {
				if (read(data, 0, 6) != 6) {
					return false;
				}
				format = FORMAT_JPEG;
				bitsPerPixel = (data[0] & 0xff) * (data[5] & 0xff);
				progressive = marker == 0xffc2 || marker == 0xffc6 ||
					marker == 0xffca || marker == 0xffce;
				width = getShortBigEndian(data, 3);
				height = getShortBigEndian(data, 1);
				return true;
			} else {
				skip(size - 2);
			}
		}
	}


	private boolean checkPng() throws IOException {
		final byte[] PNG_MAGIC = {0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a};
		byte[] a = new byte[27];
		if (read(a) != 27) {
			return false;
		}
		if (!equals(a, 0, PNG_MAGIC, 0, 6)) {
			return false;
		}
		format = FORMAT_PNG;
		width = getIntBigEndian(a, 14);
		height = getIntBigEndian(a, 18);
		bitsPerPixel = a[22] & 0xff;
		int colorType = a[23] & 0xff;
		if (colorType == 2 || colorType == 6) {
			bitsPerPixel *= 3;
		}
		progressive = (a[26] & 0xff) != 0;
		return true;
	}


	private boolean checkPsd() throws IOException {
		byte[] a = new byte[24];
		if (read(a) != a.length) {
			return false;
		}
		final byte[] PSD_MAGIC = {0x50, 0x53};
		if (!equals(a, 0, PSD_MAGIC, 0, 2)) {
			return false;
		}
		format = FORMAT_PSD;
		width = getIntBigEndian(a, 16);
		height = getIntBigEndian(a, 12);
		int channels = getShortBigEndian(a, 10);
		int depth = getShortBigEndian(a, 20);
		bitsPerPixel = channels * depth;
		return (width > 0 && height > 0 && bitsPerPixel > 0 && bitsPerPixel <= 64);
	}


	// Written by Anders Skar
	private boolean checkSwf(boolean isCompressed) throws IOException {
		// Get rid version and length
		byte[] tmp = new byte[6];
		if (read(tmp) != tmp.length) {
			return false;
		}

        if (isCompressed) {
            in = new InflaterInputStream(in);
        }

		format = FORMAT_SWF;
		int bitSize = (int)readUBits( 5 );
		int minX = (int)readSBits( bitSize );
		int maxX = (int)readSBits( bitSize );
		int minY = (int)readSBits( bitSize );
		int maxY = (int)readSBits( bitSize );
		width = (maxX-minX)/20; //cause we're in twips
		height = (maxY-minY)/20;  //cause we're in twips
		return (width > 0 && height > 0);
	}

	/**
	 * Run over String list, return false iff at least one of the arguments
	 * equals <code>-c</code>.
	 */
	private static boolean determineVerbosity(String[] args) {
		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if ("-c".equals(args[i])) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean equals(byte[] a1, int offs1, byte[] a2, int offs2, int num) {
		while (num-- > 0) {
			if (a1[offs1++] != a2[offs2++]) {
				return false;
			}
		}
		return true;
	}


	/**
	 * If {@link #check()} was successful, returns the image format as one
	 * of the FORMAT_xyz constants from this class.
	 * Use {@link #getFormatName()} to get a textual description of the file format.
	 * @return file format as a FORMAT_xyz constant
	 */
	public int getFormat() {
		return format;
	}

	/**
	 * If {@link #check()} was successful, returns the image format's name.
	 * Use {@link #getFormat()} to get a unique number.
	 * @return file format name
	 */
	public String getFormatName() {
		if (format >= 0 && format < FORMAT_NAMES.length) {
			return FORMAT_NAMES[format];
		} else {
			return "?";
		}
	}

	/**
	 * If {@link #check()} was successful, returns one the image's vertical
	 * resolution in pixels.
	 * @return image height in pixels
	 */
	public int getHeight() {
		return height;
	}

	private int getIntBigEndian(byte[] a, int offs) {
		return
			(a[offs] & 0xff) << 24 |
			(a[offs + 1] & 0xff) << 16 |
			(a[offs + 2] & 0xff) << 8 |
			a[offs + 3] & 0xff;
	}

	private int getIntLittleEndian(byte[] a, int offs) {
		return
			(a[offs + 3] & 0xff) << 24 |
			(a[offs + 2] & 0xff) << 16 |
			(a[offs + 1] & 0xff) << 8 |
			a[offs] & 0xff;
	}

	/**
	 * If {@link #check()} was successful, returns a String with the
	 * MIME type of the format.
	 * @return MIME type, e.g. <code>image/jpeg</code>
	 */
	public String getMimeType() {
		if (format >= 0 && format < MIME_TYPE_STRINGS.length) {
			if (format == FORMAT_JPEG && progressive)
			{
				return "image/pjpeg";
			}
			return MIME_TYPE_STRINGS[format];
		} else {
			return null;
		}
	}

	private int getShortBigEndian(byte[] a, int offs) {
		return
			(a[offs] & 0xff) << 8 |
			(a[offs + 1] & 0xff);
	}

	private int getShortLittleEndian(byte[] a, int offs) {
		return (a[offs] & 0xff) | (a[offs + 1] & 0xff) << 8;
	}

	/**
	 * If {@link #check()} was successful, returns one the image's horizontal
	 * resolution in pixels.
	 * @return image width in pixels
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * To use this class as a command line application, give it either
	 * some file names as parameters (information on them will be
	 * printed to standard output, one line per file) or call
	 * it with no parameters. It will then check data given to it
	 * via standard input.
	 * @param args the program arguments which must be file names
	 */
	public static void main(String[] args) {
		ImageInfo imageInfo = new ImageInfo();
		boolean verbose = determineVerbosity(args);
		if (args.length == 0) {
			run(null, System.in, imageInfo, verbose);
		} else {
			int index = 0;
			while (index < args.length) {
				InputStream in = null;
				try {
					String name = args[index++];
					System.out.print(name + ";");
					if (name.startsWith("http://")) {
						in = new URL(name).openConnection().getInputStream();
					} else {
						in = new FileInputStream(name);
					}
					run(name, in, imageInfo, verbose);
					in.close();
				} catch (Exception e) {
                    System.out.println(e);
					try {
						in.close();
					} catch (Exception ee) {
					}
				}
			}
		}
	}

	private static void print(String sourceName, ImageInfo ii, boolean verbose) {
		if (verbose) {
			printVerbose(sourceName, ii);
		} else {
			printCompact(sourceName, ii);
		}
	}

	private static void printCompact(String sourceName, ImageInfo imageInfo) {
		System.out.println(
			imageInfo.getFormatName() + ";" +
			imageInfo.getMimeType() + ";" +
			imageInfo.getWidth() + ";" +
			imageInfo.getHeight()
		);
	}


	private static void printLine(int indentLevels, String text, int value, int minValidValue) {
		if (value >= minValidValue) {
			printLine(indentLevels, text, Integer.toString(value));
		}
	}

	private static void printLine(int indentLevels, String text, String value) {
		if (value == null || value.length() == 0) {
			return;
		}
		while (indentLevels-- > 0) {
			System.out.print("\t");
		}
		if (text != null && text.length() > 0) {
			System.out.print(text);
			System.out.print(" ");
		}
		System.out.println(value);
	}

	private static void printVerbose(String sourceName, ImageInfo ii) {
		printLine(0, null, sourceName);
		printLine(1, "File format: ", ii.getFormatName());
		printLine(1, "MIME type: ", ii.getMimeType());
		printLine(1, "Width (pixels): ", ii.getWidth(), 1);
		printLine(1, "Height (pixels): ", ii.getHeight(), 1);
	}

	private int read() throws IOException {
		if (in != null) {
			return in.read();
		} else {
			return din.readByte();
		}
	}

	private int read(byte[] a) throws IOException {
		if (in != null) {
			return in.read(a);
		} else {
			din.readFully(a);
			return a.length;
		}
	}

	private int read(byte[] a, int offset, int num) throws IOException {
		if (in != null) {
			return in.read(a, offset, num);
		} else {
			din.readFully(a, offset, num);
			return num;
		}
	}


	private long readUBits( int numBits ) throws IOException
	{
		if (numBits == 0) {
			return 0;
		}
		int bitsLeft = numBits;
		long result = 0;
		if (bitPos == 0) { //no value in the buffer - read a byte
			if (in != null) {
				bitBuf = in.read();
			} else {
				bitBuf = din.readByte();
			}
			bitPos = 8;
		}

	    while( true )
        {
            int shift = bitsLeft - bitPos;
            if( shift > 0 )
            {
                // Consume the entire buffer
                result |= bitBuf << shift;
                bitsLeft -= bitPos;

                // Get the next byte from the input stream
                if (in != null) {
                  bitBuf = in.read();
                } else {
                  bitBuf = din.readByte();
                }
                bitPos = 8;
            }
            else
            {
             	// Consume a portion of the buffer
                result |= bitBuf >> -shift;
                bitPos -= bitsLeft;
                bitBuf &= 0xff >> (8 - bitPos);	// mask off the consumed bits

                return result;
            }
        }
    }

        /**
     * Read a signed value from the given number of bits
     */
    private int readSBits( int numBits ) throws IOException
    {
        // Get the number as an unsigned value.
        long uBits = readUBits( numBits );

        // Is the number negative?
        if( ( uBits & (1L << (numBits - 1))) != 0 )
        {
            // Yes. Extend the sign.
            uBits |= -1L << numBits;
        }

        return (int)uBits;
    }

	private static void run(String sourceName, InputStream in, ImageInfo imageInfo, boolean verbose) {
		imageInfo.setInput(in);
		imageInfo.setCollectComments(verbose);
		if (imageInfo.check()) {
			print(sourceName, imageInfo, verbose);
		} else {
            System.out.println("not supported:" + sourceName);
        }
	}

	/**
	 * Specify whether textual comments are supposed to be extracted from input.
	 * Default is <code>false</code>.
	 * If enabled, comments will be added to an internal list.
	 * @param newValue if <code>true</code>, this class will read comments
	 */
	public void setCollectComments(boolean newValue)
	{
		collectComments = newValue;
	}


	/**
	 * Set the input stream to the argument stream (or file).
	 * Note that {@link java.io.RandomAccessFile} implements
	 * {@link java.io.DataInput}.
	 * @param dataInput the input stream to read from
	 */
	public void setInput(DataInput dataInput) {
		din = dataInput;
		in = null;
	}

	/**
	 * Set the input stream to the argument stream (or file).
	 * @param inputStream the input stream to read from
	 */
	public void setInput(InputStream inputStream) {
		in = inputStream;
		din = null;
	}


	private void skip(int num) throws IOException {
		while (num > 0) {
			long result;
			if (in != null) {
				result = in.skip(num);
			} else {
				result = din.skipBytes(num);
			}
			if (result > 0) {
				num -= result;
			}
		}
	}

}
