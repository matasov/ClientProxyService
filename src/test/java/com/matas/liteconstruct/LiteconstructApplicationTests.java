package com.matas.liteconstruct;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LiteconstructApplicationTests {

  @Test
  public void contextLoads() {}

  @Test
  public void iterator_will_return_hello_world() {
    // подготавливаем
    Iterator i = mock(Iterator.class);
    when(i.next()).thenReturn("Hello").thenReturn("World");
    // выполняем
    String result = i.next() + " " + i.next();
    // сравниваем
    assertEquals("Hello World", result);
    System.out.println("ok!!!");
  }

  @Test
  public void with_arguments() {
    Comparable c = mock(Comparable.class);
    when(c.compareTo("Test")).thenReturn(1);
    assertEquals(1, c.compareTo("Test"));
  }

  @Test(expected = IOException.class)
  public void OutputStreamWriter_rethrows_an_exception_from_OutputStream() throws IOException {
    OutputStream mock = mock(OutputStream.class);
    OutputStreamWriter osw = new OutputStreamWriter(mock);
    doThrow(new IOException()).when(mock).close();
    osw.close();
  }
  
  @Test
  public void OutputStreamWriter_Closes_OutputStream_on_Close()
          throws IOException {
      OutputStream mock = mock(OutputStream.class);
      OutputStreamWriter osw = new OutputStreamWriter(mock);
      osw.close();
      verify(mock).close();
  }
  
  @Test
  public void OutputStreamWriter_Buffers_And_Forwards_To_OutputStream() 
          throws IOException {        
      OutputStream mock = mock(OutputStream.class);
      OutputStreamWriter osw = new OutputStreamWriter(mock);
      osw.write('a');
      osw.flush();
      // не можем делать так, потому что мы не знаем,
      // насколько длинным может быть массив
      // verify(mock).write(new byte[]{'a'}, 0, 1);

      BaseMatcher<byte[]> arrayStartingWithA = new BaseMatcher<byte[]>() {
          @Override
          public void describeTo(Description description) {
              // пустота
          }
          // Проверяем, что первый символ - это A
          @Override
          public boolean matches(Object item) {
              byte[] actual = (byte[]) item;
              return actual[0] == 'a';
          }
      };
      // проверяем, что первый символ массива - это A, и что другие два аргумента равны 0 и 1.
//      verify(mock).write(argThat(arrayStartingWithA), eq(0), eq(1));  
  }

}
