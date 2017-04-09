# TMButton

Checkable ImageButton that animates between states.

A custom image view with two states: checked and unchecked. Each state is represented with two
colors, unchecked and checked color. When the view is clicked, the state changes automatically with a scale and color animation.

[![Sample](https://j.gifs.com/y8W0QE.gif)](https://youtu.be/QQ6HaaveZ7o)

### Import

Add to root-level build.gradle:

```
allprojects {
  repositories {
    // ...
    maven { url  "http://dl.bintray.com/ricardobelchior/android" }
  }
}
```

Add to your project dependencies:

```
dependencies {
  // ...
  compile 'com.github.ricardobelchior:tmbutton:1.0.0'
}
```


### Example usage

```
    <com.rbelchior.tmbutton.TMButton
        android:layout_width="64dp"
        android:layout_height="64dp"
        app:color_checked="#bb0000"
        app:color_unchecked="#aaaaaa"
        app:icon_drawable="@drawable/ic_whatshot_black_24dp"
```

XML attributes:

* ```icon_drawable```: drawable resource for the icon
* ```color_unchecked```: color of the unchecked state, this is the default value
* ```color_checked```: color of the checked state

### Note:

Make sure to call ```android:clipChildren="false"``` on the parent layout, otherwise the scaling animation may be clipped.

