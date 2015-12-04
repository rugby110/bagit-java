package gov.loc.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UsageProcessor {
  protected static final Logger logger = LoggerFactory.getLogger(UsageProcessor.class);

  public static void printUsage(){
    String usage = "Usage: bagit <COMMAND> [ARGS]\n"
        +          "       Where <COMMAND> is any of the below commands and [ARGS] are option arguments for those commands.\n"
        +          "         <create> [--include --exclude] - create a bag\n"
        +          "         <verify> [--all --files --tags] - verify the files/tags match their hash.\n"
        +          "         <add> [--files --info] - add files/info to bag.\n"
        +          "         <remove | rm> [--files --info] - remove files/info from bag.\n"
        +          "         <list | ls> [--files --info] - list files/tags in bag. Also can list files not in bag.\n"
        +          "         <help> <COMMAND> - show more details for any of the commands.";
    
    logger.info(usage);
  }
}
