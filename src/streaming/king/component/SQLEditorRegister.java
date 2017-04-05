package streaming.king.component;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.openapi.actionSystem.Constraints;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;
import streaming.king.actions.SQLAction;

/**
 * Created by allwefantasy on 5/4/2017.
 */
public class SQLEditorRegister implements ApplicationComponent {
    @Override
    public void initComponent() {
        ActionManager am = ActionManager.getInstance();
        SQLAction sqlAction = new SQLAction("sql editor");
        am.registerAction("sql-editor", sqlAction);
        DefaultActionGroup windowM = (DefaultActionGroup) am.getAction("EditorPopupMenu");
        windowM.add(sqlAction, Constraints.FIRST);
        windowM.addSeparator();
    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "sql-editor";
    }
}
