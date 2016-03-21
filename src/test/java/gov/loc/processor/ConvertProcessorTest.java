package gov.loc.processor;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import gov.loc.error.UnsupportedConvertionException;
import gov.loc.structure.StructureConstants;

public class ConvertProcessorTest extends Assert {
  @Rule
  public TemporaryFolder folder= new TemporaryFolder();
  
  @Test
  public void testConvertVersions() throws Exception{
    List<String> versionsToTest = Arrays.asList("v0_93","v0_94","v0_95","v0_96","v0_97");
    for(String versionToTest : versionsToTest){
      URL url = this.getClass().getClassLoader().getResource("bags/" + versionToTest + "/bag");
      Path source = Paths.get(url.toURI());
      Path target = Paths.get(folder.getRoot().getPath(), versionToTest);
      createTestFilesAndFolders(source, target);
      
      System.setProperty("user.dir", target.toString());
      
      ConvertProcessor.convert();
      File dotBagDir = Paths.get(target.toString(), StructureConstants.DOT_BAG_FOLDER_NAME).toFile();
      assertTrue(dotBagDir.exists());
    }
  }
  
  @Test(expected=UnsupportedConvertionException.class)
  public void testConvertUnsupportedVersion() throws Exception{
    String versionToTest = "someOtherVersion";
    URL url = this.getClass().getClassLoader().getResource("bags/" + versionToTest + "/bag");
    Path source = Paths.get(url.toURI());
    Path target = Paths.get(folder.getRoot().getPath(), versionToTest);
    createTestFilesAndFolders(source, target);
    
    System.setProperty("user.dir", target.toString());
    
    ConvertProcessor.convert();
  }
  
  private void createTestFilesAndFolders(final Path starting, final Path ending) throws Exception{
    Files.walkFileTree(starting, new SimpleFileVisitor<Path>(){
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
           if(!attrs.isDirectory()){
             Path relative = starting.relativize(file);
             Path target = ending.resolve(relative);
             Files.createDirectories(target.getParent());
             Files.copy(file, target);
           }
           return FileVisitResult.CONTINUE;
       }
    });
  }
}
