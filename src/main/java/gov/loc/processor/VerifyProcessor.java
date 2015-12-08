package gov.loc.processor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.loc.domain.Bag;
import gov.loc.error.IntegrityException;
import gov.loc.error.InvalidBagStructureException;
import gov.loc.hash.Hasher;
import gov.loc.reader.BagReader;

public class VerifyProcessor {
  private static final Logger logger = LoggerFactory.getLogger(VerifyProcessor.class);

  public static void verify(String[] args) throws InvalidBagStructureException, IOException, NoSuchAlgorithmException, IntegrityException{
    if(args.length > 1){
      logger.error("Only one argument is allowed for verify. Run 'bagit help verify' for more info.");
      System.exit(-1);
    }
    if(args.length == 0){
      verifyAll();
    }
    else{
      switch(args[0]){
        case "--all":
          verifyAll();
          break;
        case "--files":
          verifyFiles();
          break;
        case "--tags":
          verifyTags();
          break;
        default:
          logger.error("Unrecognized argument {}! Run 'bagit help verify' for more info.", args[0]);
      }
    }
  }
  
  protected static void verifyAll() throws InvalidBagStructureException, IOException, NoSuchAlgorithmException, IntegrityException{
    verifyFiles();
    verifyTags();
  }
  
  protected static void verifyFiles() throws InvalidBagStructureException, IOException, NoSuchAlgorithmException, IntegrityException{
    File currentDir = new File(System.getProperty("user.dir"));
    Bag bag = BagReader.createBag(currentDir);
    
    for(Entry<String, String> entry : bag.getFileManifest().entrySet()){
      File fileToCheck = new File(bag.getRootDir(), entry.getValue());
      InputStream stream = Files.newInputStream(Paths.get(fileToCheck.toURI()), StandardOpenOption.READ);
      MessageDigest messageDigest = MessageDigest.getInstance(bag.getHashAlgorithm());
      
      String hash = Hasher.hash(stream, messageDigest);
      
      if(!entry.getKey().equals(hash)){
        throw new IntegrityException("File " + fileToCheck + "'s hash [" + entry.getKey() + "] does not match computed hash [" + hash + "]");
      }
    }
  }
  
  protected static void verifyTags() throws InvalidBagStructureException, IOException, NoSuchAlgorithmException, IntegrityException{
    File currentDir = new File(System.getProperty("user.dir"));
    Bag bag = BagReader.createBag(currentDir);
    
    for(Entry<String, String> entry : bag.getTagManifest().entrySet()){
      File fileToCheck = new File(bag.getRootDir(), entry.getValue());
      InputStream stream = Files.newInputStream(Paths.get(fileToCheck.toURI()), StandardOpenOption.READ);
      MessageDigest messageDigest = MessageDigest.getInstance(bag.getHashAlgorithm());
      
      String hash = Hasher.hash(stream, messageDigest);
      
      if(!entry.getKey().equals(hash)){
        throw new IntegrityException("File " + fileToCheck + "'s hash [" + entry.getKey() + "] does not match computed hash [" + hash + "]");
      }
    }
  }
}
