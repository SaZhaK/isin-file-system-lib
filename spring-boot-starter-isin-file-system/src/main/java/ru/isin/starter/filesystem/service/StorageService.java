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
 * Вся дополнительная информация о файле возвращается в виде объекта {@link FileDTO}
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
}
