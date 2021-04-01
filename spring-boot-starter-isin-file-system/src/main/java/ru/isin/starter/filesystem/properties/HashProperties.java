package ru.isin.starter.filesystem.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

/**
 * Параметры для вычисления хэш-кода.
 *
 * @author Kolomiets Alexander (01.04.2021)
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
@ConstructorBinding
@ConfigurationProperties(prefix = "isin.filesystem.hash")
public class HashProperties {
	private static final String DEFAULT_HASHING_ALGORITHM = "MD5";

	/**
	 * Алгоритм хэширования.
	 */
	private final String hashingAlgorithm;
}
