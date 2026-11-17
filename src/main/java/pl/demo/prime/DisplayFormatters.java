package pl.demo.prime;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class DisplayFormatters {
    private final FormatProperties properties;

    public String currency(BigDecimal amount) {
        Locale locale = Locale.forLanguageTag(properties.getLocale());
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        formatter.setCurrency(Currency.getInstance(properties.getCurrencyCode()));
        return formatter.format(amount);
    }

    public String date(LocalDate date) {
        Locale locale = Locale.forLanguageTag(properties.getLocale());
        return DateTimeFormatter.ofPattern(properties.getDatePattern(), locale).format(date);
    }
}
