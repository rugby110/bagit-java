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

import gov.loc.domain.Bag;
import gov.loc.error.ArgumentException;
import gov.loc.error.IntegrityException;
import gov.loc.error.InvalidBagStructureException;
import gov.loc.error.NonexistentBagException;
import gov.loc.hash.Hasher;
import gov.loc.reader.BagReader;
import gov.loc.structure.StructureConstants;

/**
 * Verifies that files in a bag have not changed.
 */
public class VerifyProcessor {
  public static void verify(String[] args) throws InvalidBagStructureException, IOException, NoSuchAlgorithmException, IntegrityException{
    File currentDir = new File(System.getProperty("user.dir"));
    File dotBagDir = new File(currentDir, StructureConstants.DOT_BAG_FOLDER_NAME);
    if (!dotBagDir.exists() || !dotBagDir.isDirectory()) {
      throw new NonexistentBagException("Not currently in a bagged directory! Please create a bag first.");
    }
    
    Bag bag = BagReader.createBag(currentDir);
    
    if(args.length > 1){
      throw new ArgumentException("Only one argument is allowed for verify. Run 'bagit help verify' for more info.");
    }
    if(args.length == 0){
      verifyAll(bag);
    }
    else{
      switch(args[0]){
        case "--all":
          verifyAll(bag);
          break;
        case "--files":
          verifyFiles(bag);
          break;
        case "--tags":
          verifyTags(bag);
          break;
        default:
          throw new ArgumentException("Unrecognized argument " + args[0] +"! Run 'bagit help verify' for more info.");
      }
    }
  }
  
  protected static void verifyAll(Bag bag) throws InvalidBagStructureException, IOException, NoSuchAlgorithmException, IntegrityException{
    verifyFiles(bag);
    verifyTags(bag);
  }
  
  protected static void verifyFiles(Bag bag) throws InvalidBagStructureException, IOException, NoSuchAlgorithmException, IntegrityException{
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
  
  protected static void verifyTags(Bag bag) throws InvalidBagStructureException, IOException, NoSuchAlgorithmException, IntegrityException{
    File dotBagDir = new File(bag.getRootDir(), StructureConstants.DOT_BAG_FOLDER_NAME);
    
    for(Entry<String, String> entry : bag.getTagManifest().entrySet()){
      File fileToCheck = new File(dotBagDir, entry.getValue());
      InputStream stream = Files.newInputStream(Paths.get(fileToCheck.toURI()), StandardOpenOption.READ);
      MessageDigest messageDigest = MessageDigest.getInstance(bag.getHashAlgorithm());
      
      String hash = Hasher.hash(stream, messageDigest);
      
      if(!entry.getKey().equals(hash)){
        throw new IntegrityException("File " + fileToCheck + "'s hash [" + entry.getKey() + "] does not match computed hash [" + hash + "]");
      }
    }
  }
}
