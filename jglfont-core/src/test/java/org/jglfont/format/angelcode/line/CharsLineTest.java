package org.jglfont.format.angelcode.line;

import static org.junit.Assert.*;

import org.jglfont.impl.format.JGLAbstractFontData;
import org.jglfont.impl.format.JGLBitmapFontData;
import org.jglfont.impl.format.angelcode.AngelCodeLineData;
import org.jglfont.impl.format.angelcode.line.CharsLine;
import org.junit.Test;


public class CharsLineTest {
  private CharsLine charsLine = new CharsLine();
  private AngelCodeLineData line = new AngelCodeLineData();
  private JGLAbstractFontData font = new JGLBitmapFontData(null, null, null);

  @Test
  public void testNoImpl() {
    assertTrue(charsLine.process(line, font));
    assertTrue(font.getBitmaps().isEmpty());
  }
}
