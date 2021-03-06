package butterknife.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static butterknife.internal.ProcessorTestUtilities.butterknifeProcessors;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.truth0.Truth.ASSERT;

public class InjectViewTest {
  @Test public void injectingView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.InjectView;",
        "public class Test extends Activity {",
        "    @InjectView(1) View thing;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "public class Test$$ViewInjector {",
            "  public static void inject(Finder finder, final test.Test target, Object source) {",
            "    View view;",
            "    view = finder.findById(source, 1);",
            "    if (view == null) {",
            "      throw new IllegalStateException(\"Required view with id '1' for field 'thing' was not found. If this view is optional add '@Optional' annotation.\");",
            "    }",
            "    target.thing = view;",
            "  }",
            "  public static void reset(test.Test target) {",
            "    target.thing = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void oneFindPerId() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.InjectView;",
        "public class Test extends Activity {",
        "  @InjectView(1) View thing1;",
        "  @InjectView(1) View thing2;",
        "  @InjectView(1) View thing3;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "public class Test$$ViewInjector {",
            "  public static void inject(Finder finder, final test.Test target, Object source) {",
            "    View view;",
            "    view = finder.findById(source, 1);",
            "    if (view == null) {",
            "      throw new IllegalStateException(\"Required view with id '1' for field 'thing1', field 'thing2', and field 'thing3' was not found. If this view is optional add '@Optional' annotation.\");",
            "    }",
            "    target.thing1 = view;",
            "    target.thing2 = view;",
            "    target.thing3 = view;",
            "  }",
            "  public static void reset(test.Test target) {",
            "    target.thing1 = null;",
            "    target.thing2 = null;",
            "    target.thing3 = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void fieldVisibility() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.InjectView;",
        "public class Test extends Activity {",
        "  @InjectView(1) public View thing1;",
        "  @InjectView(1) View thing2;",
        "  @InjectView(1) protected View thing3;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError();
  }

  @Test public void optional() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.InjectView;",
        "import butterknife.Optional;",
        "public class Test extends Activity {",
        "  @Optional @InjectView(1) View view;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "public class Test$$ViewInjector {",
            "  public static void inject(Finder finder, final test.Test target, Object source) {",
            "    View view;",
            "    view = finder.findById(source, 1);",
            "    target.view = view;",
            "  }",
            "  public static void reset(test.Test target) {",
            "    target.view = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void superclass() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.InjectView;",
        "import butterknife.Optional;",
        "public class Test extends Activity {",
        "  @InjectView(1) View view;",
        "}",
        "class TestOne extends Test {",
        "  @InjectView(1) View thing;",
        "}",
        "class TestTwo extends Test {",
        "}"
    ));

    JavaFileObject expectedSource1 = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "public class Test$$ViewInjector {",
            "  public static void inject(Finder finder, final test.Test target, Object source) {",
            "    View view;",
            "    view = finder.findById(source, 1);",
            "    if (view == null) {",
            "      throw new IllegalStateException(\"Required view with id '1' for field 'view' was not found. If this view is optional add '@Optional' annotation.\");",
            "    }",
            "    target.view = view;",
            "  }",
            "  public static void reset(test.Test target) {",
            "    target.view = null;",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test/TestOne$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "public class TestOne$$ViewInjector {",
            "  public static void inject(Finder finder, final test.TestOne target, Object source) {",
            "    test.Test$$ViewInjector.inject(finder, target, source);",
            "    View view;",
            "    view = finder.findById(source, 1);",
            "    if (view == null) {",
            "      throw new IllegalStateException(\"Required view with id '1' for field 'thing' was not found. If this view is optional add '@Optional' annotation.\");",
            "    }",
            "    target.thing = view;",
            "  }",
            "  public static void reset(test.TestOne target) {",
            "    test.Test$$ViewInjector.reset(target);",
            "    target.thing = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource1, expectedSource2);
  }

  @Test public void genericSuperclass() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.InjectView;",
        "import butterknife.Optional;",
        "public class Test<T> extends Activity {",
        "  @InjectView(1) View view;",
        "}",
        "class TestOne extends Test<String> {",
        "  @InjectView(1) View thing;",
        "}",
        "class TestTwo extends Test<Object> {",
        "}"
    ));

    JavaFileObject expectedSource1 = JavaFileObjects.forSourceString("test/Test$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "public class Test$$ViewInjector {",
            "  public static void inject(Finder finder, final test.Test target, Object source) {",
            "    View view;",
            "    view = finder.findById(source, 1);",
            "    if (view == null) {",
            "      throw new IllegalStateException(\"Required view with id '1' for field 'view' was not found. If this view is optional add '@Optional' annotation.\");",
            "    }",
            "    target.view = view;",
            "  }",
            "  public static void reset(test.Test target) {",
            "    target.view = null;",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource2 = JavaFileObjects.forSourceString("test/TestOne$$ViewInjector",
        Joiner.on('\n').join(
            "package test;",
            "import android.view.View;",
            "import butterknife.ButterKnife.Finder;",
            "public class TestOne$$ViewInjector {",
            "  public static void inject(Finder finder, final test.TestOne target, Object source) {",
            "    test.Test$$ViewInjector.inject(finder, target, source);",
            "    View view;",
            "    view = finder.findById(source, 1);",
            "    if (view == null) {",
            "      throw new IllegalStateException(\"Required view with id '1' for field 'thing' was not found. If this view is optional add '@Optional' annotation.\");",
            "    }",
            "    target.thing = view;",
            "  }",
            "  public static void reset(test.TestOne target) {",
            "    test.Test$$ViewInjector.reset(target);",
            "    target.thing = null;",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource1, expectedSource2);
  }

  @Test public void failsIfInPrivateClass() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.view.View;",
        "import butterknife.InjectView;",
        "public class Test {",
        "  private static class Inner {",
        "    @InjectView(1) View thing;",
        "  }",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(
            String.format("@InjectView fields may not be on private classes (%s).",
                "test.Test.Inner"))
        .in(source).onLine(6);
  }

  @Test public void failsIfNotView() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.InjectView;",
        "public class Test extends Activity {",
        "  @InjectView(1) String thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(
            String.format("@InjectView fields must extend from View (%s).",
                "test.Test.thing"))
        .in(source).onLine(5);
  }

  @Test public void failsIfInInterface() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.view.View;",
        "import butterknife.InjectView;",
        "public interface Test {",
        "    @InjectView(1) View thing = null;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(
            String.format("@InjectView field annotations may only be specified in " +
                "classes (%s).",
                "test.Test"))
        .in(source).onLine(5);
  }

  @Test public void failsIfPrivate() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.InjectView;",
        "public class Test extends Activity {",
        "    @InjectView(1) private View thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(
            String.format("@InjectView fields must not be private or static (%s).",
                "test.Test.thing"))
        .in(source).onLine(6);
  }

  @Test public void failsIfStatic() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.view.View;",
        "import butterknife.InjectView;",
        "public class Test extends Activity {",
        "    @InjectView(1) static View thing;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining(
            String.format("@InjectView fields must not be private or static (%s).",
                "test.Test.thing"))
        .in(source).onLine(6);
  }
}
