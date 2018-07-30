package net.vpc.app.vr.core.toolbox;

public class ValidatorFactory {
    public static StringValidator ID=new StringValidator() {
        @Override
        public boolean validate(String value) {
            return true;
        }
    };
}
