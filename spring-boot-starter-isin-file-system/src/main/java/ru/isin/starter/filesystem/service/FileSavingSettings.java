package ru.isin.starter.filesystem.service;

import lombok.*;

/**
 * Класс для задания настроек сохранения файлов при помощи {@link FileService}.
 *
 * @author Kolomiets Alexander (30.03.2021)
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileSavingSettings {

	/**
	 * Корневая директория.
	 */
	@NonNull
	private String rootDirectory;

	/**
	 * Алгоритм хэширования.
	 */
	@NonNull
	private String hashingAlgorithm;

	/**
	 * Уровень вложенности директорий.
	 */
	@NonNull
	private int directoryNestingLevel;

	/**
	 * Длина имени одной директории.
	 */
	@NonNull
	private int directoryNameLength;

	/**
	 * Минимальная длина имени файла.
	 */
	@NonNull
	private int minFileNameLength;
}
