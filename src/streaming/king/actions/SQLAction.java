package streaming.king.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import jsyntaxpane.syntaxkits.SqlSyntaxKit;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Created by allwefantasy on 5/4/2017.
 */
public class SQLAction extends AnAction {
    public SQLAction(@Nullable String text) {
        super(text);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        showSQL(e);
    }

    private void showSQL(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);

        if (psiFile == null || editor == null) {
            return;
        }

        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);

        jsyntaxpane.DefaultSyntaxKit.initKit();
        final JEditorPane codeEditor = new JEditorPane();
        codeEditor.setEditorKit(new SqlSyntaxKit());
        String sqlWithQuote = element.getText();
        if (sqlWithQuote != null && !sqlWithQuote.isEmpty()) {
            String temp = sqlWithQuote.substring(1, sqlWithQuote.length() - 1);
            codeEditor.setText(StringUtil.unescapeChar(StringUtil.replace(temp, "\\n", "\n"), '"'));
        }

        JBPopup popup = JBPopupFactory.getInstance().createComponentPopupBuilder(codeEditor, codeEditor).createPopup();
        popup.showInBestPositionFor(e.getData(PlatformDataKeys.EDITOR));
        popup.setMinimumSize(new Dimension(400, 200));
        popup.addListener(new JBPopupListener() {
            @Override
            public void beforeShown(LightweightWindowEvent event) {

            }

            @Override
            public void onClosed(LightweightWindowEvent event) {
                int elementOffset = element.getTextOffset();
                WriteCommandAction.runWriteCommandAction(e.getProject(), () -> {

                    editor.getDocument().deleteString(elementOffset + 1, elementOffset + sqlWithQuote.length() - 1);
                    editor.getDocument().insertString(elementOffset + 1, StringUtil.escapeQuotes(StringUtil.escapeLineBreak(codeEditor.getText())));
                });
            }
        });
    }
}
