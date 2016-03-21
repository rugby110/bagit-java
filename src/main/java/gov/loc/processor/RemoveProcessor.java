package gov.loc.processor;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import gov.loc.domain.Bag;
import gov.loc.error.ArgumentException;
import gov.loc.error.InvalidBagStructureException;
import gov.loc.error.NonexistentBagException;
import gov.loc.reader.BagReader;
import gov.loc.structure.StructureConstants;
import gov.loc.writer.BagWriter;

/**
 * Handles removing files or key value pair information from a bag.
 */
public class RemoveProcessor {
  public static void remove(String[] args) throws InvalidBagStructureException, IOException, NoSuchAlgorithmException {
    Path currentDir = Paths.get(System.getProperty("user.dir"));
    Path dotBagDir = currentDir.resolve(StructureConstants.DOT_BAG_FOLDER_NAME);
    if (!Files.exists(dotBagDir) || !Files.isDirectory(dotBagDir)) {
      throw new NonexistentBagException("Can not remove files, directories, or info to nonexistent bag! Please create a bag first.");
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
      throw new ArgumentException("Unrecognized argument " + args[0] + "! Run 'bagit help remove' for more info.");
    }
    
    BagWriter.write(bag);
  }
  
  protected static void removeFiles(String[] args, Bag bag) throws IOException{
    Set<String> fileNames = getFilenames(args, bag.getRootDir());
    
    //doing it this way to avoid concurrent modification exception
    Iterator<Entry<String, String>> iterator = bag.getFileManifest().entrySet().iterator();
    while(iterator.hasNext()){
      Entry<String,String> entry = iterator.next();
      if(fileNames.contains(entry.getValue())){
        iterator.remove();
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
                 fileNames.add(rootDir.relativize(file).toString());
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
