package gov.loc.processor;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.loc.domain.Bag;
import gov.loc.factory.BagFactory;
import gov.loc.writer.BagWriter;

/**
 * Handles creating a bag.
 */
public class CreateProcessor {
  private static final Logger logger = LoggerFactory.getLogger(CreateProcessor.class);
  
  public static void create(String[] args) throws IOException, NoSuchAlgorithmException{
    switch (args.length){
      case 0:
        createWithOnlyInclude(".*");
        break;
      case 2:
        handleOneArgument(args);
        break;
      default:
        logger.error("Inproper amount of arguments to create. Run 'bagit help create' for more info.");
        break;
    }
  }
  
  protected static void handleOneArgument(String[] args) throws IOException, NoSuchAlgorithmException{
    if("--include".equals(args[0])){
      createWithOnlyInclude(args[1]);
    }
    else if("--exclude".equals(args[0])){
      createWithOnlyExclude(args[1]);
    }
    else{
      logger.error("Unrecognized argument {} for create!", args[0]);
      HelpProcessor.printUsage();
      System.exit(-1);
    }
  }
  
  protected static void createWithOnlyInclude(final String includeRegex) throws IOException, NoSuchAlgorithmException{
    Path rootDir = Paths.get(System.getProperty("user.dir"));
    final List<Path> pathsIncluded = new ArrayList<>();
    
    Files.walkFileTree(rootDir, new SimpleFileVisitor<Path>(){
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
           if(!attrs.isDirectory() && file.toString().matches(includeRegex)){
             pathsIncluded.add(file);
           }
           return FileVisitResult.CONTINUE;
       }
    });
    
    Bag bag = BagFactory.createBag(rootDir, pathsIncluded, "sha1");
    BagWriter.write(bag);
  }
  
  protected static void createWithOnlyExclude(final String excludeRegex) throws IOException{
    Path rootDir = Paths.get(System.getProperty("user.dir"));
    final List<Path> pathsIncluded = new ArrayList<>();
    
    Files.walkFileTree(rootDir, new SimpleFileVisitor<Path>(){
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
           if(!attrs.isDirectory() && !file.toString().matches(excludeRegex)){
             pathsIncluded.add(file);
           }
           return FileVisitResult.CONTINUE;
       }
    });
    
    //TODO create with list...
  }
}
