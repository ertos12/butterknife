package butterknife.internal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static butterknife.internal.InjectViewProcessor.VIEW_TYPE;

class TargetClass {
  private final Map<Integer, ViewInjection> viewIdMap = new LinkedHashMap<Integer, ViewInjection>();
  private final String classPackage;
  private final String className;
  private final String targetClass;
  private String parentInjector;

  TargetClass(String classPackage, String className, String targetClass) {
    this.classPackage = classPackage;
    this.className = className;
    this.targetClass = targetClass;
  }

  void addField(int id, String name, String type, boolean required) {
    getOrCreateViewBinding(id).addFieldBinding(new FieldBinding(name, type, required));
  }

  boolean addMethod(int id, String name, String parameterType, boolean required) {
    try {
      getOrCreateViewBinding(id).addMethodBinding(new MethodBinding(name, parameterType, required));
      return true;
    } catch (IllegalStateException e) {
      return false;
    }
  }

  void setParentInjector(String parentInjector) {
    this.parentInjector = parentInjector;
  }

  private ViewInjection getOrCreateViewBinding(int id) {
    ViewInjection viewId = viewIdMap.get(id);
    if (viewId == null) {
      viewId = new ViewInjection(id);
      viewIdMap.put(id, viewId);
    }
    return viewId;
  }

  String getFqcn() {
    return classPackage + "." + className;
  }

  String brewJava() {
    StringBuilder builder = new StringBuilder();
    builder.append("// Generated code from Butter Knife. Do not modify!\n");
    builder.append("package ").append(classPackage).append(";\n\n");
    builder.append("import android.view.View;\n");
    builder.append("import butterknife.ButterKnife.Finder;\n\n");
    builder.append("public class ").append(className).append(" {\n");
    emitInject(builder);
    builder.append('\n');
    emitReset(builder);
    builder.append("}\n");
    return builder.toString();
  }

  private void emitInject(StringBuilder builder) {
    builder.append("  public static void inject(Finder finder, final ")
        .append(targetClass)
        .append(" target, Object source) {\n");

    // Emit a call to the superclass injector, if any.
    if (parentInjector != null) {
      builder.append("    ")
          .append(parentInjector)
          .append(".inject(finder, target, source);\n\n");
    }

    // Local variable in which all views will be temporarily stored.
    builder.append("    View view;\n");

    // Loop over each view injection and emit it.
    for (ViewInjection injection : viewIdMap.values()) {
      emitViewInjection(builder, injection);
    }

    builder.append("  }\n");
  }

  private void emitViewInjection(StringBuilder builder, ViewInjection injection) {
    builder.append("    view = finder.findById(source, ")
        .append(injection.getId())
        .append(");\n");

    List<Binding> requiredBindings = injection.getRequiredBindings();
    if (!requiredBindings.isEmpty()) {
      builder.append("    if (view == null) {\n")
          .append("      throw new IllegalStateException(\"Required view with id '")
          .append(injection.getId())
          .append("' for ")
          .append(humanDescriptionJoin(requiredBindings))
          .append(" was not found. If this view is optional add '@Optional' annotation.\");\n")
          .append("    }\n");
    }

    emitFieldBindings(builder, injection);
    emitMethodBindings(builder, injection);
  }

  private void emitFieldBindings(StringBuilder builder, ViewInjection injection) {
    for (FieldBinding fieldBinding : injection.getFieldBindings()) {
      builder.append("    target.")
          .append(fieldBinding.getName())
          .append(" = ");
      emitCastIfNeeded(builder, fieldBinding.getViewType());
      builder.append("view;\n");
    }
  }

  private void emitMethodBindings(StringBuilder builder, ViewInjection injection) {
    MethodBinding methodBinding = injection.getMethodBinding();
    if (methodBinding != null) {
      List<Binding> requiredBindings = injection.getRequiredBindings();
      String extraIndent = "";

      // We only need to emit the null check if there are zero required bindings.
      if (requiredBindings.isEmpty()) {
        builder.append("    if (view != null) {\n  ");
        extraIndent = "  ";
      }

      builder.append(extraIndent)
          .append("    view.setOnClickListener(new View.OnClickListener() {\n")
          .append(extraIndent)
          .append("      @Override public void onClick(View view) {\n")
          .append(extraIndent)
          .append("        target.")
          .append(methodBinding.getName())
          .append('(');
      if (methodBinding.getViewType() != null) {
        // Only emit a cast if the type is not View.
        emitCastIfNeeded(builder, methodBinding.getViewType());
        builder.append("view");
      }
      builder.append(");\n")
          .append(extraIndent)
          .append("      }\n")
          .append(extraIndent)
          .append("    });\n");
      if (requiredBindings.isEmpty()) {
        builder.append("    }\n");
      }
    }
  }

  private void emitReset(StringBuilder builder) {
    builder.append("  public static void reset(").append(targetClass).append(" target) {\n");
    if (parentInjector != null) {
      builder.append("    ")
          .append(parentInjector)
          .append(".reset(target);\n\n");
    }
    for (ViewInjection injection : viewIdMap.values()) {
      for (FieldBinding fieldBinding : injection.getFieldBindings()) {
        builder.append("    target.").append(fieldBinding.getName()).append(" = null;\n");
      }
    }
    builder.append("  }\n");
  }

  static void emitCastIfNeeded(StringBuilder builder, String viewType) {
    // Only emit a cast if the type is not View.
    if (!VIEW_TYPE.equals(viewType)) {
      builder.append("(").append(viewType).append(") ");
    }
  }

  static String humanDescriptionJoin(List<Binding> bindings) {
    switch (bindings.size()) {
      case 1:
        return bindings.get(0).getDescription();
      case 2:
        return bindings.get(0).getDescription() + " and " + bindings.get(1).getDescription();
      default:
        StringBuilder names = new StringBuilder();
        for (int i = 0, count = bindings.size(); i < count; i++) {
          Binding requiredField = bindings.get(i);
          if (i != 0) {
            names.append(", ");
          }
          if (i == count - 1) {
            names.append("and ");
          }
          names.append(requiredField.getDescription());
        }
        return names.toString();
    }
  }
}
