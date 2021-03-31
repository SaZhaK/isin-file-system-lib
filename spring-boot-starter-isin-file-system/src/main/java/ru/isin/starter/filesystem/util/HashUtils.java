package ru.isin.starter.filesystem.util;

import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Утилитный класс для хэширования данных.
 *
 * @author Kolomiets Alexander (30.03.2021)
 * @since 1.0.0
 */
@Service
public class HashUtils {

	/**
	 * Метод для вычисления хэш-кода строки при помощи заданного алгоритма.
	 *
	 * @param str       строка, для которой вычисляется хэш-код
	 * @param algorithm название алгоритма, который будет использован для вычисления хэш-кода строки
	 * @return массив байт, представляющий собой хэш-код переданой строки
	 */
	public byte[] hash(String str, String algorithm) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance(algorithm);
		digest.update(str.getBytes());
		return digest.digest();
	}
}
