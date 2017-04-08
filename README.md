# TMButton

Checkable ImageButton that animates between states.

A custom image view with two states: checked and unchecked. Each state is represented with two
colors, unchecked and checked color. When the view is clicked, the state changes automatically with a scale and color animation.

### XML attributes
* ```icon_drawable```: drawable resource for the icon
* ```color_unchecked```: color of the unchecked state, this is the default value
* ```color_checked```: color of the checked state

### Note:

Make sure to call ```android:clipChildren="false"``` on the parent layout, otherwise the scaling animation may be clipped.

