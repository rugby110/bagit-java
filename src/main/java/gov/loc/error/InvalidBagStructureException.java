package gov.loc.error;

public class InvalidBagStructureException extends Exception {
  private static final long serialVersionUID = 1L;
  public InvalidBagStructureException(String exceptionMessage){
    super(exceptionMessage);
  }
}
