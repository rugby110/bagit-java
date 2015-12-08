package gov.loc.error;

public class IntegrityException extends Exception {
  private static final long serialVersionUID = 1L;
  public IntegrityException(String exceptionMessage){
    super(exceptionMessage);
  }
}
