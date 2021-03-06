package org.apache.hupa.vaadin.actions;

import java.io.Serializable;
import java.util.List;

import org.apache.hupa.shared.data.ImapFolderImpl;
import org.apache.hupa.shared.domain.ImapFolder;
import org.apache.hupa.vaadin.hupa.HupaConnector;
import org.apache.hupa.vaadin.ui.HupaMainScreen;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.UI;


@SuppressWarnings("serial")
public class FoldersActivity implements Serializable {
    
    private HupaMainScreen display;
    private HupaConnector hupa;
    private MessageListActivity activity;
    private ImapFolder folder;
    
    public FoldersActivity(HupaConnector hupaConnector, HupaMainScreen hupaMainScreen, MessageListActivity messageListActivity) {
        hupa = hupaConnector;
        display = hupaMainScreen;
        activity = messageListActivity;
        bind();
    }

    public void goTo() {
        UI.getCurrent().setContent(display);
        reload();
    }
    
    public void reload() {
        List<ImapFolder> folders = hupa.fetchFolders();
        HierarchicalContainer container = new HierarchicalContainer();
        fillContainer(container, folders, null);
        display.getTreeFolders().setContainerDataSource(container);
        
        if (folder == null) {
            folder = new ImapFolderImpl(hupa.getUser().getSettings().getInboxFolderName());
        }
        display.getTreeFolders().select(folder);
    }
    
    private void fillContainer(HierarchicalContainer container, List<ImapFolder> list, ImapFolder current) {
        for (ImapFolder f : list) {
            container.addItem(f);
            if (current != null) {
                container.setParent(f, current);
            }
            if (f.getHasChildren() && f.getChildren().size() > 0) {
                fillContainer(container, f.getChildren(), f);
            } else {
                container.setChildrenAllowed(f, false);
            }
        }
    }
    
    private void bind() {
        display.getTreeFolders().setImmediate(true);
        display.getTreeFolders().addValueChangeListener(new ValueChangeListener() {
            public void valueChange(final ValueChangeEvent event) {
                folder = (ImapFolder)event.getProperty().getValue();
                activity.goTo(folder);
            }
        });
    }
}
