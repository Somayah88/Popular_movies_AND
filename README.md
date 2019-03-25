
## Project Overview
 I built this app as a part of Udacity Android Developer Nanodegree. This app allows users to discover the most popular movies playing. It fetches the data from TheMovieDB api.
 
 ## Features
- Present the user with a grid arrangement of movie posters upon launch.
- Allow your user to change sort order via a setting:
The sort order can be by most popular, highest-rated or the user favorites
- Allow the user to tap on a movie poster and transition to a details screen with additional information such as:
  - original title
  - movie poster image thumbnail
  - A plot synopsis (called overview in the api)
  - user rating (called vote_average in the api)
  - release date
  - trailers which users can view in the youtube app 
  - reviews.
- Users can mark a movie as a favorite in the details view by tapping a button(heart).



 **In order to use your API key please create a resource file under the values folder and insert the following code:** 
 
` <?xml version="1.0" encoding="utf-8"?>
`  
`  <resources>
`<`string name="api_key">`[ YOUR API KEY ]`</string>`
`  </resources>`

