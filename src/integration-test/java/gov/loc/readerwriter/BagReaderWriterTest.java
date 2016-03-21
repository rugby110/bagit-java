package gov.loc.readerwriter;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    URL url = this.getClass().getClassLoader().getResource("bags/v0_98/complete");
    Bag bag = BagReader.createBag(Paths.get(url.toURI()));
    Path root = Paths.get(folder.getRoot().toURI());
    bag.setRootDir(root);
    BagWriter.write(bag);
    Bag createdBag = BagReader.createBag(root);
    assertEquals(bag, createdBag);
  }
}
