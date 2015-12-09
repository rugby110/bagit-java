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

import gov.loc.domain.Bag;
import gov.loc.error.ArgumentException;
import gov.loc.factory.BagFactory;
import gov.loc.writer.BagWriter;

/**
 * Handles creating a bag.
 */
public class CreateProcessor {
  
  public static void create(String[] args) throws IOException, NoSuchAlgorithmException{
    switch (args.length){
      case 0:
        createWithOnlyInclude(".*");
        break;
      case 2:
        handleOneArgument(args);
        break;
      default:
        throw new ArgumentException("Inproper amount of arguments to create. Run 'bagit help create' for more info.");
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
      throw new ArgumentException("Unrecognized argument " + args[0] + " for create!");
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
  
  protected static void createWithOnlyExclude(final String excludeRegex) throws IOException, NoSuchAlgorithmException{
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
    
    Bag bag = BagFactory.createBag(rootDir, pathsIncluded, "sha1");
    BagWriter.write(bag);
  }
}
