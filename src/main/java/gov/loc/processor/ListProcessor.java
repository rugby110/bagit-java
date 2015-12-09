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
import gov.loc.error.InvalidBagStructureException;
import gov.loc.reader.BagReader;
import gov.loc.structure.StructureConstants;

/**
 * Handles listing files, key value pair information, or files not currently in the bag (i.e. missing).
 */
public class ListProcessor {
  private static final Logger logger = LoggerFactory.getLogger(ListProcessor.class);

  public static void list(String[] args) throws InvalidBagStructureException, IOException {
//    String listUsage = "Usage: bagit list [--files] [--info] [--missing]\n"
//        +              "  Alias: ls\n"
//        +              "  List files or key value pair information from the bag.\n"
//        +              "  You may also list files in the current directory(and subdirectories) that are not currently included in the bag.\n"
//        +              "  Defaults to --files\n"
//        +              "  --files - list all files that are currently included in the bag\n"
//        +              "  --info - list all the key value pair information in the bag\n"
//        +              "  --missing - list all the files that are NOT currently included in the bag";
    
    File currentDir = new File(System.getProperty("user.dir"));
    File dotBagDir = new File(currentDir, StructureConstants.DOT_BAG_FOLDER_NAME);
    if (!dotBagDir.exists() || !dotBagDir.isDirectory()) {
      logger.error("Not currently in a bagged directory! Please create a bag first.");
      System.exit(-1);
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
          logger.error("Unrecognized argument {}! Run 'bagit help list' for more info.");
          System.exit(-1);
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
      logger.info("  {}", entry.getValue());
    }
  }
  
  protected static void listMissing(Bag bag) throws IOException{
    logger.info("Missing files:");
    final Collection<String> files = bag.getFileManifest().values();
    final Path rootDir = Paths.get(bag.getRootDir().toURI());

    Files.walkFileTree(rootDir, new SimpleFileVisitor<Path>(){
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        String relativePath = file.relativize(rootDir).toString();
        if(!attrs.isDirectory() && !files.contains(relativePath)){
           logger.info("  {}", relativePath);
        }
        return FileVisitResult.CONTINUE;
       }
    });
  }
}
