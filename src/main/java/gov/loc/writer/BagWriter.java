package gov.loc.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Map.Entry;

import gov.loc.domain.Bag;
import gov.loc.structure.StructureConstants;

/**
 * Writes {@link Bag} out to the filesystem.
 */
public class BagWriter {
  
  /**
   * returns true if it successfully wrote bag to filesystem 
   * @throws IOException 
   */
  public static boolean write(Bag bag) throws IOException{
    File dotBagDir = createDotBagDirectory(bag.getRootDir());
    
    writeBagit(dotBagDir, bag.getVersion());
    writeBagInfo(dotBagDir, bag.getBagInfo());
    writeFileManifest(dotBagDir, bag.getFileManifest(), bag.getHashAlgorithm());
    writeTagManifest(dotBagDir, bag.getTagManifest(), bag.getHashAlgorithm());
    
    return true;
  }
  
  protected static File createDotBagDirectory(File rootDir){
    File dotBagDir = new File(rootDir, StructureConstants.SPECIAL_FOLDER_NAME);
    dotBagDir.mkdir();
    
    return dotBagDir;
  }
  
  protected static void writeBagit(File dotBagDir, String version) throws IOException{
    StringBuilder line = new StringBuilder();
    line.append("BagIt-Version").append(':').append(version);
    File bagitFile = new File(dotBagDir, StructureConstants.BAGIT_FILE_NAME);
    
    Files.write(Paths.get(bagitFile.toURI()), line.toString().getBytes(StandardCharsets.UTF_8), 
        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
  }
  
  protected static void writeBagInfo(File dotBagDir, Map<String,String> bagInfo) throws IOException{
    File bagInfoFile = new File(dotBagDir, StructureConstants.BAG_INFO_FILE_NAME);
    writeMapToFile(bagInfoFile, bagInfo, ':');
  }
  
  protected static void writeFileManifest(File dotBagDir, Map<String,String> manifest, String algorithm) throws IOException{
    String manifestFileName = StructureConstants.FILE_MANIFEST_FILE_NAME_PREFIX + algorithm + StructureConstants.FILE_MANIFEST_FILE_NAME_SUFFIX;
    writeManifest(dotBagDir, manifestFileName, manifest);
  }
  
  protected static void writeTagManifest(File dotBagDir, Map<String,String> manifest, String algorithm) throws IOException{
    String manifestFileName = StructureConstants.TAG_MANIFEST_FILE_NAME_PREFIX + algorithm + StructureConstants.TAG_MANIFEST_FILE_NAME_SUFFIX;
    writeManifest(dotBagDir, manifestFileName, manifest);
  }
  
  protected static void writeManifest(File dotBagDir, String manifestFileName, Map<String,String> manifest) throws IOException{
    File manifestFile = new File(dotBagDir, manifestFileName);
    writeMapToFile(manifestFile, manifest, ' ');
  }
  
  protected static void writeMapToFile(File output, Map<String,String> map, char delimiter) throws IOException{
    //TODO delete old if exists?
    
    try(BufferedWriter writer = Files.newBufferedWriter(Paths.get(output.toURI()), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)){
      for(Entry<String,String> entry: map.entrySet()){
        StringBuilder line = new StringBuilder();
        line.append(entry.getKey()).append(delimiter).append(entry.getValue()).append(System.lineSeparator());
        writer.write(line.toString());
      }
    }
  }
}
