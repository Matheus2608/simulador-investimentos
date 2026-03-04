package br.com.caixa.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class LocalDateTimeConverterTest {

    private final LocalDateTimeConverter converter = new LocalDateTimeConverter();

    @Test
    void deveConverterParaString() {
        LocalDateTime dt = LocalDateTime.of(2026, 3, 4, 14, 30, 15);
        String resultado = converter.convertToDatabaseColumn(dt);
        assertThat(resultado).isEqualTo("2026-03-04 14:30:15");
    }

    @Test
    void deveConverterDeStringParaLocalDateTime() {
        LocalDateTime resultado = converter.convertToEntityAttribute("2026-03-04 14:30:15");
        assertThat(resultado).isEqualTo(LocalDateTime.of(2026, 3, 4, 14, 30, 15));
    }

    @Test
    void deveRetornarNullQuandoAtributoNulo() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
    }

    @Test
    void deveRetornarNullQuandoDadoBancoNulo() {
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @Test
    void deveConverterMeiaNoite() {
        LocalDateTime meiaNoite = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
        String str = converter.convertToDatabaseColumn(meiaNoite);
        assertThat(str).isEqualTo("2026-01-01 00:00:00");
        assertThat(converter.convertToEntityAttribute(str)).isEqualTo(meiaNoite);
    }
}
