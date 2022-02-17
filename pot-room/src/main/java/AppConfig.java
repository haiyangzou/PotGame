import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("org.pot")
@PropertySource(value = {"classpath:application.properties"}, encoding = "UTF-8")
@ConfigurationPropertiesScan
public class AppConfig {

}
