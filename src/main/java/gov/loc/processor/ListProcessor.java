package gov.loc.processor;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.loc.domain.Bag;
import gov.loc.error.ArgumentException;
import gov.loc.error.InvalidBagStructureException;
import gov.loc.error.NonexistentBagException;
import gov.loc.reader.BagReader;
import gov.loc.structure.StructureConstants;

/**
 * Handles listing files, key value pair information, or files not currently in the bag (i.e. missing).
 */
public class ListProcessor {
  private static final Logger logger = LoggerFactory.getLogger(ListProcessor.class);

  public static void list(String[] args) throws InvalidBagStructureException, IOException {
    Path currentDir = Paths.get(System.getProperty("user.dir"));
    Path dotBagDir = currentDir.resolve(StructureConstants.DOT_BAG_FOLDER_NAME);
    if (!Files.exists(dotBagDir) || !Files.isDirectory(dotBagDir)) {
      throw new NonexistentBagException("Not currently in a bagged directory! Please create a bag first.");
    }
    
    Bag bag = BagReader.createBag(currentDir);
    
    if(args.length > 0){
      switch(args[0]){
        case "--files":
          listFiles(bag);
          break;
        case "--info":
          listInfo(bag);
          break;
        case "--missing":
          listMissing(bag);
          break;
        default:
          throw new ArgumentException("Unrecognized argument " + args[0] + "! Run 'bagit help list' for more info.");
      }
    }
    else{
      listFiles(bag);
    }
  }
  
  protected static void listFiles(Bag bag){
    logger.info("Tracked files:");
    for(Entry<String,String> entry : bag.getFileManifest().entrySet()){
      logger.info("  {}", entry.getValue());
    }
  }
  
  protected static void listInfo(Bag bag){
    logger.info("Bag information:");
    for(Entry<String,List<String>> entry : bag.getBagInfo().entrySet()){
      logger.info("  {}:{}", entry.getKey(), entry.getValue());
    }
  }
  
  protected static void listMissing(final Bag bag) throws IOException{
    logger.info("Missing files:");
    final Collection<String> files = bag.getFileManifest().values();

    Files.walkFileTree(bag.getRootDir(), new SimpleFileVisitor<Path>(){
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        String relativePath = bag.getRootDir().relativize(file).toString();
        if(!attrs.isDirectory() && !files.contains(relativePath) && !relativePath.startsWith(StructureConstants.DOT_BAG_FOLDER_NAME)){
           logger.info("  {}", relativePath);
        }
        return FileVisitResult.CONTINUE;
       }
    });
  }
}
