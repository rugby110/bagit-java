package gov.loc.writer;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import gov.loc.domain.Bag;
import gov.loc.domain.Version;
import gov.loc.structure.StructureConstants;

public class BagWriterTest extends Assert {
  @Rule
  public TemporaryFolder folder= new TemporaryFolder();
  
  @Test
  public void testWrite() throws Exception{
    System.setProperty("user.dir", folder.getRoot().toString());
    
    Bag bag = new Bag();
    bag.setBagInfo(new HashMap<String, List<String>>());
    bag.setFileManifest(new HashMap<String, String>());
    bag.setHashAlgorithm("sha1");
    bag.setRootDir(Paths.get(folder.getRoot().toURI()));
    bag.setVersion(new Version(0, 98));
    
    BagWriter.write(bag);
    
    File dotBagDir = new File(folder.getRoot(), StructureConstants.DOT_BAG_FOLDER_NAME);
    assertTrue(dotBagDir.exists());
    File manifestFile = new File(dotBagDir, "manifest-sha1.txt");
    assertTrue(manifestFile.exists());
    File bagitFile = new File(dotBagDir, StructureConstants.BAGIT_FILE_NAME);
    assertTrue(bagitFile.exists());    
  }
}
