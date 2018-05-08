# MusicalStructureApp
This is the beginning of an Android music player app; Project 4 for the Android Basics Nanodegree

There are 6 different Activites:
<ul>
<li>Main Menu</li>
<li>List of Artists</li>
<li>List of Albums</li>
<li>List of Songs</li>
<li>Album Details</li>
<li>Now Playing</li>
</ul>

Everything is dynamically generated. The songs database is stored in a JSON file which itself was generated from an Excel CSV document. I used the CSV to JSON converter here: http://www.convertcsv.com/csv-to-json.htm

Right now the app doesn't actually play music. I wanted to use real album data and make the app structure easily test-able but didn't want to illegally upload MP3s as part of the project. If I continue development I'll make the app an MP3 player which generates its own database from MP3 metadata rather than having to rely on the JSON database.
