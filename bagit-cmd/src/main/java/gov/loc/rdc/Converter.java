package gov.loc.rdc;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.loc.repository.bagit.domain.Bag;
import gov.loc.repository.bagit.domain.Version;
import gov.loc.repository.bagit.exceptions.InvalidBagMetadataException;
import gov.loc.repository.bagit.exceptions.MaliciousManifestException;
import gov.loc.repository.bagit.exceptions.UnparsableVersionException;
import gov.loc.repository.bagit.reader.BagReader;
import gov.loc.repository.bagit.writer.BagWriter;

public class Converter {
  private static final Logger logger = LoggerFactory.getLogger(Converter.class);
  
  public static void upConvert() throws IOException, UnparsableVersionException, MaliciousManifestException, InvalidBagMetadataException, NoSuchAlgorithmException{
    Path path = getPath();
    Bag bag = getCurrentBag(path);
    bag.setVersion(new Version(0, 98));
    BagWriter.write(bag, path);
  }
  
  public static void downConvert(String[] commandArgs) throws IOException, UnparsableVersionException, MaliciousManifestException, InvalidBagMetadataException, NoSuchAlgorithmException{
    if(commandArgs.length != 1){
      logger.error("Expected only 1 argument to downconvert, but got {} which are {}", commandArgs.length, commandArgs);
      System.exit(-1);
    }
    String[] parts = commandArgs[0].split("\\.");
    if(parts.length != 2){
      logger.error("Could not understand version [{}]. Is it in the form of <MAJOR>.<MINOR> ?", commandArgs[0]);
      System.exit(-1);
    }
    int major = Integer.parseInt(parts[0]);
    int minor = Integer.parseInt(parts[1]);
    Version version = new Version(major, minor);
    logger.debug("Parsed version {} to down convert to", version);
    
    Path path = getPath();
    Bag bag = getCurrentBag(path);
    bag.setVersion(version);
    BagWriter.write(bag, path);
    
    deleteFolder(bag.getRootDir().resolve(".bagit"));
  }
  
  protected static Path getPath(){
    String workingDirName = System.getProperty("user.dir");
    logger.debug("Current working directory is [{}]", workingDirName);
    
    return Paths.get(workingDirName);
  }
  
  protected static Bag getCurrentBag(Path path) throws IOException, UnparsableVersionException, MaliciousManifestException, InvalidBagMetadataException{
    BagReader reader = new BagReader();
    Bag bag = reader.read(path);
    
    return bag;
  }
  
  protected static void deleteFolder(Path folder) throws IOException{
    Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Files.delete(file);
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        Files.delete(dir);
        return FileVisitResult.CONTINUE;
      }
    });
  }
}
