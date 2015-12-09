package gov.loc.error;

public class NonexistentBagException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  public NonexistentBagException(String message){
    super(message);
  }
}
