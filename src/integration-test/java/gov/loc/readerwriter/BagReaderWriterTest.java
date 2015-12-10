package gov.loc.readerwriter;

import java.io.File;
import java.net.URL;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import gov.loc.domain.Bag;
import gov.loc.reader.BagReader;
import gov.loc.writer.BagWriter;

public class BagReaderWriterTest extends Assert {
  @Rule
  public TemporaryFolder folder= new TemporaryFolder();
  
  @Test
  public void testReaderWriter() throws Exception{
    URL url = this.getClass().getClassLoader().getResource("bags/v1_0/complete");
    Bag bag = BagReader.createBag(new File(url.toURI()));
    bag.setRootDir(folder.getRoot());
    BagWriter.write(bag);
    Bag createdBag = BagReader.createBag(folder.getRoot());
    assertEquals(bag, createdBag);
  }
}
