package gov.loc.writer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import gov.loc.domain.Bag;
import gov.loc.domain.Version;
import gov.loc.hash.Hasher;
import gov.loc.structure.StructureConstants;

/**
 * Writes {@link Bag} out to the filesystem.
 */
public class BagWriter {
  private static final Logger logger = LoggerFactory.getLogger(BagWriter.class);

  /**
   * returns true if it successfully wrote bag to file system</br>
   * Note: when writing the tagManifest file it ignores what is currently stored
   * in bag and calculates a new one after writing the other files.
   * 
   * @throws IOException
   * @throws NoSuchAlgorithmException
   */
  public static boolean write(Bag bag) throws IOException, NoSuchAlgorithmException {
    Path dotBagDir = createDotBagDirectory(bag.getRootDir());

    writeBagit(dotBagDir, bag.getVersion());
    
    Path bagInfoFile = dotBagDir.resolve(StructureConstants.BAG_INFO_YAML_FILE_NAME);
    if(bag.getBagInfo().size() > 0 || Files.exists(bagInfoFile)){
      writeBagInfo(dotBagDir, bag.getBagInfo());
    }
    
    writeFileManifest(dotBagDir, bag.getFileManifest(), bag.getHashAlgorithm());
    if(bag.getTagManifest().size() > 0){
      writeTagManifest(dotBagDir, bag.getHashAlgorithm());
    }

    return true;
  }

  protected static Path createDotBagDirectory(Path rootDir) throws IOException {
    Path dotBagDir = rootDir.resolve(StructureConstants.DOT_BAG_FOLDER_NAME);
    Files.createDirectories(dotBagDir);
    logger.debug("Was able to create {}? {}", dotBagDir, Files.exists(dotBagDir));

    return dotBagDir;
  }

  protected static void writeBagit(Path dotBagDir, Version version) throws IOException {
    StringBuilder line = new StringBuilder();
    line.append("BagIt-Version").append(':').append(version.toString());
    Path bagitFile = dotBagDir.resolve(StructureConstants.BAGIT_FILE_NAME);

    Files.write(bagitFile, line.toString().getBytes(StandardCharsets.UTF_8),
        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
  }

  protected static void writeBagInfo(Path dotBagDir, Map<String, List<String>> bagInfo) throws IOException {
    Path bagInfoFile = dotBagDir.resolve(StructureConstants.BAG_INFO_YAML_FILE_NAME);
    if (Files.exists(bagInfoFile)) {
      Files.delete(bagInfoFile);
      logger.debug("Deletion of file {} was successful? {}", bagInfoFile, Files.exists(bagInfoFile));
    }

    try (BufferedWriter writer = Files.newBufferedWriter(bagInfoFile, StandardCharsets.UTF_8,
        StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
      Yaml yaml = new Yaml();
      yaml.dump(bagInfo, writer);
    }
  }

  protected static void writeFileManifest(Path dotBagDir, Map<String, String> manifest, String algorithm)
      throws IOException {
    String manifestFileName = StructureConstants.FILE_MANIFEST_FILE_NAME_PREFIX + algorithm
        + StructureConstants.FILE_MANIFEST_FILE_NAME_SUFFIX;
    writeManifest(dotBagDir, manifestFileName, manifest);
  }

  protected static void writeTagManifest(Path dotBagDir, String algorithm)
      throws IOException, NoSuchAlgorithmException {
    Map<String, String> manifest = new HashMap<>();
    MessageDigest messageDigest = MessageDigest.getInstance(algorithm);

    String manifestFileName = StructureConstants.TAG_MANIFEST_FILE_NAME_PREFIX + algorithm
        + StructureConstants.TAG_MANIFEST_FILE_NAME_SUFFIX;

    DirectoryStream<Path> stream = Files.newDirectoryStream(dotBagDir);
    for (Path file : stream) {
      if (!file.getFileName().endsWith(manifestFileName)) {
        InputStream inputStream = Files.newInputStream(file, StandardOpenOption.READ);
        String hash = Hasher.hash(inputStream, messageDigest);
        manifest.put(hash, file.getFileName().toString());
      }
    }

    writeManifest(dotBagDir, manifestFileName, manifest);
  }

  protected static void writeManifest(Path dotBagDir, String manifestFileName, Map<String, String> manifest)
      throws IOException {
    Path manifestFile = dotBagDir.resolve(manifestFileName);
    writeMapToFile(manifestFile, manifest, ' ');
  }

  protected static void writeMapToFile(Path output, Map<String, String> map, char delimiter) throws IOException {
    if (Files.exists(output)) {
      Files.delete(output);
      logger.debug("Deletion of file {} was successful? {}", output, Files.exists(output));
    }

    try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8,
        StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
      for (Entry<String, String> entry : map.entrySet()) {
        StringBuilder line = new StringBuilder();
        line.append(entry.getKey()).append(delimiter).append(entry.getValue()).append(System.lineSeparator());
        writer.write(line.toString());
      }
    }
  }
}
