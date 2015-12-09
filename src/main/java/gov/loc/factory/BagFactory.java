package gov.loc.factory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.loc.domain.Bag;
import gov.loc.hash.Hasher;

/**
 * Handy factory methods for creating {@link Bag}
 */
public class BagFactory {
  private static final String BAG_VERSION = "1.0";
  
  /**
   *  creates a {@link Bag} from all the files using @param algorithm </br>
   *  <b>Note:</b> it does <b><i>not</i></b> create tagManifest
   * @throws IOException 
   * @throws NoSuchAlgorithmException 
   */
  public static Bag createBag(Path rootDir, List<Path> files, String algorithm) throws IOException, NoSuchAlgorithmException{
    Bag bag = new Bag();
    bag.setRootDir(rootDir.toFile());
    bag.setVersion(BAG_VERSION);
    bag.setHashAlgorithm(algorithm);
    
    MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
    Map<String, String> hashToFilenameMap = new HashMap<>();
    
    for(Path file:files){
      InputStream inputStream = Files.newInputStream(file, StandardOpenOption.READ);
      String hash = Hasher.hash(inputStream, messageDigest);
      Path relative = file.relativize(rootDir);
      hashToFilenameMap.put(hash, relative.toString());
    }
    bag.setFileManifest(hashToFilenameMap);
    
    bag.setBagInfo(new HashMap<String,String>());
    bag.setTagManifest(new HashMap<String, String>());
    
    return bag;
  }
}


