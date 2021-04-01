package ru.isin.starter.filesystem.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.isin.starter.filesystem.properties.HashProperties;
import ru.isin.starter.filesystem.properties.StorageProperties;
import ru.isin.starter.filesystem.service.HashService;
import ru.isin.starter.filesystem.service.HashServiceImpl;
import ru.isin.starter.filesystem.service.StorageService;
import ru.isin.starter.filesystem.service.StorageServiceImpl;

/**
 * Конфигурация модуля Filesystem.
 *
 * @author Alexander kolomiets (01.04.2001)
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(value = {StorageProperties.class, HashProperties.class})
public class IsinFilesystemStarterConfiguration {
	private static final String VERSION = "1.0.0";

	/**
	 * Инициализация bean StorageService.
	 *
	 * @param hashService       сервис для вычисления хэш-кодов имён файлов
	 * @param storageProperties параметры сохранения
	 * @return bean
	 */
	@Bean
	@ConditionalOnClass({HashService.class, StorageProperties.class})
	public StorageService createStorageService(HashService hashService, StorageProperties storageProperties) {
		log.info("ISIN Filesystem Lib {} Initializing Bean: StorageService", VERSION);
		return new StorageServiceImpl(hashService, storageProperties);
	}

	/**
	 * Метод для создания базового отправителя.
	 *
	 * @param hashProperties параметры вычисления хэш-кодов имён файлов
	 * @return bean
	 */
	@Bean
	@ConditionalOnClass({HashProperties.class})
	public HashService createHashService(HashProperties hashProperties) {
		log.info("ISIN Filesystem Lib {} Initializing Bean: HashService", VERSION);
		return new HashServiceImpl(hashProperties);
	}
}
