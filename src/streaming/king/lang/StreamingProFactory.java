package streaming.king.lang;

import com.intellij.json.JsonFileType;
import com.intellij.openapi.fileTypes.ExtensionFileNameMatcher;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Created by allwefantasy on 31/3/2017.
 */
public class StreamingProFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(JsonFileType.INSTANCE, new ExtensionFileNameMatcher("streamingpro"));
    }
}
