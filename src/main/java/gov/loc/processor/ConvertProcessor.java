package gov.loc.processor;

import java.io.File;
import java.io.IOException;
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
import java.util.List;
import java.util.Map;

import gov.loc.domain.Bag;
import gov.loc.error.InvalidBagStructureException;
import gov.loc.error.UnsupportedConvertionException;
import gov.loc.factory.BagFactory;
import gov.loc.reader.BagReader;
import gov.loc.writer.BagWriter;

/**
 * Handles converting old bagit versions to the latest version.
 */
public class ConvertProcessor extends BagReader{
  
  public static void convert(String[] args) throws InvalidBagStructureException, IOException, NoSuchAlgorithmException {
    File currentDir = new File(System.getProperty("user.dir"));
    String version = getVersion(currentDir);
    
    checkVersionIsAbleToBeConverted(version);

    moveFilesOutOfDataDir(currentDir);
    
    Bag bag = createNewBag(currentDir);
    
    bag.setBagInfo(readBagInfoIfAvalable(version, currentDir));
    
    BagWriter.write(bag);
  }
  
  protected static void checkVersionIsAbleToBeConverted(String version){
    if(!(version.contains("0.97") || version.contains("0.96") || version.contains("0.95") || 
        version.contains("0.94") || version.contains("0.93"))){
      throw new UnsupportedConvertionException("Version " + version + " is currently not supported for converting.");
    }
  }
  
  protected static void moveFilesOutOfDataDir(final File currentDir) throws IOException{
    File dataDir = new File(currentDir, "data");
    
    for(File file : dataDir.listFiles()){
      Path target = Paths.get(currentDir.getPath(), file.getName());
      Files.move(Paths.get(file.toURI()), target, StandardCopyOption.REPLACE_EXISTING);
    }
  }
  
  protected static Bag createNewBag(File currentDir) throws IOException, NoSuchAlgorithmException{
    Path rootDir = Paths.get(currentDir.toURI());
    final List<Path> pathsIncluded = new ArrayList<>();
    
    Files.walkFileTree(rootDir, new SimpleFileVisitor<Path>(){
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
           if(!attrs.isDirectory()){
             pathsIncluded.add(file);
           }
           return FileVisitResult.CONTINUE;
       }
    });
    
    Bag bag = BagFactory.createBag(rootDir, pathsIncluded, "sha1");
    
    return bag;
  }
  
  protected static Map<String, String> readBagInfoIfAvalable(String version, File currentDir) throws IOException{
    Map<String,String> bagInfo = new HashMap<>();
    
    if(version.contains("0.97") || version.contains("0.96")){
      File packageFile = new File(currentDir, "bag-info.txt");
      if(packageFile.exists()){
        bagInfo = readMultilineBagInfoFile(packageFile);
      }
    }else if(version.contains("0.95") || version.contains("0.94") || version.contains("0.93")){
      File packageFile = new File(currentDir, "package-info.txt");
      if(packageFile.exists()){
        bagInfo = readMultilineBagInfoFile(packageFile);
      }
    }
    
    return bagInfo;
  }
  
  protected static Map<String, String> readMultilineBagInfoFile(File bagInfoFile) throws IOException{
    Map<String, String> bagInfoMap = new HashMap<>();

    List<String> lines = Files.readAllLines(Paths.get(bagInfoFile.toURI()));
    if(lines.size() > 0){
      String[] firstLine = lines.get(0).split(":");
      String key = firstLine[0];
      String value = firstLine[1];
      
      for(int index=1; index<lines.size(); index++){
        String[] parts = lines.get(index).split(":");
        if(parts.length == 1){ //continue from the previous line
          value+=parts[0];
        } else{
          bagInfoMap.put(key, value);
          key=parts[0];
          value=parts[1];
        }
      }
    }
    
    return bagInfoMap;
  }
}
