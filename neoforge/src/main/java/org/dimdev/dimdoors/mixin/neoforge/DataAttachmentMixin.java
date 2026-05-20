package org.dimdev.dimdoors.mixin.neoforge;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import org.dimdev.dimdoors.world.fray.DataValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.UnaryOperator;

@Mixin(AttachmentType.class)
@SuppressWarnings("unchecked")
public abstract class DataAttachmentMixin<T> implements DataValue<T> {
    @Override
    public T get(Object object) {
        var holder = asAttachmentHolder(object);
        return holder == null ? null : holder.getExistingDataOrNull(self());
    }

    @Override
    public T getOrCreate(Object object) {
        var holder = asAttachmentHolder(object);
        return holder == null ? null : holder.getData(self());
    }

    @Override
    public void set(Object object, T value) {
        var holder = asAttachmentHolder(object);

        if (holder != null) {
            set(holder, value);
        }
    }

    @Override
    public void update(Object object, T defaultValue, UnaryOperator<T> operator) {
        var holder = asAttachmentHolder(object);

        if (holder != null) {
            T data = holder.getExistingDataOrNull(self());
            set(holder, operator.apply(data == null ? defaultValue : data));
        }
    }

    @Override
    public void update(Object object, UnaryOperator<T> operator) {
        var holder = asAttachmentHolder(object);

        if (holder != null) {
            set(holder, operator.apply(holder.getData(self())));
        }
    }

    @Override
    public void remove(Object object) {
        var holder = asAttachmentHolder(object);

        if (holder != null) {
            holder.removeData(self());
        }
    }

    @Override
    public boolean has(Object object) {
        var holder = asAttachmentHolder(object);
        return holder != null && holder.hasData(self());
    }

    @Unique
    private AttachmentType<T> self() {
        return (AttachmentType<T>) (Object) this;
    }

    @Unique
    private void set(IAttachmentHolder holder, T value) {
        if (value == null) {
            holder.removeData(self());
        } else {
            holder.setData(self(), value);
        }
    }

    @Unique
    private IAttachmentHolder asAttachmentHolder(Object object) {
        return object instanceof IAttachmentHolder holder ? holder : null;
    }
}
