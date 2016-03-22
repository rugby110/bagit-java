package gov.loc.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import gov.loc.domain.Bag;
import gov.loc.domain.Version;
import gov.loc.error.InvalidBagStructureException;
import gov.loc.structure.StructureConstants;

/**
 * Reads the .bag folder and creates a {@link Bag}
 */
public class BagReader {
  private static final Logger logger = LoggerFactory.getLogger(BagReader.class);
  
  /**
   * searches for the .bag folder as the child of @param directory
   * 
   * @throws InvalidBagStructureException
   *           if it can't find .bag folder
   * @throws IOException
   */
  public static Bag createBag(Path directory) throws InvalidBagStructureException, IOException {
    
    Path dotBagDir = getDotBagDirectory(directory);
    Bag bag = new Bag();

    bag.setVersion(getVersion(dotBagDir));
    bag.setHashAlgorithm(getHashAlgorithm(dotBagDir));
    bag.setBagInfo(getBagInfo(dotBagDir));
    bag.setFileManifest(getFileManifest(dotBagDir));
    bag.setTagManifest(getTagManifest(dotBagDir));
    bag.setRootDir(directory);

    return bag;
  }

  protected static Path getDotBagDirectory(Path rootDir) throws InvalidBagStructureException {
    Path dotBagDir = rootDir.resolve(StructureConstants.DOT_BAG_FOLDER_NAME);

    if (!Files.exists(dotBagDir) || !Files.isDirectory(dotBagDir)) {
      throw new InvalidBagStructureException(".bag directory does not exist or is not a directory!");
    }

    return dotBagDir;
  }

