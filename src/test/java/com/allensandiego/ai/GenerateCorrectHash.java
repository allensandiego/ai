import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.context.ApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GenerateCorrectHash {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(GenerateCorrectHash.class);
        ApplicationContext context = app.run(args);
        BCryptPasswordEncoder encoder = (BCryptPasswordEncoder) context.getBean("passwordEncoder");
        System.out.println("password123 -> " + encoder.encode("password123"));
        System.exit(0);
    }
}
