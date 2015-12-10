package gov.loc.processor;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
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
    File currentDir = new File(System.getProperty("user.dir"));
    File dotBagDir = new File(currentDir, StructureConstants.DOT_BAG_FOLDER_NAME);
    if (!dotBagDir.exists() || !dotBagDir.isDirectory()) {
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
    for(Entry<String,String> entry : bag.getBagInfo().entrySet()){
      logger.info("  {}:{}", entry.getKey(), entry.getValue());
    }
  }
  
  protected static void listMissing(Bag bag) throws IOException{
    logger.info("Missing files:");
    final Collection<String> files = bag.getFileManifest().values();
    final Path rootDir = Paths.get(bag.getRootDir().toURI());

    Files.walkFileTree(rootDir, new SimpleFileVisitor<Path>(){
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        String relativePath = rootDir.relativize(file).toString();
        if(!attrs.isDirectory() && !files.contains(relativePath) && !relativePath.startsWith(StructureConstants.DOT_BAG_FOLDER_NAME)){
           logger.info("  {}", relativePath);
        }
        return FileVisitResult.CONTINUE;
       }
    });
  }
}
