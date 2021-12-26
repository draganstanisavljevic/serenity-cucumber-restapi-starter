package lib.helpers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lib.exceptions.TestErrorException;
import org.assertj.core.api.Assertions;

import java.io.IOException;
import java.net.URL;

public class JsonHelper {

    private static final String ERROR_PREFIX_NO_FILE = "Resources folder does not contain file: ";
    private static final String ERROR_UNABLE_TO_MAP = "Unable to parse json from %s file into type %s with error %s";


    public static <T> T readItemFromJsonFile(final String fileName, final TypeReference<T> reference) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            URL filePath = JsonHelper.class.getClassLoader().getResource(fileName);
            Assertions.assertThat(filePath).as(ERROR_PREFIX_NO_FILE + fileName).isNotNull();
            return mapper.readValue(filePath, reference);
        } catch (IOException e) {
            String formattedError = String.format(
                    ERROR_UNABLE_TO_MAP, fileName, reference.getType().getTypeName(), e.getMessage());
            throw new TestErrorException(formattedError, e);
        }
    }
}
