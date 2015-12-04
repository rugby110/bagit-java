package gov.loc.factory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import gov.loc.domain.Bag;
import gov.loc.hash.Hasher;

/**
 * Handy factory methods for creating {@link Bag}
 */
public class BagFactory {

  /**
   *  creates a {@link Bag} from all the files in the @param rootDir using @param algorithm </br>
   *  <b>Note:</b> it does <b><i>not</i></b> create tagManifest
   * @throws IOException 
   * @throws NoSuchAlgorithmException 
   */
  public Bag createBag(File rootDir, String algorithm) throws IOException, NoSuchAlgorithmException{
    Bag bag = new Bag();
    bag.setHashAlgorithm(algorithm);
    
    MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
    Map<String, String> manifestMap = computeManifestList(Paths.get(rootDir.toURI()), algorithm, messageDigest);
    bag.setFileManifest(manifestMap);
    
    return bag;
  }
  
  protected Map<String, String> computeManifestList(final Path rootDir, String algorithm, final MessageDigest messageDigest) throws IOException{
    final Map<String, String> manifestMap = new HashMap<>();
    
    Files.walkFileTree(rootDir, new SimpleFileVisitor<Path>(){
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
           if(!attrs.isDirectory()){
             InputStream inputStream = Files.newInputStream(file, StandardOpenOption.READ);
             String hash = Hasher.hash(inputStream, messageDigest);
             Path relative = file.relativize(rootDir);
             manifestMap.put(hash, relative.toString());
           }
           return FileVisitResult.CONTINUE;
       }
    });
    return manifestMap;
  }
}


