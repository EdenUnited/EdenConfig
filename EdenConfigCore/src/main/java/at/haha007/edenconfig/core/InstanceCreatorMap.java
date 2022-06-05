package at.haha007.edenconfig.core;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class InstanceCreatorMap<C> implements Collection<InstanceCreator<C, ?>>, Cloneable {
    Map<Class<?>, InstanceCreator<C, ?>> map = new HashMap<>();


    public boolean containsKey(Class<?> key) {
        return map.containsKey(key);
    }

    public <K> InstanceCreator<C, K> get(Class<K> key) {
        //noinspection unchecked
        return (InstanceCreator<C, K>) map.get(key);
    }


    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean contains(Object o) {
        if (!(o instanceof InstanceCreator creator))
            return false;
        return map.containsValue(creator);
    }

    @NotNull
    public Iterator<InstanceCreator<C, ?>> iterator() {
        return map.values().iterator();
    }


    public Object @NotNull [] toArray() {
        return map.values().toArray();
    }

    public <T> T @NotNull [] toArray(T @NotNull [] a) {
        //noinspection SuspiciousToArrayCall
        return map.values().toArray(a);
    }

    public boolean add(InstanceCreator<C, ?> creator) {
        return map.put(creator.getType(), creator) != creator;
    }

    public boolean remove(Object o) {
        if (!(o instanceof InstanceCreator creator))
            return false;
        return map.remove(creator.getType()) != null;
    }

    public boolean containsAll(@NotNull Collection<?> c) {
        return map.values().containsAll(c);
    }

    public boolean addAll(@NotNull Collection<? extends InstanceCreator<C, ?>> c) {
        boolean anyMatch = false;
        for (InstanceCreator<C, ?> instanceCreator : c) {
            if (add(instanceCreator))
                anyMatch = true;
        }
        return anyMatch;
    }

    public boolean removeAll(@NotNull Collection<?> c) {
        return map.values().removeAll(c);
    }

    public boolean retainAll(@NotNull Collection<?> c) {
        return map.values().retainAll(c);
    }

    public void clear() {
        map.clear();
    }

    public InstanceCreatorMap<C> clone() {
        try {
            //noinspection unchecked
            InstanceCreatorMap<C> map = (InstanceCreatorMap<C>) super.clone();
            map.map = new HashMap<>();
            map.addAll(this);
            return map;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
