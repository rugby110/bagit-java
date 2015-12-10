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
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import gov.loc.error.ArgumentException;
import gov.loc.error.NonexistentBagException;
import gov.loc.structure.StructureConstants;

public class RemoveProcessorTest extends Assert {
  @Rule
  public TemporaryFolder folder= new TemporaryFolder();
  
  @Before
  public void setup() throws Exception{
    URL url = this.getClass().getClassLoader().getResource("bags/v1_0/incomplete");
    final Path starting = Paths.get(url.toURI());
    final Path ending = Paths.get(folder.getRoot().toURI());
    
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
    
    System.setProperty("user.dir", folder.getRoot().toString());
  }
  
  @Test
  public void testRemoveDirectory() throws Exception{
    String lineToBeRemoved = "bead9f5795d40067c7cd30d913eb5f487069b6fb bar/ham.txt";

    RemoveProcessor.remove(new String[]{"--files", "bar"});
    
    File manifestFile = new File(folder.getRoot(), StructureConstants.DOT_BAG_FOLDER_NAME + File.separator + "manifest-sha1.txt");
    List<String> lines = Files.readAllLines(Paths.get(manifestFile.toURI()));
    assertFalse(lines.contains(lineToBeRemoved));
  }

  @Test
  public void testRemoveFile() throws Exception{
    String lineToBeRemoved = "f572d396fae9206628714fb2ce00f72e94f2258f hello.txt";

    RemoveProcessor.remove(new String[]{"--files", "hello.txt"});
    
    File manifestFile = new File(folder.getRoot(), StructureConstants.DOT_BAG_FOLDER_NAME + File.separator + "manifest-sha1.txt");
    List<String> lines = Files.readAllLines(Paths.get(manifestFile.toURI()));
    assertFalse(lines.contains(lineToBeRemoved));
  }
  
  @Test
  public void testRemoveInfo() throws Exception{
    String lineToBeRemoved = "foo:bar";

    RemoveProcessor.remove(new String[]{"--info", "foo"});
    
    File manifestFile = new File(folder.getRoot(), 
        StructureConstants.DOT_BAG_FOLDER_NAME + File.separator + StructureConstants.BAG_INFO_FILE_NAME);
    List<String> lines = Files.readAllLines(Paths.get(manifestFile.toURI()));
    assertFalse(lines.contains(lineToBeRemoved));
  }
  
  @Test(expected=ArgumentException.class)
  public void testBadArgument() throws Exception{
    RemoveProcessor.remove(new String[]{"--foo"});
  }
  
  @Test(expected=NonexistentBagException.class)
  public void testRemoveWhenNotInBag() throws Exception{
    File nonBagDir = folder.newFolder();
    System.setProperty("user.dir", nonBagDir.toString());
    RemoveProcessor.remove(new String[]{"--files", "hello.txt"});
  }
}
