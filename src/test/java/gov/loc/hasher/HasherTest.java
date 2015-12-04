package gov.loc.hasher;

import java.io.InputStream;
import java.security.MessageDigest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.loc.hash.Hasher;

public class HasherTest extends Assert {
  private InputStream testFile;
  
  @Before
  public void setup(){
    testFile = getClass().getClassLoader().getResourceAsStream("bags/v0_97/bag/manifest-md5.txt");
  }

  @Test
  public void testMD5Hash() throws Exception{
    MessageDigest messageDigest = MessageDigest.getInstance("md5");
    String expectedHash = "ba8644f8c8b7adb3d5cf3ad4245606e8";
    
    String hash = Hasher.hash(testFile, messageDigest);
    
    assertEquals(expectedHash, hash);
  }
  
  @Test
  public void testSHA1Hash() throws Exception{
    MessageDigest messageDigest = MessageDigest.getInstance("sha1");
    String expectedHash = "55da689b9b2e0180fd39d04b0768ed7f9f1fc4e4";
    
    String hash = Hasher.hash(testFile, messageDigest);
    
    assertEquals(expectedHash, hash);
  }
  
  @Test
  public void testSHA256Hash() throws Exception{
    MessageDigest messageDigest = MessageDigest.getInstance("sha-256");
    String expectedHash = "a87696d08fd806679337cd91af4b2b9b09c67ce56a8e43ffbd76259392af073f";
    
    String hash = Hasher.hash(testFile, messageDigest);
    
    assertEquals(expectedHash, hash);
  }
}
