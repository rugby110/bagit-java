package gov.loc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
  protected static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    logger.error("RAN FROM MAIN!");
  }
}
