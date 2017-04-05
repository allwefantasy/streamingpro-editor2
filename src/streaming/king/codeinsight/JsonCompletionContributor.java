package streaming.king.codeinsight;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.json.psi.impl.JsonPropertyImpl;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.popup.ComponentPopupBuilder;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.EventDispatcher;
import com.intellij.util.ProcessingContext;
import com.intellij.util.containers.HashMap;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * Created by allwefantasy on 4/4/2017.
 */
public class JsonCompletionContributor extends CompletionContributor {

    public static Logger LOG = Logger.getInstance(JsonCompletionContributor.class);
    private static final PsiElementPattern.Capture<PsiElement> AFTER_FORMAT_IN_PROPERTY = psiElement()
            .afterLeaf(":").
                    withSuperParent(2, JsonProperty.class).
                    and(psiElement().withParent(JsonStringLiteral.class)).with(new PatternCondition<PsiElement>("source-format") {
                @Override
                public boolean accepts(@NotNull PsiElement psiElement, ProcessingContext context) {
                    return ((JsonPropertyImpl) psiElement.getParent().getParent()).getName().equalsIgnoreCase("format");
                }
            });

    private static final PsiElementPattern.Capture<PsiElement> AFTER_NAME_IN_PROPERTY = psiElement()
            .afterLeaf(":").
                    withSuperParent(2, JsonProperty.class).
                    and(psiElement().withParent(JsonStringLiteral.class)).with(new PatternCondition<PsiElement>("element-name") {
                @Override
                public boolean accepts(@NotNull PsiElement psiElement, ProcessingContext context) {
                    return ((JsonPropertyImpl) psiElement.getParent().getParent()).getName().equalsIgnoreCase("name");
                }
            });


    private static final PsiElementPattern.Capture<PsiElement> SQL_ELEMENT = psiElement()
            .afterLeaf(":").
                    withSuperParent(2, JsonProperty.class).
                    and(psiElement().withParent(JsonStringLiteral.class)).with(new PatternCondition<PsiElement>("sql-finder") {
                @Override
                public boolean accepts(@NotNull PsiElement psiElement, ProcessingContext context) {
                    return ((JsonPropertyImpl) psiElement.getParent().getParent()).getName().equalsIgnoreCase("sql");
                }
            });


    private static final PsiElementPattern.Capture<PsiElement> AFTER_JDBC_FORMAT_PROVIED_PROPERTY = visitNode("format", "jdbc");
    private static final PsiElementPattern.Capture<PsiElement> AFTER_CSV_FORMAT_PROVIED_PROPERTY = visitNode("format", "com.databricks.spark.csv");


    private static final PsiElementPattern.Capture<PsiElement> AFTER_BATCH_SCRIPT_NAME_PROVIED_PROPERTY = visitNode(6, "name", "batch.script");

    private static PsiElementPattern.Capture<PsiElement> visitNode(String nodeName, String nodeValue) {
        return visitNode(3, nodeName, nodeValue);

    }

    private static PsiElementPattern.Capture<PsiElement> visitNode(int level, String nodeName, String nodeValue) {
        return psiElement()
                .with(new PatternCondition<PsiElement>("format-or-name-" + nodeName) {
                    @Override
                    public boolean accepts(@NotNull PsiElement psiElement, ProcessingContext context) {
                        PsiElement targetPsiElement = psiElement;

                        for (int i = 0; i < level; i++) {
                            targetPsiElement = targetPsiElement.getParent();
                        }
                        PsiElement[] elements = targetPsiElement.getChildren();
                        String formatValue = null;
                        for (PsiElement psiElement1 : elements) {
                            if (psiElement1 instanceof JsonPropertyImpl) {
                                JsonPropertyImpl temp = ((JsonPropertyImpl) psiElement1);
                                if (temp.getName().equalsIgnoreCase(nodeName)) {
                                    formatValue = temp.getValue().getText();
                                }
                            }
                        }
                        if (formatValue == null) return false;
                        return ("\"" + nodeValue + "\"").equalsIgnoreCase(formatValue);
                    }
                });

    }


    public JsonCompletionContributor() {
        extend(CompletionType.BASIC, AFTER_FORMAT_IN_PROPERTY, new MyKeywordsCompletionProvider("formatNameList", 0));
        extend(CompletionType.BASIC, AFTER_NAME_IN_PROPERTY, new MyKeywordsCompletionProvider("nameList", 0));

        extend(CompletionType.BASIC, AFTER_JDBC_FORMAT_PROVIED_PROPERTY, new MyKeywordsCompletionProvider("formatParameters", 2));
        extend(CompletionType.BASIC, AFTER_CSV_FORMAT_PROVIED_PROPERTY, new MyKeywordsCompletionProvider("formatParameters", 3));

        extend(CompletionType.BASIC, AFTER_BATCH_SCRIPT_NAME_PROVIED_PROPERTY, new MyKeywordsCompletionProvider("nameParameters", 2));

        extend(CompletionType.BASIC, SQL_ELEMENT, new SQLCompletionProvider());
    }

    private static class SQLCompletionProvider extends CompletionProvider<CompletionParameters> {

        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {

        }
    }

    private static class MyKeywordsCompletionProvider extends CompletionProvider<CompletionParameters> {

        private String name = null;
        private int index = -1;

        private static final Map<String, String[][]> KEYWORDSMAP = new HashMap();

        public MyKeywordsCompletionProvider(String name, int index) {

            this.name = name;
            this.index = index;

            KEYWORDSMAP.put("formatNameList", new String[][]{
                    {
                            "parquet",
                            "json",
                            "jdbc",
                            "com.databricks.spark.csv",
                            "console"
                    }
            });

            KEYWORDSMAP.put("formatParameters", new String[][]{
                    {},
                    {},
                    {
                            "url",
                            "dbtable",
                            "driver",
                            "partitionColumn",
                            "lowerBound",
                            "upperBound",
                            "numPartitions", "fetchsize", "batchsize", "isolationLevel", "truncate", "createTableOptions"
                    },
                    {
                            "header"
                    },
                    {}
            });

            KEYWORDSMAP.put("nameList", new String[][]{
                    {
                            "batch.sources",
                            "batch.sql",
                            "batch.script",
                            "batch.script.df",
                            "batch.outputs",
                    }
            });

            KEYWORDSMAP.put("nameParameters", new String[][]{
                    {
                            "format", "path", "outputTable"
                    },
                    {
                            "sql", "outputTableName"
                    },
                    {
                            "inputTableName", "outputTableName", "useDocMap", "source", "ignoreOldColumns"
                    },
                    {
                            "script", "source"
                    },
                    {
                            "format", "path", "inputTableName", "outputFileNum"
                    }
            });


        }


        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters,
                                      ProcessingContext context,
                                      @NotNull CompletionResultSet result) {
            for (String keyword : KEYWORDSMAP.get(name)[index]) {
                result.addElement(LookupElementBuilder.create(keyword).bold());
            }
        }
    }
}
