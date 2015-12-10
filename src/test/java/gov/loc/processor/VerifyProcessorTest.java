package gov.loc.processor;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.loc.error.ArgumentException;
import gov.loc.error.NonexistentBagException;

public class VerifyProcessorTest extends Assert {

  @Before
  public void setup() throws Exception{
    File bagFile = new File(this.getClass().getClassLoader().getResource("bags/v1_0/incomplete").toURI());
    System.setProperty("user.dir", bagFile.toString());
  }
  
  @Test
  public void testVerifyWithNoArguments() throws Exception{
    VerifyProcessor.verify(new String[]{});
  }
  
  @Test
  public void testVerifyAll() throws Exception{
    VerifyProcessor.verify(new String[]{"--all"});
  }
  
  @Test
  public void testVerifyFiles() throws Exception{
    VerifyProcessor.verify(new String[]{"--files"});
  }
  
  @Test
  public void testVerifyTags() throws Exception{
    VerifyProcessor.verify(new String[]{"--tags"});
  }
  
  @Test(expected=ArgumentException.class)
  public void testVerifyWithBadArgument() throws Exception{
    VerifyProcessor.verify(new String[]{"--foo"});
  }
  
  @Test(expected=ArgumentException.class)
  public void testVerifyWithWrongNumberOfArgument() throws Exception{
    VerifyProcessor.verify(new String[]{"--files", "--tags"});
  }
  
  @Test(expected=NonexistentBagException.class)
  public void testVerifyWhenNotInBag() throws Exception{
    File notBagDir = new File(System.getProperty("java.io.tmpdir"));
    System.setProperty("user.dir", notBagDir.toString());
    VerifyProcessor.verify(new String[]{"--files"});
  }
}
