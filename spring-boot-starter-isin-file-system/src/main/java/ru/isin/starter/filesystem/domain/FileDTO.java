package ru.isin.starter.filesystem.domain;

import lombok.*;

/**
 * ДТО для представления данных о сохранённом файле.
 *
 * @author Kolomiets Alexander (30.03.2021)
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileDTO {
	/**
	 * Имя файла.
	 */
	@NonNull
	private String name;

	/**
	 * Путь к файлу в файловой системе.
	 */
	@NonNull
	private String path;

	/**
	 * Тип файла.
	 */
	@NonNull
	private String contentType;

	/**
	 * Размер файла.
	 */
	@NonNull
	private Long size;
}
