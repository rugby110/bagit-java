package gov.loc.error;

public class UnsupportedConvertionException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  public UnsupportedConvertionException(String message){
    super(message);
  }
}
