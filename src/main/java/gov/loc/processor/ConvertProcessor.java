package gov.loc.processor;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import gov.loc.domain.Bag;
import gov.loc.domain.Version;
import gov.loc.error.InvalidBagStructureException;
import gov.loc.error.UnsupportedConvertionException;
import gov.loc.factory.BagFactory;
import gov.loc.reader.BagReader;
import gov.loc.structure.StructureConstants;
import gov.loc.writer.BagWriter;

/**
 * Handles converting old bagit versions to the latest version.
 */
public class ConvertProcessor extends BagReader{
  private static Version VERSION_93 = new Version(0, 93);
  private static Version VERSION_94 = new Version(0, 94);
  private static Version VERSION_95 = new Version(0, 95);
  private static Version VERSION_96 = new Version(0, 96);
  private static Version VERSION_97 = new Version(0, 97);
  
  public static void convert() throws InvalidBagStructureException, IOException, NoSuchAlgorithmException {
    Path currentDir = Paths.get(System.getProperty("user.dir"));
    Version version = getVersion(currentDir);
    
    checkVersionIsAbleToBeConverted(version);

    moveFilesOutOfDataDir(currentDir);
    
    Bag bag = createNewBag(currentDir);
    
    bag.setBagInfo(readBagInfoIfAvalable(version, currentDir));
    
    BagWriter.write(bag);
  }
  
  protected static void checkVersionIsAbleToBeConverted(Version version){
    if(VERSION_97.compareTo(version) < 0){
      throw new UnsupportedConvertionException("Version " + version.toString() + " is currently not supported for converting.");
    }
  }
  
  protected static void moveFilesOutOfDataDir(final Path currentDir) throws IOException{
    Path dataDir = currentDir.resolve("data");
    DirectoryStream<Path> stream = Files.newDirectoryStream(dataDir);
    for(Path file : stream){
      Path target = currentDir.resolve(file.getFileName());
      Files.move(file, target, StandardCopyOption.REPLACE_EXISTING);
    }
  }
  
  protected static Bag createNewBag(Path currentDir) throws IOException, NoSuchAlgorithmException{
    final List<Path> pathsIncluded = new ArrayList<>();
    
    Files.walkFileTree(currentDir, new SimpleFileVisitor<Path>(){
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
           if(!attrs.isDirectory()){
             pathsIncluded.add(file);
           }
           return FileVisitResult.CONTINUE;
       }
    });
    
    Bag bag = BagFactory.createBag(currentDir, pathsIncluded, "sha1");
    
    return bag;
  }
  
  protected static Map<String, List<String>> readBagInfoIfAvalable(Version version, Path currentDir) throws IOException{
    Map<String,List<String>> bagInfo = new LinkedHashMap<>();
    
    if(version.equals(VERSION_97) || version.equals(VERSION_96)){
      Path packageFile = currentDir.resolve(StructureConstants.BAG_INFO_TEXT_FILE_NAME);
      if(Files.exists(packageFile)){
        bagInfo = readMultilineBagInfoFile(packageFile);
      }
    }else if(version.equals(VERSION_95) || version.equals(VERSION_94) || version.equals(VERSION_93)){
      Path packageFile = currentDir.resolve(StructureConstants.PACKAGE_INFO_TEXT_FILE_NAME);
      if(Files.exists(packageFile)){
        bagInfo = readMultilineBagInfoFile(packageFile);
      }
    }
    //TODO throw unsupported version exception
    
    return bagInfo;
  }
  
  protected static Map<String, List<String>> readMultilineBagInfoFile(Path bagInfoFile) throws IOException{
    Map<String, List<String>> bagInfoMap = new HashMap<>();

    List<String> lines = Files.readAllLines(bagInfoFile);
    if(lines.size() > 0){
      String[] firstLine = lines.get(0).split(":");
      String key = firstLine[0];
      String value = firstLine[1];
      
      for(int index=1; index<lines.size(); index++){
        String[] parts = lines.get(index).split(":");
        if(parts.length == 1){ //continue from the previous line
          value+=parts[0];
        } else{
          List<String> values = new ArrayList<>();
          values.add(value);
          bagInfoMap.put(key, values);
          key=parts[0];
          value=parts[1];
        }
      }
    }
    
    return bagInfoMap;
  }
}
