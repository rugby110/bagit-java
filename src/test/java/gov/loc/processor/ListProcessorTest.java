package gov.loc.processor;

import java.io.File;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.loc.error.ArgumentException;
import gov.loc.error.NonexistentBagException;

public class ListProcessorTest extends Assert {
  
  @Before
  public void setup() throws Exception{
    URL url = this.getClass().getClassLoader().getResource("bags/v0_98/incomplete");
    File directory = new File(url.toURI());
    
    System.setProperty("user.dir", directory.toString());
  }
  
  @Test(expected=NonexistentBagException.class)
  public void testListWhenNotInABag() throws Exception{
    File directory = new File(System.getProperty("java.io.tmpdir"));
    System.setProperty("user.dir", directory.toString());
    ListProcessor.list(new String[]{});
  }
  
  @Test
  public void testListFiles() throws Exception{
    ListProcessor.list(new String[]{"--files"});
  }
  
  @Test
  public void testListInfo() throws Exception{
    ListProcessor.list(new String[]{"--info"});
  }
  
  @Test
  public void testListMissing() throws Exception{
    ListProcessor.list(new String[]{"--missing"});
  }
  
  @Test
  public void testListWithNoArguments() throws Exception{
    ListProcessor.list(new String[]{});
  }
  
  @Test(expected=ArgumentException.class)
  public void testUnrecognizedArgument() throws Exception{
    ListProcessor.list(new String[]{"--foo"});
  }
}
