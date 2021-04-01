package ru.isin.starter.filesystem.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.isin.starter.filesystem.properties.HashProperties;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Сервис для хэширования данных.
 *
 * @author Kolomiets Alexander (30.03.2021)
 * @since 1.0.0
 */
@Service
@AllArgsConstructor
public class HashServiceImpl implements HashService {
	private final HashProperties hashProperties;

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public byte[] hash(String str) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance(hashProperties.getHashingAlgorithm());
		digest.update(str.getBytes());
		return digest.digest();
	}
}
