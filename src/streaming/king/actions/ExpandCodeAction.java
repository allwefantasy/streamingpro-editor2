package streaming.king.actions;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;



/**
 * Created by allwefantasy on 1/4/2017.
 */
public class ExpandCodeAction extends AnAction {

    Logger LOG = Logger.getInstance(ExpandCodeAction.class);


    @Override
    public void actionPerformed(AnActionEvent e) {

        batch(e);
        sources(e);
        source(e);
        batchSQL(e);
        output(e);
        outputs(e);
        batchScript(e);
        batchScriptDF(e);
        udf(e);

    }

    private void block(AnActionEvent e, String keyword, String template) {
        Editor editor = e.getDataContext().getData(PlatformDataKeys.EDITOR);
        int offset = editor.getCaretModel().getOffset();
        int startOffset = offset - keyword.length();
        String source = editor.getDocument().getText(new TextRange(startOffset, offset));
        if (source.equals(keyword)) {
            WriteCommandAction.runWriteCommandAction(e.getProject(), () -> {
                editor.getDocument().deleteString(startOffset, offset);
                editor.getDocument().insertString(startOffset, template);
            });
        }
    }

    private void source(AnActionEvent e) {
        block(e, "batch.source", batch_source);
    }

    private void sources(AnActionEvent e) {
        block(e, "batch.sources", batch_sources);
    }

    private void output(AnActionEvent e) {
        block(e, "batch.output", batch_output);
    }

    private void outputs(AnActionEvent e) {
        block(e, "batch.outputs", batch_outputs);
    }

    private void batchSQL(AnActionEvent e) {
        block(e, "batch.sql", batch_sql);
    }

    private void batchScript(AnActionEvent e) {
        block(e, "batch.script", batch_script);
    }

    private void batchScriptDF(AnActionEvent e) {
        block(e, "batch.script.df", batch_script_df);
    }

    private void udf(AnActionEvent e) {
        block(e, "udf", udf);
    }

    private void batch(AnActionEvent e) {
        block(e, "batch", batch_framework);
    }


    private static String batch_framework = "{\n" +
            "  \"your-name\": {\n" +
            "    \"desc\": \"\",\n" +
            "    \"strategy\": \"spark\",\n" +
            "    \"algorithm\": [],\n" +
            "    \"ref\": [],\n" +
            "    \"compositor\": [\n" +
            "    ],\n" +
            "    \"configParams\": {\n" +
            "    }\n" +
            "  }\n" +
            "}";

    private static String batch_sources = "{\n" +
            "    \"name\": \"batch.sources\",\n" +
            "    \"params\": [\n" +
            "      {\n" +
            "        \"path\": \"-\",\n" +
            "        \"format\": \"-\",\n" +
            "        \"outputTable\": \"-\"        \n" +
            "      }      \n" +
            "    ]\n" +
            "}\n";

    private static String batch_output = "{\n" +
            "    \"format\": \"-\",\n" +
            "    \"path\": \"-\",\n" +
            "    \"inputTableName\": \"-\",\n" +
            "    \"outputFileNum\":\"-1\"\n" +
            "  }";

    private static String batch_outputs = "{\n" +
            "        \"name\": \"batch.outputs\",\n" +
            "        \"params\": [          \n" +
            "          {\n" +
            "            \"format\": \"-\",\n" +
            "            \"path\": \"-\",\n" +
            "            \"inputTableName\": \"-\",\n" +
            "            \"outputFileNum\":\"-1\"\n" +
            "          }\n" +
            "        ]\n" +
            "      }";

    private static String batch_source = "{\n" +
            "    \"path\": \"-\",\n" +
            "    \"format\": \"-\",\n" +
            "    \"outputTable\": \"-\"    \n" +
            "}";


    private static String batch_sql = "{\n" +
            "        \"name\": \"batch.sql\",\n" +
            "        \"params\": [\n" +
            "          {\n" +
            "            \"sql\": \"-\",\n" +
            "            \"outputTableName\": \"-\"\n" +
            "          }\n" +
            "        ]\n" +
            "      }";

    private static String batch_script = " {\n" +
            "        \"name\": \"batch.script\",\n" +
            "        \"params\": [\n" +
            "          {\n" +
            "            \"inputTableName\": \"-\",\n" +
            "            \"outputTableName\": \"-\",\n" +
            "            \"useDocMap\": true,\n" +
            "            \"source\": \"file\",\n" +
            "            \"ignoreOldColumns\": true\n" +
            "          },\n" +
            "          {\n" +
            "            \"file\": \"-\"\n" +
            "          }\n" +
            "        ]\n" +
            "}";

    private static String batch_script_df = "{\n" +
            "        \"name\": \"batch.script.df\",\n" +
            "        \"params\": [\n" +
            "          {\n" +
            "            \"script\": \"-\",\n" +
            "            \"source\": \"file\"\n" +
            "          }\n" +
            "        ]\n" +
            "}";


    private static String udf = "  \"udf_register\": {\n" +
            "    \"desc\": \"\",\n" +
            "    \"strategy\": \"refFunction\",\n" +
            "    \"algorithm\": [],\n" +
            "    \"ref\": [],\n" +
            "    \"compositor\": [\n" +
            "      {\n" +
            "        \"name\": \"sql.udf\",\n" +
            "        \"params\": [\n" +
            "          {\n" +
            "            \"functions\": \"-\"\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    ]\n" +
            "  }";


}