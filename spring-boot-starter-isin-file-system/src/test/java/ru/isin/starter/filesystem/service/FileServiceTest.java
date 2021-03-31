package ru.isin.starter.filesystem.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.isin.starter.filesystem.domain.FileDTO;
import ru.isin.starter.filesystem.util.FileUtils;
import ru.isin.starter.filesystem.util.HashUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тестирование работы {@link FileService}.
 *
 * @author Alexander Kolomiets (31.03.2021)
 */
@ActiveProfiles("dev")
class FileServiceTest {
	private static final String NAME = "Test.txt";
	private static final String ORIGINAL_NAME = "OriginalTest.txt";
	private static final String CONTENT_TYPE = "text";
	private static final byte[] CONTENT = "content".getBytes();

	private static final String ROOT_DIRECTORY = "src/test/files";
	private static final String HASHING_ALGORITHM = "MD5";
	private static final int DIRECTORY_NESTING_LEVEL = 3;
	private static final int DIRECTORY_NAME_LENGTH = 3;
	private static final int MIN_FILE_NAME_LENGTH = 3;

	/**
	 * Инициализация тестового экзмеляра {@link HashUtils}.
	 *
	 * @return сущность для тестов
	 */
	private HashUtils initHashUtils() {
		return new HashUtils();
	}

	/**
	 * Инициализация тестового экзмеляра {@link FileUtils}.
	 *
	 * @return сущность для тестов
	 */
	private FileUtils initFileUtils() {
		return new FileUtils();
	}

	/**
	 * Инициализация тестового экзмеляра {@link FileService}.
	 *
	 * @return сущность для тестов
	 */
	private FileService initFileService() {
		return new FileService(
				initFileUtils(),
				initHashUtils());
	}

	/**
	 * Инициализация тестового экзмеляра {@link MultipartFile}.
	 *
	 * @return сущность для тестов
	 */
	private MultipartFile initMultipartFile() {
		return new MockMultipartFile(
				NAME,
				ORIGINAL_NAME,
				CONTENT_TYPE,
				CONTENT);
	}

	/**
	 * Инициализация тестового экзмеляра {@link FileSavingSettings}.
	 *
	 * @return сущность для тестов
	 */
	private FileSavingSettings initFileSavingSettings() {
		return FileSavingSettings.builder().
				rootDirectory(ROOT_DIRECTORY).
				hashingAlgorithm(HASHING_ALGORITHM).
				directoryNestingLevel(DIRECTORY_NESTING_LEVEL).
				directoryNameLength(DIRECTORY_NAME_LENGTH).
				minFileNameLength(MIN_FILE_NAME_LENGTH).
				build();
	}

	/**
	 * Удаление созданных директорий и файлов.
	 */
	@AfterAll
	public static void clear() throws IOException {
		FileSystemUtils.deleteRecursively(Path.of(ROOT_DIRECTORY));
	}

	/**
	 * Тестирование метода {@link FileService#saveFile(MultipartFile, FileSavingSettings)}.
	 */
	@Test
	public void testSaveFile() throws IOException, NoSuchAlgorithmException {
		MultipartFile multipartFile = initMultipartFile();
		FileSavingSettings settings = initFileSavingSettings();
		HashUtils hashUtils = initHashUtils();
		FileUtils fileUtils = initFileUtils();
		FileService fileService = initFileService();

		FileDTO fileDTO = fileService.saveFile(multipartFile, settings);
		assertTrue(Files.exists(Paths.get(ROOT_DIRECTORY)));

		String hash = Base64.getEncoder().encodeToString(hashUtils.hash(ORIGINAL_NAME, HASHING_ALGORITHM));
		String directories = hash.substring(0, DIRECTORY_NESTING_LEVEL * DIRECTORY_NAME_LENGTH);
		String path = fileUtils.createPath(directories, DIRECTORY_NAME_LENGTH);

		checkDirectories(path);

		assertEquals(ORIGINAL_NAME, fileDTO.getName());
		assertEquals(ROOT_DIRECTORY + File.separator +
				path + File.separator +
				hash.substring(DIRECTORY_NESTING_LEVEL * DIRECTORY_NAME_LENGTH), fileDTO.getPath());
		assertEquals(CONTENT_TYPE, fileDTO.getContentType());
		assertEquals(CONTENT.length, fileDTO.getSize());
	}

	/**
	 * Проверка наличия директорий согласно указанной иерархии.
	 */
	private void checkDirectories(String path) {
		int startIdx = 0;
		StringBuilder folderName = new StringBuilder(ROOT_DIRECTORY);
		while (startIdx < path.length()) {
			int endIdx = path.indexOf(File.separator, startIdx + 1);
			if (endIdx == -1) {
				endIdx = path.length();
			}
			folderName.append(path, startIdx, endIdx);
			startIdx = endIdx;

			assertTrue(Files.exists(Paths.get(folderName.toString())));
		}
	}
}