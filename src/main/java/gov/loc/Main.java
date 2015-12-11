package gov.loc;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.loc.error.IntegrityException;
import gov.loc.error.InvalidBagStructureException;
import gov.loc.processor.AddProcessor;
import gov.loc.processor.ConvertProcessor;
import gov.loc.processor.CreateProcessor;
import gov.loc.processor.HelpProcessor;
import gov.loc.processor.ListProcessor;
import gov.loc.processor.RemoveProcessor;
import gov.loc.processor.VerifyProcessor;

public class Main {
  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
    checkForCommand(args.length);

    String[] commandArgs = new String[args.length - 1];
    System.arraycopy(args, 1, commandArgs, 0, args.length - 1);

    try {
      doCommand(args[0], commandArgs);
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  protected static void doCommand(String command, String[] commandArgs)
      throws NoSuchAlgorithmException, IOException, InvalidBagStructureException, IntegrityException {
    switch (command) {
    case "add":
      AddProcessor.add(commandArgs);
      break;
    case "create":
      CreateProcessor.create(commandArgs);
      break;
    case "convert":
      ConvertProcessor.convert(commandArgs);
      break;
    case "help":
      HelpProcessor.help(commandArgs);
      break;
    case "list":
    case "ls":
      ListProcessor.list(commandArgs);
      break;
    case "remove":
    case "rm":
      RemoveProcessor.remove(commandArgs);
      break;
    case "verify":
      VerifyProcessor.verify(commandArgs);
      break;
    default:
      logger.error("Command [{}] is unrecognized!", command);
      HelpProcessor.printUsage();
      System.exit(-1);
    }
  }

  protected static void checkForCommand(int argsLength) {
    if (argsLength == 0) {
      HelpProcessor.printUsage();
      System.exit(-1);
    }
  }
}
