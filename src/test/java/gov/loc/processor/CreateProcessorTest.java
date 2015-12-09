package gov.loc.processor;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import gov.loc.error.ArgumentException;
import gov.loc.structure.StructureConstants;

public class CreateProcessorTest extends Assert {
  @Rule
  public TemporaryFolder folder= new TemporaryFolder();
  
  @Before
  public void setup() throws Exception{
    createTestFoldersAndFiles();
    System.setProperty("user.dir", folder.getRoot().toString());
  }
  
  @Test
  public void testDefaultCreate() throws Exception{
    CreateProcessor.create(new String[]{});
    
    assertFilesExist();
  }
  
  @Test
  public void testCreateWithInclude() throws Exception{
    CreateProcessor.create(new String[]{"--include",".*"});
    
    assertFilesExist();
  }
  
  @Test
  public void testCreateWithExclude() throws Exception{
    CreateProcessor.create(new String[]{"--exclude",".*ham.*"});
    
    assertFilesExist();
  }
  
  @Test(expected=ArgumentException.class)
  public void testCreateWithWrongNumberOfArguments() throws Exception{
    CreateProcessor.create(new String[]{"--include"});
  }
  
  @Test(expected=ArgumentException.class)
  public void testCreateWithBadArguments() throws Exception{
    CreateProcessor.create(new String[]{"--foo", "foo"});
  }
  
  private void createTestFoldersAndFiles() throws IOException{
    File hamDir = folder.newFolder("foo", "bar", "ham");
    File hamFile = new File(hamDir, "ham.txt");
    hamFile.createNewFile();
    
    File barDir = hamDir.getParentFile();
    File barFile = new File(barDir, "bar.txt");
    barFile.createNewFile();
    
    File fooDir = barDir.getParentFile();
    File fooFile = new File(fooDir, "foo.txt");
    fooFile.createNewFile();
  }
  
  private void assertFilesExist(){
    File dotBagDir = new File(folder.getRoot(), StructureConstants.DOT_BAG_FOLDER_NAME);
    assertTrue(dotBagDir.exists());
    File manifestFile = new File(dotBagDir, "manifest-sha1.txt");
    assertTrue(manifestFile.exists());
    File tagManifestFile = new File(dotBagDir, "tagmanifest-sha1.txt");
    assertTrue(tagManifestFile.exists());
    File bagitFile = new File(dotBagDir, StructureConstants.BAGIT_FILE_NAME);
    assertTrue(bagitFile.exists());
  }
}
