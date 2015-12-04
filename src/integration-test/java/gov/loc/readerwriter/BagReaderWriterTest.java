package gov.loc.readerwriter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import gov.loc.domain.Bag;
import gov.loc.reader.BagReader;
import gov.loc.writer.BagWriter;

//TODO move to integration test suite
public class BagReaderWriterTest extends Assert {
  @Rule
  public TemporaryFolder folder= new TemporaryFolder();
  
  //integration test
  @Test
  public void testReaderWriter() throws Exception{
    Bag bag = createTestBag();
    BagWriter.write(bag);
    Bag readBag = BagReader.createBag(bag.getRootDir());
    assertEquals(bag, readBag);
  }
  
  private Bag createTestBag(){
    Bag bag = new Bag();
    bag.setRootDir(folder.getRoot());
    
    Map<String, String> bagInfo = new HashMap<>();
    bagInfo.put("Bag-Size", "0");
    bagInfo.put("Bagging-Date", LocalDate.now().toString());
    bag.setBagInfo(bagInfo);
    
    Map<String, String> fileManifest = new HashMap<>();
    fileManifest.put("8ad8757baa8564dc136c1e07507f4a98", "foo/bar.txt");
    bag.setFileManifest(fileManifest);
    
    bag.setHashAlgorithm("md5");
    
    Map<String, String> tagManifest = new HashMap<>();
    tagManifest.put("41b89090f32a9ef33226b48f1b98dddf", "bagit.txt");
    tagManifest.put("ba8644f8c8b7adb3d5cf3ad4245606e8", "manifest-md5.txt");
    tagManifest.put("68b1dabaea8770a0e9411dc5d99341f9", "bag-info.txt");
    bag.setTagManifest(tagManifest);
    
    bag.setVersion(".bagitVersion");
    
    return bag;
  }
}
