package ftm._0xfmel.itdmtrct.utils;

import java.util.HashMap;
import java.util.function.Supplier;

public class FactoryMap<K, V> extends HashMap<K, V> {
    private final Supplier<V> factory;

    public FactoryMap(Supplier<V> factory) {
        this.factory = factory;
    }

    public V factoryGet(K key) {
        V val = super.get(key);
        if (val == null) {
            val = factory.get();
            this.put(key, val);
        }
        return val;
    }
}
