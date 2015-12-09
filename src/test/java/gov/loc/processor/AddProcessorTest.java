package gov.loc.processor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import gov.loc.error.ArgumentException;
import gov.loc.error.NonexistentBagException;
import gov.loc.structure.StructureConstants;

public class AddProcessorTest extends Assert {
  @Rule
  public TemporaryFolder folder= new TemporaryFolder();

  @Test
  public void testAddFiles() throws Exception{
    createMockBag();
    File folderToAdd = folder.newFolder("bar");
    File barFile = new File(folderToAdd, "bar.txt");
    Files.write(Paths.get(barFile.toURI()), "hello world!".getBytes(), StandardOpenOption.CREATE);
    
    System.setProperty("user.dir", folder.getRoot().toString());
    File fooFile = new File(folder.getRoot(), "foo.txt");
    Files.write(Paths.get(fooFile.toURI()), "hello world!".getBytes(), StandardOpenOption.CREATE);
    
    AddProcessor.add(new String[] {"--files", "foo.txt", "bar", "nonexistingFile"});
  }
  
  @Test
  public void testAddInfo() throws Exception{
    createMockBag();
    System.setProperty("user.dir", folder.getRoot().toString());
    
    AddProcessor.add(new String[] {"--info", "foo=bar"});
  }
  
  @Test(expected=ArgumentException.class)
  public void testAddWithIncorrectNumberOfArguments() throws Exception{
    AddProcessor.add(new String[]{});
  }
  
  @Test(expected=NonexistentBagException.class)
  public void testAddWithNoBag() throws Exception{
    AddProcessor.add(new String[]{"--files foo.txt"});
  }
  
  @Test(expected=ArgumentException.class)
  public void testAddWithBadArgument() throws Exception{
    createMockBag();
    System.setProperty("user.dir", folder.getRoot().toString());
    AddProcessor.add(new String[]{"--foo"});
  }
  
  @Test(expected=ArgumentException.class)
  public void testAddInfoWithBadArgument() throws Exception{
    createMockBag();
    System.setProperty("user.dir", folder.getRoot().toString());
    AddProcessor.add(new String[]{"--info", "foo", "bar"});
  }
  
  private void createMockBag() throws IOException{
    File dotBagDir = folder.newFolder(StructureConstants.DOT_BAG_FOLDER_NAME);
    
    File bagitFile = new File(dotBagDir, StructureConstants.BAGIT_FILE_NAME);
    Files.write(Paths.get(bagitFile.toURI()), "BagIt-Version:1.0".getBytes(), StandardOpenOption.CREATE);
    
    File manifestFile = new File(dotBagDir, StructureConstants.FILE_MANIFEST_FILE_NAME_PREFIX + "sha1" + StructureConstants.FILE_MANIFEST_FILE_NAME_SUFFIX);
    manifestFile.createNewFile();
    
    File tagManifestFile = new File(dotBagDir, StructureConstants.TAG_MANIFEST_FILE_NAME_PREFIX + "sha1" + StructureConstants.TAG_MANIFEST_FILE_NAME_SUFFIX);
    tagManifestFile.createNewFile();
  }
}
