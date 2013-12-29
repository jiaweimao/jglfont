package org.jglfont;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jglfont.impl.JGLFontImpl;
import org.jglfont.impl.ClasspathResourceLoader;
import org.jglfont.impl.format.JGLFontLoader;
import org.jglfont.impl.format.angelcode.AngelCodeJGLFontLoader;
import org.jglfont.impl.format.angelcode.AngelCodeLineProcessors;
import org.jglfont.spi.JGLFontRenderer;
import org.jglfont.spi.ResourceLoader;

public class JGLFontFactory {
  public final static int FONT_STYLE_NONE   = 0;
  public final static int FONT_STYLE_BOLD   = 1;
  public final static int FONT_STYLE_ITALIC = 1<<1;

  private final JGLFontRenderer fontRenderer;
  private final ResourceLoader resourceLoader;
  private final String defaultSuffix = "fnt";

  private final static Map<String, JGLFontLoader> loaders = new ConcurrentHashMap<String, JGLFontLoader>();

  static {
    loaders.put("fnt", new AngelCodeJGLFontLoader(new AngelCodeLineProcessors()));
  }

  public JGLFontFactory(final JGLFontRenderer fontRenderer) {
    this(fontRenderer, new ClasspathResourceLoader());
  }

  public JGLFontFactory(final JGLFontRenderer fontRenderer, final ResourceLoader resourceLoader) {
    this.fontRenderer = fontRenderer;
    this.resourceLoader = resourceLoader;
  }

  public JGLFont loadFont(final InputStream stream, final String filenameWithHash) throws IOException {
    String hash = "";
    String filename = filenameWithHash;
    int i = filenameWithHash.lastIndexOf('#');
    int sep = filenameWithHash.lastIndexOf(File.separatorChar);
    int dot = filenameWithHash.lastIndexOf('.');
    if (i > 0 && i > sep && i > dot) {
      hash = filenameWithHash.substring(i+1);
      filename = filenameWithHash.substring(0, i-1);
    }
    if (hash.isEmpty()) {
      return loadFont(stream, filename, 16);
    }

    int size = 16;
    int style = FONT_STYLE_NONE;
    StringBuilder sb = new StringBuilder();

    String[] blocks = hash.split(";");
    for (String block : blocks) {
      String[] keyvalue = block.split("=");
      if (keyvalue.length == 2) {
        if (keyvalue[0].equalsIgnoreCase("size")) {
          try {
            size = Integer.parseInt(keyvalue[1]);
          } catch (NumberFormatException ignore) {
          }
        } else {
          append(sb, block);
        }
      } else if (keyvalue.length == 1) {
        if (keyvalue[0].equalsIgnoreCase("bold")) {
          style |= FONT_STYLE_BOLD;
        } else if (keyvalue[0].equalsIgnoreCase("italic")) {
          style |= FONT_STYLE_ITALIC;
        } else {
          append(sb, block);
        }
      } else {
        append(sb, block);
      }
    }

    return loadFont(stream, filename, size, style, sb.toString());
  }

  private void append(StringBuilder sb, String block) {
    if (sb.length() == 0) {
      sb.append(block);
    } else {
      sb.append(';').append(block);
    }
  }

  public JGLFont loadFont(final InputStream stream, final String filename, final int size) throws IOException {
    return loadFont(stream, filename, size, FONT_STYLE_NONE);
  }

  public JGLFont loadFont(final InputStream stream, final String filename, final int size, final int style) throws IOException {
    return loadFont(stream, filename, size, style, "");
  }

  public JGLFont loadFont(final InputStream stream, final String filename, final int size, final int style, final String params) throws IOException {
    String suffix = defaultSuffix;

    int i = filename.lastIndexOf('.');
    if (i > 0) {
      suffix = filename.substring(i+1).toLowerCase();
    }

    JGLFontLoader loader = loaders.get(suffix);
    if (loader == null) {
      loader = loaders.get(defaultSuffix);
    }

    return new JGLFontImpl(loader.load(fontRenderer, resourceLoader, stream, filename, size, style, params));
  }
}
