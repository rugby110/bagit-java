package gov.loc.domain;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Contains all information regarding the bag, such as baginfo, file and tag manifests, etc. 
 */
public class Bag {
  private Version version;
  //the root of the bag directory. i.e. the parent of .bag
  private Path rootDir;
  private String hashAlgorithm;
  private LinkedHashMap<String, List<String>> bagInfo = new LinkedHashMap<>();
  private Map<String, String> fileManifest = new HashMap<>();
  private Map<String, String> tagManifest = new HashMap<>();
  
  @Override
  public int hashCode() {
    return Objects.hash(version, rootDir, hashAlgorithm, bagInfo, fileManifest, tagManifest);
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj){return true;}
    if (obj == null){return false;}
    if (!(obj instanceof Bag)){return false;}
    Bag other = (Bag) obj;
    return Objects.equals(version, other.getVersion()) && 
        Objects.equals(rootDir, other.getRootDir()) && 
        Objects.equals(hashAlgorithm, other.getHashAlgorithm()) &&
        Objects.equals(bagInfo, other.getBagInfo()) &&
        Objects.equals(fileManifest, other.getFileManifest()) &&
        Objects.equals(tagManifest, other.getTagManifest());
  }
  
  public Version getVersion() {
    return version;
  }
  public void setVersion(Version version) {
    this.version = version;
  }
  public Path getRootDir() {
    return rootDir;
  }
  public void setRootDir(Path rootDir) {
    this.rootDir = rootDir;
  }
  public String getHashAlgorithm() {
    return hashAlgorithm;
  }
  public void setHashAlgorithm(String hashAlgorithm) {
    this.hashAlgorithm = hashAlgorithm;
  }
  public LinkedHashMap<String, List<String>> getBagInfo() {
    return bagInfo;
  }
  public void setBagInfo(Map<String, List<String>> bagInfo) {
    this.bagInfo = new LinkedHashMap<>(bagInfo);
  }
  public Map<String, String> getFileManifest() {
    return fileManifest;
  }
  public void setFileManifest(Map<String, String> fileManifest) {
    this.fileManifest = fileManifest;
  }
  public Map<String, String> getTagManifest() {
    return tagManifest;
  }
  public void setTagManifest(Map<String, String> tagManifest) {
    this.tagManifest = tagManifest;
  }

  @Override
  public String toString() {
    return "Bag [version=" + version + ", rootDir=" + rootDir + ", hashAlgorithm=" + hashAlgorithm + ", bagInfo="
        + bagInfo + ", fileManifest=" + fileManifest + ", tagManifest=" + tagManifest + "]";
  }
}
