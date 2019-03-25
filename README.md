
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

## Screenshots

<img src="https://user-images.githubusercontent.com/10868955/54937214-5c5e8680-4efa-11e9-9433-dc2224e4e894.png" width="250"> <img src="https://user-images.githubusercontent.com/10868955/54937891-b90e7100-4efb-11e9-8cad-1da9c1057d07.png" width="250"> <img src="https://user-images.githubusercontent.com/10868955/54938185-481b8900-4efc-11e9-9e0c-15e04dc1cc5e.png" width="250"> 

## Landscape mode 
<img src="https://user-images.githubusercontent.com/10868955/54938052-04288400-4efc-11e9-8710-bd5350b061b9.png" height="250">

## What I Learned from this project?
 - I fetched data from the Internet with theMovieDB API.
 - I used adapters and custom list layouts to populate list views.
 - I used libraries to simplify the amount of code I wrote

 **In order to use your API key please create a resource file under the values folder and insert the following code:** 
 ```<?xml version="1.0" encoding="utf-8"?> 
    <resources>
   <string name="api_key">[ YOUR API KEY ]</string>
   </resources>

