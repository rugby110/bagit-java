package gov.loc.structure;

public abstract class StructureConstants {
  public static final String DOT_BAG_FOLDER_NAME = ".bag";
  public static final String BAGIT_FILE_NAME = "bagit.txt";
  public static final String BAG_INFO_FILE_NAME = "bag-info.txt";
  public static final String FILE_MANIFEST_FILE_NAME_PREFIX = "manifest-";
  public static final String FILE_MANIFEST_FILE_NAME_SUFFIX = ".txt";
  public static final String FILE_MANIFEST_FILE_NAME_REGEX = FILE_MANIFEST_FILE_NAME_PREFIX + "\\w*\\" + FILE_MANIFEST_FILE_NAME_SUFFIX;
  public static final String TAG_MANIFEST_FILE_NAME_PREFIX = "tagmanifest-";
  public static final String TAG_MANIFEST_FILE_NAME_SUFFIX = ".txt";
  public static final String TAG_MANIFEST_FILE_NAME_REGEX = TAG_MANIFEST_FILE_NAME_PREFIX + "\\w*\\" + TAG_MANIFEST_FILE_NAME_SUFFIX;
}
