package ru.isin.starter.filesystem.service;

import org.springframework.web.multipart.MultipartFile;
import ru.isin.starter.filesystem.domain.FileDTO;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * Сервис для сохранения файлов.
 * <p>
 * Имя файла хэшируется при помощи указанного алгоритма, на основе полученного хэша строится иерархия директорий,
 * представлющая собой хэш, разделённый на равные по длине (кроме последнего) блоки,
 * задающие имена вложенных директорий
 * Искомый файл сохраняется в низ полученной иерархии
 * Вся дополнительная информация о сохранённом файле возвращается в виде объекта {@link FileDTO}
 *
 * @author Kolomiets Alexander (30.03.2021)
 * @since 1.0.0
 */
public interface StorageService {

	/**
	 * Метод для сохранения произвольной информации в виде файла.
	 *
	 * @param data        поток данных для сохранения
	 * @param fileName    имя файла, в котором будет сохранена информация
	 * @param contentType тип хранимой информации
	 * @return объект с информацией о сохранённом файле
	 * @throws IOException в случае ошибки при создании файла или директории
	 */
	FileDTO save(InputStream data, String fileName, String contentType) throws IOException;

	/**
	 * Метод для сохранения файла.
	 *
	 * @param file файл для сохранения
	 * @return объект с информацией о сохранённом файле
	 * @throws IOException в случае ошибки при создании файла или директории
	 */
	FileDTO save(Path file) throws IOException;

	/**
	 * Метод для сохранения файла.
	 *
	 * @param file файл для сохранения
	 * @return объект с информацией о сохранённом файле
	 * @throws IOException в случае ошибки при создании файла или директории
	 */
	FileDTO save(File file) throws IOException;

	/**
	 * Метод для сохранения файла.
	 *
	 * @param file файл для сохранения
	 * @return объект с информацией о сохранённом файле
	 * @throws IOException в случае ошибки при создании файла или директории
	 */
	FileDTO save(MultipartFile file) throws IOException;

	/**
	 * Метод для чтения содержимого напрямую из сохранённого файла.
	 *
	 * @param path исходное имя файла
	 * @return массив байт, представляющий содержимое сохранённого файла
	 * @throws IOException в случае ошибки открытия файла
	 */
	byte[] read(Path path) throws IOException;

	/**
	 * Метод для обновления сохранённого файла.
	 *
	 * @param path исходное имя файла
	 * @param file новый файл для сохранения
	 * @return объект с информацией об изменённом файле
	 * @throws IOException в случае ошибки открытия файла
	 */
	FileDTO update(Path path, Path file) throws IOException;

	/**
	 * Метод для обновления сохранённого файла.
	 *
	 * @param path исходное имя файла
	 * @param file новый файл для сохранения
	 * @return объект с информацией об изменённом файле
	 * @throws IOException в случае ошибки открытия файла
	 */
	FileDTO update(Path path, File file) throws IOException;

	/**
	 * Метод для обновления сохранённого файла.
	 *
	 * @param path исходное имя файла
	 * @param file новый файл для сохранения
	 * @return объект с информацией об изменённом файле
	 * @throws IOException в случае ошибки открытия файла
	 */
	FileDTO update(Path path, MultipartFile file) throws IOException;

	/**
	 * Метод для удаления сохранённого файла и образовавшихся пустых директорий.
	 *
	 * @param path исходное имя файла
	 * @throws IOException в случае ошибки открытия файла
	 */
	void delete(Path path) throws IOException;

	/**
	 * Метод для полуения имени файла, под которым он сохранён в системе.
	 *
	 * @param path исходное имя файла
	 * @return имя сохранённого файла в системе
	 */
	Path getActualPath(Path path);

	/**
	 * Метод для удаления пустых директорий.
	 *
	 * @throws IOException в случае ошибки открытия директории
	 */
	void clear() throws IOException;

	/**
	 * Метод для удаления пустых директорий вверх по иерархии, начиная с выбранного места.
	 *
	 * @param path файл, с которого будет начато удаление
	 * @throws IOException в случае ошибки открытия директории
	 */
	void clearSubtree(Path path) throws IOException;
}
