package net.vpc.app.vainruling.core.service.stats;

/**
 * Created by vpc on 8/29/16.
 */
public class StringArrayKPIGroup implements KPIGroup {
    private String name;
    private String key;
    private Object[] keys;
    private Object value;

    public StringArrayKPIGroup(String name, Object value, Object... keys) {
        this.name = name;
        this.keys = keys;
        this.value = value;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.length; i++) {
            Object key = keys[i];
            String str = String.valueOf(key);
            if (str.contains(":")) {
                throw new RuntimeException(": is not allowed in " + key);
            }
            if (i > 0) {
                sb.append(":");
            }
            sb.append(str);
        }
        key = sb.toString();
    }

    @Override
    public int compareTo(KPIGroup o) {
        return name.compareTo(o.getName());
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(KPIGroup other) {
        return equals((Object) other);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StringArrayKPIGroup that = (StringArrayKPIGroup) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return key != null ? key.equals(that.key) : that.key == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (key != null ? key.hashCode() : 0);
        return result;
    }
}
