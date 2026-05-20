package org.dimdev.dimdoors.fabric.mixin;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.impl.attachment.AttachmentTypeImpl;
import org.dimdev.dimdoors.world.fray.DataValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.UnaryOperator;

@Mixin(AttachmentTypeImpl.class)
@SuppressWarnings("unchecked")
public abstract class DataAttachmentMixin<T> implements DataValue<T> {
    @Override
    public T get(Object object) {
        var target = asAttachmentTarget(object);
        return target == null ? null : target.getAttached(self());
    }

    @Override
    public T getOrCreate(Object object) {
        var target = asAttachmentTarget(object);
        return target == null ? null : target.getAttachedOrCreate(self());
    }

    @Override
    public void set(Object object, T value) {
        var target = asAttachmentTarget(object);

        if (target != null) {
            target.setAttached(self(), value);
        }
    }

    @Override
    public void update(Object object, T defaultValue, UnaryOperator<T> operator) {
        var target = asAttachmentTarget(object);

        if (target != null) {
            T data = target.getAttached(self());
            target.setAttached(self(), operator.apply(data == null ? defaultValue : data));
        }
    }

    @Override
    public void update(Object object, UnaryOperator<T> operator) {
        var target = asAttachmentTarget(object);

        if (target != null) {
            target.setAttached(self(), operator.apply(target.getAttachedOrCreate(self())));
        }
    }

    @Override
    public void remove(Object object) {
        var target = asAttachmentTarget(object);

        if (target != null) {
            target.removeAttached(self());
        }
    }

    @Override
    public boolean has(Object object) {
        var target = asAttachmentTarget(object);
        return target != null && target.hasAttached(self());
    }

    @Unique
    private AttachmentType<T> self() {
        return (AttachmentType<T>) (Object) this;
    }

    @Unique
    private AttachmentTarget asAttachmentTarget(Object object) {
        return object instanceof AttachmentTarget target ? target : null;
    }
}
