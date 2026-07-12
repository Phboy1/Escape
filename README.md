# Escape

Welcome to "Escape", a tile-based escape game.

This project was made for #Horizions.

# What is it?

The mechanics are simple. You move through a level with discrete movement and you must collect cookies based on the size of your level. Additionally, the time is also based on the size of your level. If you don't collect enough cookies and ESCAPE, you die, if you run out of time, you DIE. There are cookies and freezes. Freezes stop time and enemies for 3 seconds.

# How It Works?

The level is a 2D Array List that is compatible for any size game. By using '_' or 'x' or '1' or '2' or 'p', any type of level and be generated simply based on a text file. In the future, this could also be beneficial when having multiple levels.

## Player Movement

The game uses a 2D Array List as said earlier and it contains a character 'p'. Then whenever I try pressing WASD or Arrowkeys, I check the indice in that direction and check if it isnt a 'x'. If so, then I can move in that direction. CHEEKY TRICK: When designing levels make sure to have a box around the level so you don't get an ArrayOutOfBoundsException. 

## Enemy Movement

There are 2 types of enemies, vertical moving and horizontal moving enemies. With those type of enemies I have 4 parallel arraylists that track one unique property of the enemy whether that be enemyX, enemyY, enemyDir, or enemyType. Then for movement, I simply check if the index infront of the enemy is an 'x', if so they I reverse the enemyDir and continue.

## Cookie Spawning && Freeze Spawning

I use a do while loop. This is TECHNICALLY RISKY, since cookies can infinitely spawn in places with 'x', enemies or players, therefore, after 100 attempts, it simply puts it in the first spot there is an empty space to ensure the game does not crash.

## Timer

I used nanotime to ensure that the timing was as accurate as possible.

## Freezing

When freezing occurs I start a 3 second timer to stop the game timer from moving. Additonally, I use the "continue" key word to ensure enemies don't move. I also add a blue hue over the game to ensure the player knows everything has been frozen. Finally, in the last 1.5 seconds there is an UNFREEZING flashing warning to ensure the player knows.

# Motivation

I wanted an escape game that used discrete movement and tracked EVERYTHING inside of a level grid. This is also highly dynamic for any level and using 5% of the level size for the amount of time in seconds and 2% of the level size for the amount of cookies needed, it makes the game dynamic for any level. You can also have jagged levels as well.

# Controls

WASD or Arrow Keys - Movement
SPACE or R - Restart
ENTER - Begin the Game

# Screenshots from Games