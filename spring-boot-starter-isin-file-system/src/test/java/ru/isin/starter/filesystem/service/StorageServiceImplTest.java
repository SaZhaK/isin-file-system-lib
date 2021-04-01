package ru.isin.starter.filesystem.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.isin.starter.filesystem.domain.FileDTO;
import ru.isin.starter.filesystem.properties.HashProperties;
import ru.isin.starter.filesystem.properties.StorageProperties;

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
 * Тестирование работы {@link StorageServiceImpl}.
 *
 * @author Alexander Kolomiets (31.03.2021)
 */
@ActiveProfiles("dev")
class StorageServiceImplTest {
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
	 * Инициализация тестового экзмеляра {@link StorageProperties}.
	 *
	 * @return сущность для тестов
	 */
	private StorageProperties initStorageProperties() {
		return new StorageProperties(
				ROOT_DIRECTORY,
				DIRECTORY_NESTING_LEVEL,
				DIRECTORY_NAME_LENGTH,
				MIN_FILE_NAME_LENGTH);
	}

	/**
	 * Инициализация тестового экзмеляра {@link HashProperties}.
	 *
	 * @return сущность для тестов
	 */
	private HashProperties initHashingProperties() {
		return new HashProperties(HASHING_ALGORITHM);
	}

	/**
	 * Инициализация тестового экзмеляра {@link HashServiceImpl}.
	 *
	 * @return сущность для тестов
	 */
	private HashServiceImpl initHashUtils() {
		return new HashServiceImpl(initHashingProperties());
	}

	/**
	 * Инициализация тестового экзмеляра {@link StorageServiceImpl}.
	 *
	 * @return сущность для тестов
	 */
	private StorageServiceImpl initFileService() {
		return new StorageServiceImpl(
				initHashUtils(),
				initStorageProperties());
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
	 * Удаление созданных директорий и файлов.
	 */
	@AfterAll
	public static void clear() throws IOException {
		FileSystemUtils.deleteRecursively(Path.of(ROOT_DIRECTORY));
	}

	/**
	 * Тестирование метода {@link StorageServiceImpl#save(MultipartFile)}.
	 */
	@Test
	public void testSaveFile() throws IOException, NoSuchAlgorithmException {
		MultipartFile multipartFile = initMultipartFile();
		HashServiceImpl hashServiceImpl = initHashUtils();
		StorageServiceImpl storageServiceImpl = initFileService();

		FileDTO fileDTO = storageServiceImpl.save(multipartFile);
		assertTrue(Files.exists(Paths.get(ROOT_DIRECTORY)));

		String hash = Base64.getEncoder().encodeToString(hashServiceImpl.hash(ORIGINAL_NAME));
		String directories = hash.substring(0, DIRECTORY_NESTING_LEVEL * DIRECTORY_NAME_LENGTH);
		Path path = createPath(directories);

		checkDirectories(path);

		assertEquals(ORIGINAL_NAME, fileDTO.getName());
		assertEquals(Path.of(ROOT_DIRECTORY).
				resolve(path).
				resolve(hash.substring(DIRECTORY_NESTING_LEVEL * DIRECTORY_NAME_LENGTH)), fileDTO.getPath());
		assertEquals(CONTENT_TYPE, fileDTO.getContentType());
		assertEquals(CONTENT.length, fileDTO.getSize());
	}

	/**
	 * Проверка наличия директорий согласно указанной иерархии.
	 */
	private void checkDirectories(Path path) {
		Path total = Paths.get(ROOT_DIRECTORY);
		for (Path curPath : path) {
			total = total.resolve(curPath);
			assertTrue(Files.exists(total));
		}
	}

	/**
	 * Создание пути из строки.
	 */
	private Path createPath(String source) {
		StringBuilder resultPath = new StringBuilder(source);
		for (int i = DIRECTORY_NAME_LENGTH; i < resultPath.length(); i += DIRECTORY_NAME_LENGTH + 1) {
			resultPath.insert(i, File.separator);
		}
		return Paths.get(resultPath.toString());
	}
}