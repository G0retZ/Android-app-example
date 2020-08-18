# App-code-example

This is an example of the Android app with the common master/details problem.
Written with TDD and handmade DI.
Can be used as a template for new projects providing restricting architectural approach, yet easy to adopt big changes.
*Overall this results in bugless apps cheap in maintenance.*

<img src="https://github.com/G0retZ/Android-app-example/blob/master/ui-design.png" width="600" />

### Acceptence Criterea:
1.	All elements from the design should be visible on the screen for a user. If the feature is not implemented, show "The feature is in progress. Stay tuned"! toast.
2.	Show list of locations with a given design
3.	Implement selection of a location
4.	If any location selected, show the Accept button. Hide the button if a location deselected (clicked again or is not selected yet). Note: these screens are part of 6-screen flow, and every screen has a similar button.
5.	On click on Accept button, transition to the next screen. The new screen should display the selected location name and city in a simple TextView
6.	Get directions button should open Maps app showing the direction
7.	"Close" button closes the first screen or navigates back on the second
8.	Nearby button should be displayed, but on click it should just log the click and show a message "The feature is in progress. Stay tuned"!
9.	The accept button should appear and disappear with sliding animation from the bottom of the screen. The button should appear when smth just got selected and disappear if the selection gone or before moving to the next or previous screen.
10.	The screens should be changed with sliding animation
11.	The Accept button on the second screen should be always shown and just do nothing
12.	Everything else that you consider part of "production-ready" app
