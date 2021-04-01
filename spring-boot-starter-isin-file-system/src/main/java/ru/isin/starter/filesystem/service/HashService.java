package ru.isin.starter.filesystem.service;

import java.security.NoSuchAlgorithmException;

/**
 * Сервис для хэширования данных.
 *
 * @author Kolomiets Alexander (1.04.2021)
 * @since 1.0.0
 */
public interface HashService {

	/**
	 * Метод для вычисления хэш-кода строки при помощи заданного алгоритма.
	 *
	 * @param str строка, для которой вычисляется хэш-код
	 * @return массив байт, представляющий собой хэш-код переданой строки
	 */
	byte[] hash(String str) throws NoSuchAlgorithmException;
}
