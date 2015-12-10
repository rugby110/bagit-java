package gov.loc.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import gov.loc.domain.Bag;
import gov.loc.error.InvalidBagStructureException;
import gov.loc.structure.StructureConstants;

/**
 * Reads the .bag folder and creates a {@link Bag}
 */
public class BagReader {

  /**
   * searches for the .bag folder as the child of @param directory
   * 
   * @throws InvalidBagStructureException
   *           if it can't find .bag folder
   * @throws IOException
   */
  public static Bag createBag(File directory) throws InvalidBagStructureException, IOException {
    File dotBagDir = getDotBagDirectory(directory);
    Bag bag = new Bag();

    bag.setVersion(getVersion(dotBagDir));
    bag.setHashAlgorithm(getHashAlgorithm(dotBagDir));
    bag.setBagInfo(getBagInfo(dotBagDir));
    bag.setFileManifest(getFileManifest(dotBagDir));
    bag.setTagManifest(getTagManifest(dotBagDir));
    bag.setRootDir(directory);

    return bag;
  }

  protected static File getDotBagDirectory(File rootDir) throws InvalidBagStructureException {
    File dotBagDir = new File(rootDir, StructureConstants.DOT_BAG_FOLDER_NAME);

    if (!dotBagDir.exists() || !dotBagDir.isDirectory()) {
      throw new InvalidBagStructureException(".bag directory does not exist or is not a directory!");
    }

    return dotBagDir;
  }

  protected static String getVersion(File dotBagDir) throws IOException, InvalidBagStructureException {
    File bagitFile = new File(dotBagDir, StructureConstants.BAGIT_FILE_NAME);
    Map<String, String> map = readKeyValueFile(bagitFile, ":");

    String version = map.get("BagIt-Version");
    if (version == null) {
      throw new InvalidBagStructureException(
          StructureConstants.BAGIT_FILE_NAME + " is invalid because it does not contain 'BagIt-Version'!");
    }

    return version;
  }

  protected static String getHashAlgorithm(File dotBagDir) throws InvalidBagStructureException {
    String hashAlgorithm = null;

    String[] filenames = dotBagDir.list();
    if (filenames != null) {
      for (String filename : filenames) {
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
    } else {
      throw new InvalidBagStructureException(
          "Could not get hash algorithm from any of the manifest files because they do not exist!");
    }

    return hashAlgorithm;
  }

  protected static Map<String, String> getBagInfo(File dotBagDir) throws InvalidBagStructureException, IOException {
    File bagInfoFile = new File(dotBagDir, StructureConstants.BAG_INFO_FILE_NAME);
    Map<String, String> bagInfoMap = new HashMap<>();

    if (bagInfoFile.exists() && !bagInfoFile.isDirectory()) {
      bagInfoMap = readKeyValueFile(bagInfoFile, ":");
    }

    return bagInfoMap;
  }

  protected static Map<String, String> getFileManifest(File dotBagDir)
      throws InvalidBagStructureException, IOException {
    return getManifest(dotBagDir, StructureConstants.FILE_MANIFEST_FILE_NAME_REGEX);
  }

  protected static Map<String, String> getTagManifest(File dotBagDir) throws InvalidBagStructureException, IOException {
    return getManifest(dotBagDir, StructureConstants.TAG_MANIFEST_FILE_NAME_REGEX);
  }

  protected static Map<String, String> getManifest(File dotBagDir, String regex)
      throws InvalidBagStructureException, IOException {
    boolean foundManifestFile = false;
    Map<String, String> fileManifestMap = new HashMap<>();

    String[] filenames = dotBagDir.list();
    if (filenames != null) {
      for (String filename : filenames) {
        if (filename.matches(regex)) {
          foundManifestFile = true;
          File manifestFile = new File(dotBagDir, filename);
          fileManifestMap = readKeyValueFile(manifestFile, " ");
        }
      }
    }

    if (!foundManifestFile) {
      throw new InvalidBagStructureException("Could not find a manifest file matching regex pattern " + regex + " !");
    }

    return fileManifestMap;
  }

  protected static Map<String, String> readKeyValueFile(File file, String delimiter)
      throws IOException, InvalidBagStructureException {
    Map<String, String> keyValueMap = new HashMap<>();

    BufferedReader br = Files.newBufferedReader(Paths.get(file.toURI()), StandardCharsets.UTF_8);
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

  /**
   * Converts old versions of bagit spec to {@link Bag}
   */
  public static Bag convertBag(File directory) throws Exception {
    // TODO
    throw new Exception("Not yet implemented!");
  }
}
