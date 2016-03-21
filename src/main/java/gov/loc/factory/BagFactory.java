package gov.loc.factory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.loc.domain.Bag;
import gov.loc.domain.Version;
import gov.loc.hash.Hasher;

/**
 * Handy factory methods for creating {@link Bag}
 */
public class BagFactory {
  private static final Version BAG_VERSION = new Version(0, 98);
  
  /**
   *  creates a {@link Bag} from all the files using @param algorithm </br>
   *  <b>Note:</b> it does <b><i>not</i></b> create tagManifest
   * @throws IOException 
   * @throws NoSuchAlgorithmException 
   */
  public static Bag createBag(Path rootDir, List<Path> files, String algorithm) throws IOException, NoSuchAlgorithmException{
    Bag bag = new Bag();
    bag.setRootDir(rootDir);
    bag.setVersion(BAG_VERSION);
    bag.setHashAlgorithm(algorithm);
    
    MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
    Map<String, String> hashToFilenameMap = hashFiles(files, messageDigest, rootDir);
    bag.setFileManifest(hashToFilenameMap);
    
    return bag;
  }
  
  protected static Map<String,String> hashFiles(List<Path> files, MessageDigest messageDigest, Path rootDir) throws IOException{
    Map<String, String> hashToFilenameMap = new HashMap<>();
    
    for(int index=0; index<files.size(); index++){
      printPercentDone(index, files.size());
      Path file = files.get(index);
      InputStream inputStream = Files.newInputStream(file, StandardOpenOption.READ);
      String hash = Hasher.hash(inputStream, messageDigest);
      Path relative = rootDir.relativize(file);
      hashToFilenameMap.put(hash, relative.toString());
    }
    
    return hashToFilenameMap;
  }
  
  protected static void printPercentDone(int index, int total){
    int percentage = ((index+1)*100)/total;
    char[] chars = new char[percentage/5];
    Arrays.fill(chars, '#');
    String percentageString = new String(chars);
    
    System.out.printf("Processing - [%-20s]%3d%%\r", percentageString, percentage);
  }
}


