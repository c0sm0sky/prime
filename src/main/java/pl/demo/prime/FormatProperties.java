package pl.demo.prime;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "prime.format")
@Getter
@Setter
public class FormatProperties {
    private String locale = "pl-PL";
    private String datePattern = "dd.MM.yyyy";
    private String currencyCode = "PLN";
}
