package gov.loc.processor;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.loc.domain.Bag;
import gov.loc.error.InvalidBagStructureException;
import gov.loc.reader.BagReader;
import gov.loc.structure.StructureConstants;

/**
 * Handles removing files or key value pair information from a bag.
 */
public class RemoveProcessor {
  private static final Logger logger = LoggerFactory.getLogger(RemoveProcessor.class);

  public static void remove(String[] args) throws InvalidBagStructureException, IOException {
    File currentDir = new File(System.getProperty("user.dir"));
    File dotBagDir = new File(currentDir, StructureConstants.DOT_BAG_FOLDER_NAME);
    if (!dotBagDir.exists() || !dotBagDir.isDirectory()) {
      logger.error("Can not remove files, directories, or info to nonexistent bag! Please create a bag first.");
      System.exit(-1);
    }

    Bag bag = BagReader.createBag(currentDir);

    switch (args[0]) {
    case "--files":
      removeFiles(args, bag);
      break;
    case "--info":
      removeInfo(args, bag);
      break;
    default:
      logger.error("Unrecognized argument {}! Run 'bagit help remove' for more info.", args[0]);
      System.exit(-1);
    }
  }
  
  protected static void removeFiles(String[] args, Bag bag) throws IOException{
    final Path rootDir = Paths.get(bag.getRootDir().toURI());
    Set<String> fileNames = getFilenames(args, rootDir);
    
    for(Entry<String,String> entry:bag.getFileManifest().entrySet()){
      if(fileNames.contains(entry.getValue())){
        bag.getFileManifest().remove(entry.getKey());
      }
    }
  }
  
  protected static Set<String> getFilenames(String[] args, final Path rootDir) throws IOException{
    final Set<String> fileNames = new HashSet<>();
    File bagRootDir = rootDir.toFile();
    
    for(int index=1; index<args.length; index++){
      File currentFile = new File(bagRootDir, args[index]);
      if(currentFile.isDirectory()){
        Files.walkFileTree(Paths.get(currentFile.toURI()), new SimpleFileVisitor<Path>(){
          @Override
          public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
               if(!attrs.isDirectory()){
                 fileNames.add(file.relativize(rootDir).toString());
               }
               return FileVisitResult.CONTINUE;
           }
        });
      }
      else{ //assume it is file
        fileNames.add(currentFile.getName());
      }
    }
    
    return fileNames;
  } 
  
  protected static void removeInfo(String[] args, Bag bag){
    for(int index=1; index<args.length; index++){
      bag.getBagInfo().remove(args[index]);
    }
  }
}
