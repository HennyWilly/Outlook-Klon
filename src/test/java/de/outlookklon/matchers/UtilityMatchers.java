package de.outlookklon.matchers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import lombok.SneakyThrows;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class UtilityMatchers {

    public static Matcher isWellDefinedUtilityClass() {
        return new TypeSafeMatcher<Class>() {
            private boolean isFinal;
            private boolean onlyOneCtor;
            private boolean isConstructorPrivate;
            private Method nonStaticMethod;

            @Override
            @SneakyThrows({NoSuchMethodException.class, InstantiationException.class, IllegalAccessException.class, InvocationTargetException.class})
            protected boolean matchesSafely(Class clazz) {
                isFinal = Modifier.isFinal(clazz.getModifiers());
                if (!isFinal) {
                    return false;
                }

                onlyOneCtor = clazz.getDeclaredConstructors().length == 1;
                if (!onlyOneCtor) {
                    return false;
                }

                final Constructor<?> constructor = clazz.getDeclaredConstructor();
                isConstructorPrivate = !constructor.isAccessible() && Modifier.isPrivate(constructor.getModifiers());
                if (!isConstructorPrivate) {
                    return false;
                }

                constructor.setAccessible(true);
                constructor.newInstance();
                constructor.setAccessible(false);
                for (final Method method : clazz.getMethods()) {
                    if (!Modifier.isStatic(method.getModifiers())
                            && method.getDeclaringClass().equals(clazz)) {
                        nonStaticMethod = method;
                        return false;
                    }
                }

                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Class must be a well defined utility class ");
            }

            @Override
            protected void describeMismatchSafely(final Class item, final Description mismatchDescription) {
                if (!isFinal) {
                    mismatchDescription.appendText(" class is not final");
                } else if (!onlyOneCtor) {
                    mismatchDescription.appendText(" has not exactly one constructor");
                } else if (!isConstructorPrivate) {
                    mismatchDescription.appendText(" constructor is not private");
                } else if (nonStaticMethod != null) {
                    mismatchDescription.appendText(" method ").appendValue(nonStaticMethod).appendText(" is not static");
                }
            }
        };
    }
}
