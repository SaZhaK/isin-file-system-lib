package ru.isin.starter.filesystem.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.isin.core.utils.log.tree.annotation.Profiled;
import ru.isin.starter.filesystem.domain.FileDTO;
import ru.isin.starter.filesystem.properties.StorageProperties;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

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
	 * Сохранение файла.
	 */
	private FileDTO commitSave(FileInfo fileInfo) throws IOException {
		String hash;
		try {
			hash = Base64.getEncoder().encodeToString(hashServiceImpl.hash(Objects.requireNonNull(fileInfo.getName())));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Unable to calculate hash: " + e.getMessage());
		}
		String directories;
		int directoriesPartLength = storageProperties.getDirectoryNestingLevel() * storageProperties.getDirectoryNameLength();
		if (directoriesPartLength >= hash.length()) {
			directories = hash.substring(0, hash.length() - storageProperties.getMinFileNameLength());
		} else {
			directories = hash.substring(0, directoriesPartLength);
		}

		Path path = createPath(directories);
		createDirectories(path);

		Path fullNamePath = storageProperties.getRootDirectory().resolve(path).resolve(hash.substring(directoriesPartLength));

		if (Files.notExists(fullNamePath)) {
			Files.createFile(fullNamePath);
			try (FileOutputStream fileOutputStream = new FileOutputStream(fullNamePath.toFile())) {
				fileOutputStream.write(fileInfo.getContent());
			}
		}

		return FileDTO.builder().
				name(fileInfo.getName()).
				path(fullNamePath).
				contentType(fileInfo.getContentType()).
				size(fileInfo.getSize()).
				build();
	}

	/**
	 * Метод для создания пути из строки.
	 * Данный метод не создаёт фактические директории внутри файловой системы, а лишь модифицирует переданную строку.
	 * Строка будет разделена системным символом разделения директорий на блоки указанной длины
	 *
	 * @param source строка, на основе которой будет создан путь
	 * @return объект Path, представляющий собой путь в файловой системе
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
	 *
	 * @param path строка, представялющая собой иерархию директорий
	 * @throws IOException в случае возникновения проблем при создании директории
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
