package gov.loc;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import gov.loc.runner.BagitCommandLineRunner;

@SpringBootApplication
public class Main {

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(Main.class);
    app.setBannerMode(Banner.Mode.OFF);
    app.run(args);
  }
  
  @Bean
  public BagitCommandLineRunner bagitCommandLineRunner(){
    return new BagitCommandLineRunner();
  }
}
