import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
public class PrintHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String h = encoder.encode("password123");
        System.out.println(h);
    }
}
