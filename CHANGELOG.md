Change Log
==========

Version 4.0.0 *(In Development)*
--------------------------------

`Views` class is now named `ButterKnife`

 * New: Views are now only checked to be `null` once if at least one of the fields and/or methods
   lack the `@Optional` annotation.
 * Fix: Do no emit redundant casts to `View` for methods.


Version 3.0.1 *(2013-11-12)*
----------------------------

 * Fix: Do not emit redundant casts to `View`.


Version 3.0.0 *(2013-09-10)*
----------------------------

 * New: Injections are now required. An exception will be thrown if a view is
   not found. Add `@Optional` annotation to suppress this verification.


Version 2.0.1 *(2013-07-18)*
----------------------------

 * New: Control debug logging via `Views.setDebug`.


Version 2.0.0 *(2013-07-16)*
----------------------------

 * New: `@OnClick` annotation for binding click listeners to methods!


Version 1.4.0 *(2013-06-03)*
----------------------------

 * New: `Views.reset` for settings injections back to `null` in a fragment's
   `onDestroyView` callback.
 * Fix: Support parent class injection when the parent class has generics.


Version 1.3.2 *(2013-04-27)*
----------------------------

 * Multiple injections of the same view ID only require a single find call.
 * Fix: Ensure injection happens on classes who do not have any injections but
   their superclasses do.


Version 1.3.1 *(2013-04-12)*
----------------------------

 * Fix: Parent class inflater resolution now generates correct code.


Version 1.3.0 *(2013-03-26)*
----------------------------

 * New: Injection on objects that have zero `@InjectView`-annotated fields will
   no longer throw an exception.


Version 1.2.2 *(2013-03-11)*
----------------------------

 * Fix: Prevent annotations on private classes.


Version 1.2.1 *(2013-03-11)*
----------------------------

 * Fix: Correct generated code for parent class inflation.
 * Fix: Allow injection on `protected`-scoped fields.


Version 1.2.0 *(2013-05-07)*
----------------------------

 * Support injection on any object using an Activity as the view root.
 * Support injection on views for their children.
 * Fix: Annotation errors now appear on the affected field in IDEs.


Version 1.1.1 *(2013-05-06)*
----------------------------

 * Fix: Verify that the target type extends from `View`.
 * Fix: Correct package name resolution in Eclipse 4.2


Version 1.1.0 *(2013-03-05)*
----------------------------

 * Perform injection on any object by passing a view root.
 * Fix: Correct naming of static inner-class injection points.
 * Fix: Enforce `findById` can only be used with child classes of `View`.


Version 1.0.0 *(2013-03-05)*
----------------------------

Initial release.
