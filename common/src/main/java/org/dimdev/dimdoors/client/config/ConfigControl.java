package org.dimdev.dimdoors.client.config;

public interface ConfigControl<T> {
    OptionInfo<T> option();

    default String key() {
        return this.option().key();
    }

    int rowCount();

    void addRows(ControlHost host, RowCursor cursor);

    boolean validate();

    T pendingValue();

    default void commit() {
        this.option().setValue(this.pendingValue());
    }
}
