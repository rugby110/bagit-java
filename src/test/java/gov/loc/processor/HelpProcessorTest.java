package gov.loc.processor;

import org.junit.Assert;
import org.junit.Test;

import gov.loc.error.ArgumentException;

public class HelpProcessorTest extends Assert {

  @Test(expected=ArgumentException.class)
  public void testHelpWithNoArguments(){
    HelpProcessor.help(new String[]{});
  }
  
  @Test(expected=ArgumentException.class)
  public void testHelpWithTooManyArguments(){
    HelpProcessor.help(new String[]{"foo", "bar"});
  }
  
  @Test(expected=ArgumentException.class)
  public void testHelpWithBadArgument(){
    HelpProcessor.help(new String[]{"foo"});
  }
  
  @Test
  public void testHelpWithConvert(){
    HelpProcessor.help(new String[]{"convert"});
  }
  
  @Test
  public void testHelpWithCreate(){
    HelpProcessor.help(new String[]{"create"});
  }
  
  @Test
  public void testHelpWithVerify(){
    HelpProcessor.help(new String[]{"verify"});
  }
  
  @Test
  public void testHelpWithAdd(){
    HelpProcessor.help(new String[]{"add"});
  }
  
  @Test
  public void testHelpWithRemove(){
    HelpProcessor.help(new String[]{"remove"});
    HelpProcessor.help(new String[]{"rm"});
  }
  
  @Test
  public void testHelpWithList(){
    HelpProcessor.help(new String[]{"list"});
    HelpProcessor.help(new String[]{"ls"});
  }
  
  @Test
  public void testHelpWithHelp(){
    HelpProcessor.help(new String[]{"help"});
  }
  
  @Test
  public void testPrintUsage(){
    HelpProcessor.printUsage();
  }
}
