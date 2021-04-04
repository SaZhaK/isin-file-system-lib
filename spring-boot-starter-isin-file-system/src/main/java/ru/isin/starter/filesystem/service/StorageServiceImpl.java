package ru.isin.starter.filesystem.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.isin.core.utils.log.tree.annotation.Profiled;
import ru.isin.starter.filesystem.domain.FileDTO;
import ru.isin.starter.filesystem.properties.StorageProperties;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация интерфейса {@link StorageService}.
 *
 * @author Kolomiets Alexander (30.03.2021)
 * @since 1.0.0
 */
@Profiled
@Service
@AllArgsConstructor
public class StorageServiceImpl implements StorageService {
	private final HashService hashServiceImpl;
	private final StorageProperties storageProperties;

	/**
	 * Класс для сбора необходимой для сохранения инфмормации о предоставленных данных.
	 */
	@Data
	@Builder
	private static class FileInfo {
		private String name;
		private String contentType;
		private Long size;
		private byte[] content;
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public FileDTO save(InputStream data, String fileName, String contentType) throws IOException {
		byte[] content = data.readAllBytes();
		FileInfo fileInfo = FileInfo.builder().
				name(fileName).
				contentType(contentType).
				size((long) content.length).
				content(content).
				build();
		return commitSave(fileInfo);
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public FileDTO save(File file) throws IOException {
		FileInfo fileInfo = FileInfo.builder().
				name(file.getName()).
				contentType(Files.probeContentType(file.toPath())).
				size(Files.size(file.toPath())).
				content(Files.readAllBytes(file.toPath())).
				build();
		return commitSave(fileInfo);
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public FileDTO save(Path file) throws IOException {
		FileInfo fileInfo = FileInfo.builder().
				name(file.getFileName().toString()).
				contentType(Files.probeContentType(file)).
				size(Files.size(file)).
				content(Files.readAllBytes(file)).
				build();
		return commitSave(fileInfo);
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public FileDTO save(MultipartFile file) throws IOException {
		FileInfo fileInfo = FileInfo.builder().
				name(file.getOriginalFilename()).
				contentType(file.getContentType()).
				size(file.getSize()).
				content(file.getBytes()).
				build();
		return commitSave(fileInfo);
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public byte[] read(Path path) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(getActualPath(path).toFile());
		return fileInputStream.readAllBytes();
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public FileDTO update(Path path, Path file) throws IOException {
		Path actualSystemPath = getActualPath(path);
		checkBeforeUpdate(actualSystemPath);
		Files.delete(actualSystemPath);

		FileInfo fileInfo = FileInfo.builder().
				name(path.toString()).
				contentType(Files.probeContentType(file)).
				size(Files.size(file)).
				content(Files.readAllBytes(file)).
				build();
		return commitSave(fileInfo);
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public FileDTO update(Path path, File file) throws IOException {
		Path actualSystemPath = getActualPath(path);
		checkBeforeUpdate(actualSystemPath);
		Files.delete(actualSystemPath);

		FileInfo fileInfo = FileInfo.builder().
				name(path.toString()).
				contentType(Files.probeContentType(file.toPath())).
				size(Files.size(file.toPath())).
				content(Files.readAllBytes(file.toPath())).
				build();
		return commitSave(fileInfo);
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public FileDTO update(Path path, MultipartFile file) throws IOException {
		Path actualSystemPath = getActualPath(path);
		checkBeforeUpdate(actualSystemPath);
		Files.delete(actualSystemPath);

		FileInfo fileInfo = FileInfo.builder().
				name(path.toString()).
				contentType(file.getContentType()).
				size(file.getSize()).
				content(file.getBytes()).
				build();
		return commitSave(fileInfo);
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public void delete(Path path) throws IOException {
		Path actualSystemPath = getActualPath(path);
		Files.delete(actualSystemPath);
		clearSubtree(actualSystemPath.getParent());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Path getActualPath(Path path) {
		String hash = countHash(path.getFileName().toString());
		return storageProperties.getRootDirectory().
				resolve(getDirectoriesPath(hash)).
				resolve(hash.substring(countDirectoriesPartLength()));
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public void clear() throws IOException {
		List<Path> paths = Files.walk(storageProperties.getRootDirectory()).
				filter(Files::isDirectory).
				filter(path -> path.toFile().listFiles() != null && path.toFile().listFiles().length == 0).
				collect(Collectors.toList());

		for (Path path : paths) {
			clearSubtree(path);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearSubtree(Path path) throws IOException {
		while (Files.exists(path) &&
				path.toFile().listFiles() != null &&
				path.toFile().listFiles().length == 0 &&
				!path.equals(storageProperties.getRootDirectory())) {
			Files.delete(path);
			clearSubtree(path.getParent());
		}
	}

	/**
	 * Сохранение файла.
	 */
	private FileDTO commitSave(FileInfo fileInfo) throws IOException {
		createDirectories(getDirectoriesPath(countHash(fileInfo.getName())));

		Path actualSystemPath = getActualPath(Paths.get(fileInfo.getName()));

		if (Files.notExists(actualSystemPath)) {
			Files.createFile(actualSystemPath);
			try (FileOutputStream fileOutputStream = new FileOutputStream(actualSystemPath.toFile())) {
				fileOutputStream.write(fileInfo.getContent());
			}
		}

		return FileDTO.builder().
				name(fileInfo.getName()).
				path(actualSystemPath).
				contentType(fileInfo.getContentType()).
				size(fileInfo.getSize()).
				build();
	}

	/**
	 * Проверка того, что обновляемый файл принадлежит данному хранилищу и не является директорией.
	 */
	private void checkBeforeUpdate(Path path) {
		if (!path.startsWith(storageProperties.getRootDirectory())) {
			throw new RuntimeException("You can not update files out of storage");
		}
		if (Files.isDirectory(path)) {
			throw new RuntimeException("You can not update directory");
		}
	}

	/**
	 * Вычисление длины имени сохраняемого файла, отводимой для иерархии директорий.
	 */
	private int countDirectoriesPartLength() {
		return storageProperties.getDirectoryNestingLevel() * storageProperties.getDirectoryNameLength();
	}

	/**
	 * Безопасное вычисление хэш-кода.
	 */
	private String countHash(String fileName) {
		try {
			return Base64.getEncoder().encodeToString(hashServiceImpl.hash(fileName));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Unable to calculate hash: " + e.getMessage());
		}
	}

	/**
	 * Вычисление иерархии директорий.
	 */
	private Path getDirectoriesPath(String hash) {
		String directories;
		int directoriesPartLength = countDirectoriesPartLength();
		if (directoriesPartLength >= hash.length()) {
			directories = hash.substring(0, hash.length() - storageProperties.getMinFileNameLength());
		} else {
			directories = hash.substring(0, directoriesPartLength);
		}
		return createPath(directories);
	}

	/**
	 * Метод для создания пути из строки.
	 * Данный метод не создаёт фактические директории внутри файловой системы, а лишь модифицирует переданную строку.
	 * Строка будет разделена системным символом разделения директорий на блоки указанной длины
	 */
	private Path createPath(String source) {
		StringBuilder resultPath = new StringBuilder(source);
		int directoryNameLength = storageProperties.getDirectoryNameLength();
		for (int i = directoryNameLength; i < resultPath.length(); i += directoryNameLength + 1) {
			resultPath.insert(i, File.separator);
		}
		return Paths.get(resultPath.toString());
	}

	/**
	 * Метод для созданий директорий, иерархия которых будет удовлетворять переданному пути
	 */
	private void createDirectories(Path path) throws IOException {
		Path rootDirectory = storageProperties.getRootDirectory();
		if (Files.notExists(rootDirectory)) {
			Files.createDirectory(rootDirectory);
		}
		Path curPath = rootDirectory;
		for (Path curDirectory : path) {
			curPath = curPath.resolve(curDirectory);
			if (Files.notExists(curPath)) {
				Files.createDirectory(curPath);
			}
		}
	}
}
