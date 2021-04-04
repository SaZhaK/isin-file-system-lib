# ISIN-file-system-lib.

## Библиотека для сохранения файлов в файловой системе 

### Актуальные версии

- **В maven репозитории:**
- **В github репозитории: 1.0.0**

### Стек

#### Java

- Spring Boot
- Isin core lib
- Lombok

### Работа с библиотекой

Для сохранения файлов требуется внедрить *StorageService*. <br>
*StorageService* поддерживает CRUD операции над файлами.

После подключения библиотеки можно указать пользовательские параметры сохранения
в файле application.yml. <br>
По умолчанию используются зачения:
<br> isin.filesystem.hash.hashingAlgorithm = MD5
<br> isin.filesystem.storage.rootDirectory = storage/files
<br> isin.filesystem.storage.directoryNestingLevel = 3
<br> isin.filesystem.storage.directoryNameLength = 2
<br> isin.filesystem.storage.minFileNameLength = 5

Пример использования:
```java
import ru.isin.starter.filesystem.service;
import org.springframework.beans.factory.annotation.Autowired;

public class FileStorage {
    
    @Autowired
    private final StorageService storageService;
    
    // Сохранение файла
    public void save(MultipartFile file) {
        FileDTO fileDTO = storageService.save(file);
    }

    // Чтение содержимого файла
    public void read(Path file) {
        byte[] content = storageService.read(file);
    }

    // Обновление содержимого файла
    public void update(Path path, MultipartFile file) {
        FileDTO fileDTO = storageService.update(path, file);
    }
    
    // Удаление файла
    public void delete(Path path) {
    	storageService.delete(path);
    }
}
```


