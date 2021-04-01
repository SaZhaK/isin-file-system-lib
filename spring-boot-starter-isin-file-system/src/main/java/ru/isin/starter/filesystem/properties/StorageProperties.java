package ru.isin.starter.filesystem.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Параметры для создания иерархии директорий и сохранения файлов.
 *
 * @author Kolomiets Alexander (01.04.2021)
 * @since 1.0.0
 */
@Getter
@ConstructorBinding
@Configuration
@ConfigurationProperties(prefix = "isin.filesystem.storage")
public class StorageProperties {
	private static final String DEFAULT_ROOT_DIRECTORY = "storage/files";
	private static final String DEFAULT_DIRECTORY_NESTING_LEVEL = "3";
	private static final String DEFAULT_DIRECTORY_NAME_LENGTH = "2";
	private static final String MIN_FILE_NAME_LENGTH = "5";

	/**
	 * Корневая директория.
	 */
	private final Path rootDirectory;

	/**
	 * Уровень вложенности директорий.
	 */
	private final int directoryNestingLevel;

	/**
	 * Длина имени одной директории.
	 */
	private final int directoryNameLength;

	/**
	 * Минимальная длина имени файла.
	 */
	private final int minFileNameLength;

	/**
	 * Констуктор.
	 */
	public StorageProperties(@DefaultValue(DEFAULT_ROOT_DIRECTORY) String rootDirectory,
							 @DefaultValue(DEFAULT_DIRECTORY_NESTING_LEVEL) int directoryNestingLevel,
							 @DefaultValue(DEFAULT_DIRECTORY_NAME_LENGTH) int directoryNameLength,
							 @DefaultValue(MIN_FILE_NAME_LENGTH) int minFileNameLength) {
		this.rootDirectory = Paths.get(rootDirectory);
		this.directoryNestingLevel = directoryNestingLevel;
		this.directoryNameLength = directoryNameLength;
		this.minFileNameLength = minFileNameLength;
	}
}