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

Для сохранения файлов требуется внедрить *FileService*. 
С помощью метода *saveFile(MultipartFile file, FileSavingSettings settings)* сохранить файл.

Пример использования:
```java
import ru.isin.starter.filesystem.service;
import org.springframework.beans.factory.annotation.Autowired;

public class FileStorage {
    
    @Autowired
    private final FileService fileService;
    
    public void save(MultipartFile file) {
        FileSavingSettings settings = new FileSavingSettings("root", "MD5", 3, 2, 5);
        fileService.saveFile(file, settings);
    }
}
```