  protected static Version getVersion(Path dotBagDir) throws IOException, InvalidBagStructureException {
    Path bagitFile = dotBagDir.resolve(StructureConstants.BAGIT_FILE_NAME);
    Map<String, String> map = readKeyValueFile(bagitFile, ":");

    String version = map.get("BagIt-Version");
    if (version == null) {
      throw new InvalidBagStructureException(
          StructureConstants.BAGIT_FILE_NAME + " is invalid because it does not contain 'BagIt-Version'!");
    }
    
    String[] parts = version.split("\\.");
    if(parts.length != 2){
      throw new InvalidBagStructureException(StructureConstants.BAGIT_FILE_NAME + " is invalid because the version number is not in the format MAJOR.MINOR");
    }

    return new Version(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()));
  }

  protected static String getHashAlgorithm(Path dotBagDir) throws InvalidBagStructureException, IOException {
    String hashAlgorithm = null;

    DirectoryStream<Path> stream = Files.newDirectoryStream(dotBagDir);
    for(Path file : stream){
      String filename = file.getFileName().toString();
      if (filename.matches(StructureConstants.FILE_MANIFEST_FILE_NAME_REGEX) ||
          filename.matches(StructureConstants.TAG_MANIFEST_FILE_NAME_REGEX)) {
        int beginIndex = filename.indexOf('-') + 1;
        String foundHashAlgorithm = filename.substring(beginIndex, filename.length() - 4);
        if (hashAlgorithm == null) {
          hashAlgorithm = foundHashAlgorithm;
        } else if (!foundHashAlgorithm.equals(hashAlgorithm)) {
          throw new InvalidBagStructureException(
              "manifest and tagmanifest files do not share the same hash algorithm. This is unsupported!");
        }
      }
    }
    
    if(hashAlgorithm == null) {
      throw new InvalidBagStructureException(
          "Could not get hash algorithm from any of the manifest files because they do not exist!");
    }

    return hashAlgorithm;
  }

  protected static Map<String, List<String>> getBagInfo(Path dotBagDir) throws InvalidBagStructureException, IOException {
    Path bagInfoFile = dotBagDir.resolve(StructureConstants.BAG_INFO_YAML_FILE_NAME);
    Map<String, List<String>> bagInfoMap = new HashMap<>();

    if(Files.exists(bagInfoFile) && !Files.isDirectory(bagInfoFile)) {
      addListToMap(bagInfoFile, bagInfoMap);
    }

    return bagInfoMap;
  }
  
  protected static void addListToMap(Path bagInfoFile, Map<String, List<String>> bagInfoMap) throws IOException, InvalidBagStructureException{
    Yaml yaml = new Yaml();
    Object obj = yaml.load(Files.newInputStream(bagInfoFile, StandardOpenOption.READ));
    
    if(obj instanceof List<?>){
      List<?> list = (List<?>) obj;
      for(Object listItem : list){
        if(listItem instanceof LinkedHashMap<?, ?>){
          LinkedHashMap<?, ?> map = (LinkedHashMap<?, ?>)listItem;
          for(Entry<?, ?> entry : map.entrySet()){
            addEntryToMap(entry, bagInfoMap);
          }
        }
        else{
          throw new InvalidBagStructureException("Expected a linkedHashMap but got " + listItem.getClass() + " for item " + listItem);
        }
      }
    }
    else if(obj instanceof LinkedHashMap<?, ?> && ((LinkedHashMap<?, ?>)obj).size() == 0 || obj == null){
      //special case
      logger.debug("Empty bag-info yaml file encountered");
    }
    else{
      throw new InvalidBagStructureException(bagInfoFile + " is not a valid yaml list of key values");
    }
  }
  
  @SuppressWarnings("unchecked")
  protected static void addEntryToMap(Entry<?, ?> entry, Map<String, List<String>> bagInfoMap) throws InvalidBagStructureException{
    if(!(entry.getKey() instanceof String)){
      throw new InvalidBagStructureException("Expected a String for the key, but got " + 
    entry.getKey().getClass() + " instead for " + entry.getKey());
    }
    if(entry.getValue() instanceof String){
      List<String> values = new ArrayList<>();
      values.add((String)entry.getValue());
      bagInfoMap.put((String)entry.getKey(), values);
    }
    else if(entry.getValue() instanceof List<?>){
      bagInfoMap.put((String)entry.getValue(), (List<String>)entry.getValue());
    }
    else{
      throw new InvalidBagStructureException("Expected a String or a list of Strings for the value(s) to key " +
    entry.getKey() + " but got " + entry.getValue() + " instead");
    }
  }

  protected static Map<String, String> getFileManifest(Path dotBagDir)
      throws InvalidBagStructureException, IOException {
    return getManifest(dotBagDir, StructureConstants.FILE_MANIFEST_FILE_NAME_REGEX);
  }

  protected static Map<String, String> getTagManifest(Path dotBagDir) throws InvalidBagStructureException, IOException {
    return getManifest(dotBagDir, StructureConstants.TAG_MANIFEST_FILE_NAME_REGEX);
  }

  protected static Map<String, String> getManifest(Path dotBagDir, String regex) throws InvalidBagStructureException, IOException {
    boolean foundManifestFile = false;
    Map<String, String> fileManifestMap = new HashMap<>();

    DirectoryStream<Path> stream = Files.newDirectoryStream(dotBagDir);
    for(Path file : stream){
      if (file.getFileName().toString().matches(regex)) {
        foundManifestFile = true;
        fileManifestMap = readCSVFile(file);
      }
    }

    if (!foundManifestFile) {
      throw new InvalidBagStructureException("Could not find a manifest file matching regex pattern " + regex + " !");
    }

    return fileManifestMap;
  }
  
  protected static Map<String,String> readCSVFile(Path file) throws IOException{
    Map<String,String> map = new HashMap<>();
    
    CsvParserSettings settings = new CsvParserSettings();
    settings.getFormat().setLineSeparator("\n");
    
    CsvParser parser = new CsvParser(settings);
    InputStream stream = Files.newInputStream(file, StandardOpenOption.READ);
    parser.beginParsing(stream);
    
    String[] row;
    while ((row = parser.parseNext()) != null) {
      map.put(row[0], row[1]);
    }

    return map;
  }

  protected static Map<String, String> readKeyValueFile(Path file, String delimiter)
      throws IOException, InvalidBagStructureException {
    Map<String, String> keyValueMap = new HashMap<>();

    BufferedReader br = Files.newBufferedReader(file, StandardCharsets.UTF_8);
    String line;
    while ((line = br.readLine()) != null) {
      String[] parts = line.split(delimiter);
      if (parts.length != 2) {
        throw new InvalidBagStructureException(
            file + " contains line [" + line + "] which is not valid. Must be in the form KEY" + delimiter + "VALUE");
      }

      keyValueMap.put(parts[0], parts[1]);
    }

    return keyValueMap;
  }
}
