# ISIN-file-system-lib.

## Библиотека для сохранения файлов в файловой системе 

### Актуальные версии

- **В maven репозитории:**
- **В github репозитории:**

### Стек

#### Java

- Spring Boot
- Isin core lib
- Lombok

### Работа с библиотекой

Для сохранения файлов требуется внедрить *FileService*. <br>
Методы *save(MultipartFile file, FileSavingSettings settings)*, 
*save(Path file)* и *save(File file)* позволяют сохранить файл. <br>
Метод *save(InputStream data, String fileName, String contentType)* позволяет
сохранить произвольный набор байт в качестве нового файла

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
    
    public void save(MultipartFile file) {
        storageService.saveFile(file);
    }

    public void save(File file) {
        storageService.saveFile(file);
    }
    
    public void save(Path file) {
        storageService.saveFile(file);
    }
    
    public void save(InputStream data, String fileName, String contentType) {
            storageService.saveFile(data, fileName, contentType);
        }
}
```


