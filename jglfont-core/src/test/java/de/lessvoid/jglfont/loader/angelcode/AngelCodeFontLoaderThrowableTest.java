package de.lessvoid.jglfont.loader.angelcode;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.lessvoid.jglfont.loader.angelcode.AngelCodeFontLoader;
import de.lessvoid.jglfont.loader.angelcode.line.LineProcessors;

public class AngelCodeFontLoaderThrowableTest {
  private AngelCodeFontLoader fontLoader;
  private LineProcessors lineProcessorsMock;
  private boolean closeCalled = false;

  @Before
  public void before() {
    closeCalled = false;
    lineProcessorsMock = createMock(LineProcessors.class);
    replay(lineProcessorsMock);
    fontLoader = new AngelCodeFontLoader(lineProcessorsMock);
  }

  @Test(expected=Throwable.class)
  public void testInputStreamReadRuntimeException() throws Exception {
    fontLoader.load(createInputStreamWithRuntimeException());
  }

  @Test(expected=Throwable.class)
  public void testInputStreamReadRuntimeExceptionAndCloseException() throws Exception {
    fontLoader.load(createInputStreamWithRuntimeExceptionAndCloseException());
  }

  @After
  public void after() {
    verify(lineProcessorsMock);
    assertTrue(closeCalled);
  }

  private InputStream createInputStreamWithRuntimeException() throws Exception {
    InputStream in = new ByteArrayInputStream("stuff\n".getBytes("ISO-8859-1")) {
      @Override
      public synchronized int read(byte[] b, int off, int len) {
        throw new IOError(new NullPointerException());
      }      
      @Override
      public void close() throws IOException {
        super.close();
        closeCalled = true;
      }
    };
    return in;
  }

  private InputStream createInputStreamWithRuntimeExceptionAndCloseException() throws Exception {
    InputStream in = new ByteArrayInputStream("stuff\n".getBytes("ISO-8859-1")) {
      @Override
      public synchronized int read(byte[] b, int off, int len) {
        throw new IOError(new NullPointerException());
      }

      @Override
      public void close() throws IOException {
        closeCalled = true;
        throw new IOException();
      }
    };
    return in;
  }
}