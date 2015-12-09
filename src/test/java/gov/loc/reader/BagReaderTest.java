package gov.loc.reader;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import gov.loc.domain.Bag;

public class BagReaderTest extends Assert {
  
  @Test
  public void testRead() throws Exception{
    URL url = this.getClass().getClassLoader().getResource("bags/v1_0");
    File directory = new File(url.toURI());
    
    Bag expectedBag = new Bag();
    expectedBag.setHashAlgorithm("sha1");
    expectedBag.setRootDir(directory);
    expectedBag.setVersion("1.0");
    expectedBag.setBagInfo(new HashMap<String,String>());
    
    Map<String,String> tagManifest = new HashMap<>();
    tagManifest.put("bc91bfa1ed344269a304bb491dc52db8d6832513", "bagit.txt");
    tagManifest.put("da39a3ee5e6b4b0d3255bfef95601890afd80709", "bag-info.txt");
    tagManifest.put("16d33c58b2d14d4de32ab9d2b4d3a6736ff72728", "manifest-sha1.txt");
    expectedBag.setTagManifest(tagManifest);
    
    Map<String,String> fileManifest = new HashMap<>();
    fileManifest.put("bead9f5795d40067c7cd30d913eb5f487069b6fb", "bar/ham.txt");
    fileManifest.put("f572d396fae9206628714fb2ce00f72e94f2258f", "hello.txt");
    expectedBag.setFileManifest(fileManifest);
    
    Bag createdBag = BagReader.createBag(directory);
    assertEquals(expectedBag, createdBag);
  }
}
