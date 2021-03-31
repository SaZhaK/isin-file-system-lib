package ru.isin.starter.filesystem.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.isin.core.utils.log.tree.annotation.Profiled;
import ru.isin.starter.filesystem.domain.FileDTO;
import ru.isin.starter.filesystem.util.FileUtils;
import ru.isin.starter.filesystem.util.HashUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

/**
 * Сервис для сохранения файлов.
 *
 * @author Kolomiets Alexander (30.03.2021)
 * @since 1.0.0
 */
@Profiled
@Service
@AllArgsConstructor
public class FileService {
	private final FileUtils fileUtils;
	private final HashUtils hashUtils;

	/**
	 * Метод для сохранения файла.
	 * <p>
	 * Имя файла хэшируется при помощи указанного алгоритма, на основе полученного хэша строится иерархия директорий,
	 * представлющая собой хэш, разделённый на равные по длине (кроме последнего) блоки,
	 * задающие имена вложенных директорий
	 * Искомый файл сохраняется в низ полученной иерархии
	 * Вся дополнительная информация о файле возвращается в виде объекта {@link FileDTO}
	 *
	 * @param file     файл для сохранения
	 * @param settings настройки для сохранения файла
	 * @return объект с информацией о сохранённом файле
	 * @throws NoSuchAlgorithmException если не существует выбранный алгоритм хэширования
	 * @throws IOException              в случае ошибки при создании файла или директории
	 */
	public FileDTO saveFile(MultipartFile file, FileSavingSettings settings)
			throws NoSuchAlgorithmException, IOException {
		int directoriesPartLength = settings.getDirectoryNestingLevel() * settings.getDirectoryNameLength();
		String originalFilename = file.getOriginalFilename();
		String hash = Base64.getEncoder().encodeToString(
				hashUtils.hash(Objects.requireNonNull(originalFilename), settings.getHashingAlgorithm()));
		String directories;
		if (directoriesPartLength >= hash.length()) {
			directories = hash.substring(0, hash.length() - settings.getMinFileNameLength());
		} else {
			directories = hash.substring(0, directoriesPartLength);
		}

		String path = fileUtils.createPath(directories, settings.getDirectoryNameLength());
		fileUtils.createDirectories(path, settings.getRootDirectory());

		String fullNamePath = settings.getRootDirectory() + File.separator +
				path + File.separator + hash.substring(directoriesPartLength);

		if (Files.notExists(Paths.get(fullNamePath))) {
			Files.createFile(Paths.get(fullNamePath));
		}

		return FileDTO.builder().
				name(originalFilename).
				path(fullNamePath).
				contentType(file.getContentType()).
				size(file.getSize()).
				build();
	}
}
