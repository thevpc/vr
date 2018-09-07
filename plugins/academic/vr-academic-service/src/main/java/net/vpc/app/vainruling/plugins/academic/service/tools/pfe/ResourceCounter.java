package net.vpc.app.vainruling.plugins.academic.service.tools.pfe;

class ResourceCounter {
    private String person;
    private String type;
    private double value;

    public ResourceCounter(String person, String type) {
        this.person = person;
        this.type = type;
    }

    public void inc() {
        value++;
    }

    public void set(double v) {
        value = v;
    }

    public void add(double v) {
        value += v;
    }

    public String getPerson() {
        return person;
    }

    public String getType() {
        return type;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
