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

There are 2 types of enemies, vertical moving and horizontal moving enemies. With those type of enemies I have 4 parallel arraylists that track one unique property of the enemy whether that be enemyX, enemyY, enemyDir, or enemyType

# Motivation

# Controls

# Screenshots from Games