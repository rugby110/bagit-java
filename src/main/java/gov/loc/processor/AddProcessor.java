package gov.loc.processor;

import java.io.File;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.loc.domain.Bag;
import gov.loc.error.InvalidBagStructureException;
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
    File currentDir = new File(System.getProperty("user.dir"));
    File dotBagDir = new File(currentDir, StructureConstants.DOT_BAG_FOLDER_NAME);
    if (!dotBagDir.exists() || !dotBagDir.isDirectory()) {
      logger.error("Can not add files, directories, or info to nonexistent bag! Please create a bag first.");
      System.exit(-1);
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
      logger.error("Unrecognized argument {}! Run 'bagit help create' for more info.", args[0]);
      System.exit(-1);
    }
  }

  protected static void addFiles(String[] args, Bag bag) throws IOException, NoSuchAlgorithmException {
    String hashAlgorithm = "sha1";
    if (bag.getHashAlgorithm() != null) {
      hashAlgorithm = bag.getHashAlgorithm();
    } else {
      bag.setHashAlgorithm(hashAlgorithm);
    }
    MessageDigest messageDigest = MessageDigest.getInstance(hashAlgorithm);
    
    for (int index = 1; index < args.length; index++) {
      File currentFile = new File(bag.getRootDir(), args[index]);
      if (currentFile.exists()) {
        if (currentFile.isDirectory()) {
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
  
  protected static void addDirectoryToBag(final Bag bag, final MessageDigest messageDigest, File dir) throws IOException{
    Files.walkFileTree(Paths.get(dir.toURI()), new SimpleFileVisitor<Path>(){
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
           if(!attrs.isDirectory()){
             addFileToBag(bag, file.toFile(), messageDigest);
           }
           return FileVisitResult.CONTINUE;
       }
    });
  }

  protected static void addFileToBag(Bag bag, File file, MessageDigest messageDigest) throws IOException {
    Path rootDirPath = Paths.get(bag.getRootDir().toURI());
    Path fileToAddPath = Paths.get(file.toURI());

    String hash = Hasher.hash(Files.newInputStream(fileToAddPath, StandardOpenOption.READ), messageDigest);
    String filePath = fileToAddPath.relativize(rootDirPath).toString();
    bag.getFileManifest().put(hash, filePath);
  }

  protected static void addInfo(String[] args, Bag bag) throws IOException, NoSuchAlgorithmException {
    for(int index=1; index<args.length; index++){
      String[] parts = args[index].split("=");
      if(parts.length != 2){
        logger.error("argument {} does not conform to the pattern <KEY>=<VALUE>!", args[index]);
        System.exit(-1);
      }
      
      bag.getBagInfo().put(parts[0], parts[1]);
    }
    
    BagWriter.write(bag);
  }
}
