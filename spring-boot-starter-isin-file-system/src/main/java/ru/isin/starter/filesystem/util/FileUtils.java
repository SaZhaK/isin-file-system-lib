package ru.isin.starter.filesystem.util;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Утилитный класс для работы с файлами.
 *
 * @author Kolomiets Alexander (31.03.2021)
 * @since 1.0.0
 */
@Service
public class FileUtils {

	/**
	 * Метод для создания пути из строки.
	 * Данный метод не создаёт фактические директории внутри файловой системы, а лишь модифицирует переданную строку.
	 * Строка будет разделена системным символом разделения директорий на блоки указанной длины
	 *
	 * @param source              строка, на основе которой будет создан путь
	 * @param directoryNameLength длина имени директории
	 * @return строка, представляющая собой путь в файловой системе
	 */
	public String createPath(String source, int directoryNameLength) {
		StringBuilder resultPath = new StringBuilder();
		int startIdx = 0;
		while (startIdx < source.length()) {
			int endIdx = Math.min(startIdx + directoryNameLength, source.length());
			String folderName = source.substring(startIdx, endIdx);
			startIdx += directoryNameLength;

			resultPath.append(File.separator).append(folderName);
		}
		return resultPath.toString();
	}

	/**
	 * Метод для созданий директорий, иерархия которых будет удовлетворять переданному пути
	 *
	 * @param path строка, представялющая собой иерархию директорий
	 * @throws IOException в случае возникновения проблем при создании директории
	 */
	public void createDirectories(String path, String rootDirectory) throws IOException {
		if (Files.notExists(Paths.get(rootDirectory))) {
			Files.createDirectory(Paths.get(rootDirectory));
		}
		int startIdx = 0;
		StringBuilder folderName = new StringBuilder(rootDirectory);
		while (startIdx < path.length()) {
			int endIdx = path.indexOf(File.separator, startIdx + 1);
			if (endIdx == -1) {
				endIdx = path.length();
			}
			folderName.append(path, startIdx, endIdx);
			startIdx = endIdx;
			if (Files.notExists(Paths.get(folderName.toString()))) {
				Files.createDirectory(Paths.get(folderName.toString()));
			}
		}
	}
}
