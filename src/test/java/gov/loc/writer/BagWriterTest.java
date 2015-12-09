package gov.loc.writer;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public class BagWriterTest extends Assert {
  @Rule
  public TemporaryFolder folder= new TemporaryFolder();
}
