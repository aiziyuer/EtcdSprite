package com.aiziyuer.app.ui.ssh;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;


/**
 *
 */
@Log4j2
public class SshInfoTableCellModifier implements ICellModifier {

    @Getter
    private TableViewer tv;

    public SshInfoTableCellModifier(TableViewer tv) {
        super();
        this.tv = tv;
    }

    @Override
    public boolean canModify(Object element, String property) {
        return true;
    }

    @Override
    public Object getValue(Object element, String property) {

        Object ret = null;

        try {
            ret = FieldUtils.readDeclaredField(element, property, true);
        } catch (IllegalAccessException e) {
            log.error(e);
        }

        return ret;
    }

    @Override
    public void modify(Object element, String property, Object value) {

        try {
            FieldUtils.writeDeclaredField(element, property, value, true);
        } catch (IllegalAccessException e) {
            log.error(e);
        }

    }
}
