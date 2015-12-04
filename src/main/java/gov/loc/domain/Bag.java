package gov.loc.domain;

import java.io.File;
import java.util.Map;
import java.util.Objects;

/**
 * Contains all information regarding the bag, such as baginfo, file and tag manifests, etc. 
 */
public class Bag {
  private String version;
  //the root of the bag directory. i.e. the parent of .bag
  private File rootDir;
  private String hashAlgorithm;
  private Map<String, String> bagInfo;
  private Map<String, String> fileManifest;
  private Map<String, String> tagManifest;
  
  @Override
  public int hashCode() {
    return Objects.hash(version, rootDir, hashAlgorithm, bagInfo, fileManifest, tagManifest);
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof Bag))
      return false;
    Bag other = (Bag) obj;
    return Objects.equals(version, other.getVersion()) && 
        Objects.equals(rootDir, other.getRootDir()) && 
        Objects.equals(hashAlgorithm, other.getHashAlgorithm()) &&
        Objects.equals(bagInfo, other.getBagInfo()) &&
        Objects.equals(fileManifest, other.getFileManifest()) &&
        Objects.equals(tagManifest, other.getTagManifest());
  }
  
  public String getVersion() {
    return version;
  }
  public void setVersion(String version) {
    this.version = version;
  }
  public File getRootDir() {
    return rootDir;
  }
  public void setRootDir(File rootDir) {
    this.rootDir = rootDir;
  }
  public String getHashAlgorithm() {
    return hashAlgorithm;
  }
  public void setHashAlgorithm(String hashAlgorithm) {
    this.hashAlgorithm = hashAlgorithm;
  }
  public Map<String, String> getBagInfo() {
    return bagInfo;
  }
  public void setBagInfo(Map<String, String> bagInfo) {
    this.bagInfo = bagInfo;
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
}
