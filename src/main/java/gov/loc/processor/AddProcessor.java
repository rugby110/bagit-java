package gov.loc.processor;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.loc.domain.Bag;
import gov.loc.error.ArgumentException;
import gov.loc.error.InvalidBagStructureException;
import gov.loc.error.NonexistentBagException;
import gov.loc.hash.Hasher;
import gov.loc.reader.BagReader;
import gov.loc.structure.StructureConstants;
import gov.loc.writer.BagWriter;

/**
 * Handles adding files or key value pair information to a bag.
 */
public class AddProcessor {
  private static final Logger logger = LoggerFactory.getLogger(AddProcessor.class);

  public static void add(String[] args) throws InvalidBagStructureException, IOException, NoSuchAlgorithmException {
    if(args.length<1){
      throw new ArgumentException("The 'add' command requires at least one argument! Run 'bagit help add' for details.");
    }
    
    Path currentDir = Paths.get(System.getProperty("user.dir"));
    Path dotBagDir = currentDir.resolve(StructureConstants.DOT_BAG_FOLDER_NAME);
    if (!Files.exists(dotBagDir) || !Files.isDirectory(dotBagDir)) {
      throw new NonexistentBagException("Can not add files, directories, or info to nonexistent bag! Please create a bag first.");
    }

    Bag bag = BagReader.createBag(currentDir);

    switch (args[0]) {
    case "--files":
      addFiles(args, bag);
      break;
    case "--info":
      addInfo(args, bag);
      break;
    default:
      throw new ArgumentException("Unrecognized argument "+ args[0] +"! Run 'bagit help create' for more info.");
    }
  }

  protected static void addFiles(String[] args, Bag bag) throws IOException, NoSuchAlgorithmException {
    MessageDigest messageDigest = MessageDigest.getInstance(bag.getHashAlgorithm());
    
    for (int index = 1; index < args.length; index++) {
      Path currentFile = bag.getRootDir().resolve(args[index]);
      if (Files.exists(currentFile)) {
        if (Files.isDirectory(currentFile)) {
          addDirectoryToBag(bag, messageDigest, currentFile);
        } else {
          addFileToBag(bag, currentFile, messageDigest);
        }
      } else {
        logger.error("File {} does not exist!", currentFile);
      }
    }

    BagWriter.write(bag);
  }
  
  protected static void addDirectoryToBag(final Bag bag, final MessageDigest messageDigest, Path dir) throws IOException{
    Files.walkFileTree(dir, new SimpleFileVisitor<Path>(){
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
           if(!attrs.isDirectory()){
             addFileToBag(bag, file, messageDigest);
           }
           return FileVisitResult.CONTINUE;
       }
    });
  }

  protected static void addFileToBag(Bag bag, Path file, MessageDigest messageDigest) throws IOException {
    String hash = Hasher.hash(Files.newInputStream(file, StandardOpenOption.READ), messageDigest);
    String filePath = file.relativize(bag.getRootDir()).toString();
    bag.getFileManifest().put(hash, filePath);
  }

  protected static void addInfo(String[] args, Bag bag) throws IOException, NoSuchAlgorithmException {
    for(int index=1; index<args.length; index++){
      String[] parts = args[index].split("=");
      if(parts.length != 2){
        throw new ArgumentException("argument " + args[index] + " does not conform to the pattern <KEY>=<VALUE>!");
      }
      
      if(bag.getBagInfo().get(parts[0]) != null){
        bag.getBagInfo().get(parts[0]).add(parts[1]);
      }
      else{
        List<String> values = new ArrayList<>();
        values.add(parts[1]);
        bag.getBagInfo().put(parts[0], values);
      }
    }
    
    BagWriter.write(bag);
  }
}
